package xyz.larkzhh.echomusic.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_READY
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import xyz.larkzhh.echomusic.EchoApplication
import xyz.larkzhh.echomusic.core.MusicPlayController
import xyz.larkzhh.echomusic.core.UserSpace
import xyz.larkzhh.echomusic.core.player.MediaCache
import xyz.larkzhh.echomusic.core.viewstate.BaseViewStateViewModel
import xyz.larkzhh.echomusic.core.viewstate.paging.AppPagingConfig
import xyz.larkzhh.echomusic.core.viewstate.paging.buildPager
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.model.MvAllItem
import xyz.larkzhh.echomusic.model.MvDetailData
import xyz.larkzhh.echomusic.model.Subscribers
import xyz.larkzhh.echomusic.model.Video
import xyz.larkzhh.echomusic.model.VideoUrlBean
import xyz.larkzhh.echomusic.utils.NetworkUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/** 视频互动数据 */
data class VideoStats(
    val commentCount: Int,
    val shareCount: Int,
    val praisedCount: Int,
)


@HiltViewModel
class VideoPlayViewModel @Inject constructor(private val api: NeteaseApi) : BaseViewStateViewModel() {

    // 视频列表数据流
    var videoFlows: Flow<PagingData<MvAllItem>>? = null

    // 视频列表集合
    var videoPagingItems: LazyPagingItems<MvAllItem>? = null

    private val mvUrlCache = mutableMapOf<String, VideoUrlBean>()
    private val mvAreaList = listOf("全部", "内地", "港台", "欧美", "日本", "韩国")

    // 当前播放的视频url
    var curVideoUrl by mutableStateOf<String?>(null)

    // 当前播放的视频 id
    var curVideoId by mutableStateOf<String?>(null)

    // 当前列表位置
    var curVideoIndex by mutableStateOf(0)

    // 列表视频的互动数据，分页数据里没有这些字段
    var videoStats by mutableStateOf<Map<String, VideoStats>>(emptyMap())
        private set

    // 播放器状态
    var exoPlayStatus by mutableStateOf(Player.STATE_IDLE)

    // 当前视频是否在播放中
    var videoPlaying by mutableStateOf(false)

    // 是否显示视频信息
    var showVideoInfo by mutableStateOf(true)

    // 是否显示视频信息
    var videoInfoAlpha by mutableStateOf(1f)

    // 视频进度
    var videoProgress by mutableStateOf(0)

    // 第一个视频
    lateinit var firstVideo: Video

    // 播放器池：一个在播，另一个预加载下一条；上一条只预热缓存
    private var players: Array<ExoPlayer>? = null
    private var activePlayerIndex = 0
    private val playerVids = arrayOfNulls<String>(2)

    val exoPlayer: ExoPlayer
        get() = requireNotNull(players)[activePlayerIndex]

    private fun idlePlayers(): List<Int> =
        players?.indices?.filter { it != activePlayerIndex }.orEmpty()

    private fun playerHolding(vid: String?): Int? {
        if (vid == null) {
            return null
        }
        return players?.indices?.firstOrNull {
            it != activePlayerIndex &&
                playerVids[it] == vid &&
                players?.get(it)?.playbackState != Player.STATE_IDLE
        }
    }

    // 上一条缓存预热任务
    private val warmJobs = mutableMapOf<String, Job>()
    private val PRELOAD_BYTES = 4L * 1024 * 1024

    // 是否拖动进度条中
    private var seeking = false

    private val mTimer: Timer = Timer()
    private var mUpdateDuringTask: TimerTask? = null
    private var mHandler = Handler(Looper.getMainLooper())

    private fun buildListener(player: ExoPlayer) = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            if (player !== players?.getOrNull(activePlayerIndex)) {
                return
            }
            exoPlayStatus = playbackState
            if (exoPlayStatus == STATE_READY && MusicPlayController.isPlaying()) {
                MusicPlayController.pause()
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (player !== players?.getOrNull(activePlayerIndex)) {
                return
            }
            videoPlaying = isPlaying
            if (isPlaying) {
                startUpdateDuringTask()
            } else {
                mUpdateDuringTask?.cancel()
            }
        }
    }

    fun seeking(progress: Int) {
        seeking = true
        videoProgress = progress
    }

    fun seekTo(progress: Int) {
        seeking = false
        val curPosition = exoPlayer.duration * progress / 100
        exoPlayer.seekTo(curPosition)
    }

    /**
     * 初始化播放器池
     */
    fun initExoPlayerIfNeeded(context: Context) {
        if (players == null) {
            players = Array(2) { index ->
                buildPlayer(context).also { player ->
                    player.addListener(buildListener(player))
                    playerVids[index] = null
                }
            }
        }
    }

    /** 解码器申请失败时允许回退，避免低端机满实例直接黑屏 */
    @OptIn(UnstableApi::class)
    private fun buildPlayer(context: Context): ExoPlayer {
        val renderersFactory = DefaultRenderersFactory(context).setEnableDecoderFallback(true)
        return ExoPlayer.Builder(context, renderersFactory).build()
    }

    /**
     * 暂停视频播放
     */
    fun pauseVideo() {
        curVideoUrl?.run {
            exoPlayer.pause()
            mUpdateDuringTask?.cancel()
        }
    }

    /**
     * 恢复视频播放
     */
    fun resumeVideo() {
        curVideoUrl?.run {
            exoPlayer.play()
            startUpdateDuringTask()
        }
    }

    /**
     * 停止视频播放
     */
    private fun stopVideo() {
        videoProgress = 0
        curVideoUrl?.run {
            exoPlayer.stop()
            mUpdateDuringTask?.cancel()
        }
    }

    /**
     * 释放
     */
    fun release() {
        players?.forEach { player ->
            player.stop()
            player.release()
        }
        players = null
    }

    /**
     * 加载视频：空闲播放器已预加载好就直接交换
     */
    fun loadVideo(url: String) {
        val vid = curVideoId
        val prepared = playerHolding(vid)
        if (vid != null && prepared != null) {
            players?.getOrNull(activePlayerIndex)?.pause()
            activePlayerIndex = prepared
            videoProgress = 0
            exoPlayStatus = players?.get(prepared)?.playbackState ?: Player.STATE_IDLE
            players?.get(prepared)?.apply {
                playWhenReady = true
                play()
            }
            prepareAround()
            return
        }

        val player = exoPlayer
        player.stop()
        player.setMediaSource(buildMediaSource(url, vid))
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.playWhenReady = true
        player.prepare()
        playerVids[activePlayerIndex] = vid
        prepareAround()
    }

    @OptIn(UnstableApi::class)
    private fun buildMediaSource(url: String, vid: String?): ProgressiveMediaSource {
        // 缓存键用视频 id，避免地址带签名变化后缓存失效
        val cacheKey = "${UserSpace.id}:video:${vid ?: url}"
        val dataSourceFactory = CacheDataSource.Factory()
            .setCache(MediaCache.video(EchoApplication.context))
            .setUpstreamDataSourceFactory(
                DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
            )
            .setCacheKeyFactory { cacheKey }
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        // 移动网络下只读缓存，不写入新数据
        if (!NetworkUtil.isWifi(EchoApplication.context)) {
            dataSourceFactory.setCacheWriteDataSinkFactory(null)
        }

        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(url)))
    }

    /**
     * 下一条用空闲播放器准备，上一条只预热缓存
     */
    private fun prepareAround() {
        releaseStalePlayers()
        prepareNeighbor(curVideoIndex + 1)
        warmNeighbor(curVideoIndex - 1)
    }

    @OptIn(UnstableApi::class)
    private fun prepareNeighbor(displayIndex: Int) {
        if (displayIndex < 0) {
            return
        }
        val vid = vidAt(displayIndex) ?: return
        if (playerHolding(vid) != null) {
            return
        }
        val url = urlAt(displayIndex) ?: run {
            getMvUrlById(vid, isPreLoad = true)
            return
        }
        val slot = idlePlayers().firstOrNull { playerVids[it] == null } ?: return
        val player = players?.get(slot) ?: return
        player.stop()
        player.setMediaSource(buildMediaSource(url, vid))
        player.playWhenReady = false
        player.prepare()
        playerVids[slot] = vid
    }

    /** 上一条只把字节写进缓存，不占用解码器 */
    private fun warmNeighbor(displayIndex: Int) {
        if (displayIndex < 0) {
            return
        }
        val vid = vidAt(displayIndex) ?: return
        val url = urlAt(displayIndex) ?: run {
            getMvUrlById(vid, isPreLoad = true)
            return
        }
        warmCache(vid, url)
    }

    @OptIn(UnstableApi::class)
    private fun warmCache(vid: String, url: String) {
        // 只在 Wi-Fi 预热，避免消耗流量
        if (!NetworkUtil.isWifi(EchoApplication.context) || warmJobs.containsKey(vid)) {
            return
        }
        warmJobs[vid] = viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val cacheKey = "${UserSpace.id}:video:$vid"
                val dataSource = CacheDataSource.Factory()
                    .setCache(MediaCache.video(EchoApplication.context))
                    .setUpstreamDataSourceFactory(
                        DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
                    )
                    .setCacheKeyFactory { cacheKey }
                    .createDataSource()
                val spec = DataSpec.Builder()
                    .setUri(Uri.parse(url))
                    .setLength(PRELOAD_BYTES)
                    .build()
                dataSource.open(spec)
                val buffer = ByteArray(64 * 1024)
                var total = 0L
                while (total < PRELOAD_BYTES) {
                    val read = dataSource.read(buffer, 0, buffer.size)
                    if (read == C.RESULT_END_OF_INPUT) {
                        break
                    }
                    total += read
                }
                dataSource.close()
            }
        }
    }

    /** 停掉不再是相邻项的空闲播放器 */
    private fun releaseStalePlayers() {
        val keep = setOf(
            vidAt(curVideoIndex) ?: curVideoId,
            vidAt(curVideoIndex + 1),
            vidAt(curVideoIndex - 1),
        )
        idlePlayers().forEach { index ->
            val vid = playerVids[index] ?: return@forEach
            if (vid !in keep) {
                players?.get(index)?.stop()
                playerVids[index] = null
            }
        }
    }

    private fun vidAt(displayIndex: Int): String? = when {
        displayIndex == 0 -> firstVideo.vid
        displayIndex > 0 -> videoPagingItems?.itemSnapshotList?.getOrNull(displayIndex - 1)?.id?.toString()
        else -> null
    }

    private fun urlAt(displayIndex: Int): String? {
        val vid = vidAt(displayIndex) ?: return null
        if (displayIndex == 0) {
            firstVideo.urls?.firstOrNull()?.url?.let { if (it.isNotBlank()) return it }
        }
        return mvUrlCache[vid]?.url
    }

    /**
     * 切换视频播放地址
     */
    fun switchVideoUrl(newIndex: Int) {
        stopVideo()
        curVideoIndex = newIndex
        if (newIndex > 0) {
            val curMv = videoPagingItems?.itemSnapshotList?.getOrNull(newIndex - 1)
            val mvid = curMv?.id?.toString()
            curVideoId = mvid
            val cached = if (mvid.isNullOrBlank()) null else mvUrlCache[mvid]
            if (cached != null) {
                curVideoUrl = cached.url
            } else {
                getMvUrlById(mvid, isPreLoad = false)
            }
        } else {
            curVideoId = firstVideo.vid
            val firstUrl = firstVideo.urls?.firstOrNull()?.url
            if (!firstUrl.isNullOrBlank()) {
                curVideoUrl = firstUrl
            } else {
                getMvUrlById(firstVideo.vid, isPreLoad = false)
            }
        }
        prefetchAround(newIndex)
    }

    /**
     * 列表项渲染时确保当前项的地址已就绪
     */
    fun ensureVideoUrl(index: Int, vid: String?) {
        if (vid.isNullOrBlank()) {
            return
        }
        if (index == curVideoIndex) {
            curVideoId = vid
            getMvUrlById(vid, isPreLoad = false)
        } else {
            getMvUrlById(vid, isPreLoad = true)
        }
    }

    /**
     * 列表视频的点赞/评论/分享数需要单独取详情
     */
    fun loadVideoStats(vid: String?) {
        if (vid.isNullOrBlank() || videoStats.containsKey(vid)) {
            return
        }
        viewModelScope.launch {
            runCatching { api.getMvDetail(vid) }
                .onSuccess { result ->
                    result.data?.let { detail ->
                        videoStats = videoStats + (vid to VideoStats(
                            commentCount = detail.commentCount?.toInt() ?: 0,
                            shareCount = detail.shareCount?.toInt() ?: 0,
                            praisedCount = detail.subCount?.toInt() ?: 0,
                        ))
                    }
                }
        }
    }

    fun buildVideoPager(areaIndex: Int, initOffset: Int) {
        videoFlows = buildPager(
            config = AppPagingConfig(pageSize = 10, initialLoadSize = 10),
            transformListBlock = {
                it?.data
            }) { curPage, pageSize ->
            val area = mvAreaList.getOrElse(areaIndex) { "全部" }
            api.getMvAll(
                area = area,
                offset = initOffset + (curPage - 1) * pageSize,
                limit = pageSize,
            )
        }
    }

    fun getMvUrlById(mvid: String?, isPreLoad: Boolean = false) {
        if (mvid.isNullOrBlank()) return
        launch(handleSuccessBlock = { result ->
            val url = result.data?.url.orEmpty()
            if (url.isNotBlank()) {
                val fixedUrl = if (url.startsWith("http://", ignoreCase = true)) {
                    url.replaceFirst("http://", "https://", ignoreCase = true)
                } else {
                    url
                }
                val videoUrlBean = VideoUrlBean(
                    id = mvid,
                    url = fixedUrl,
                    size = result.data?.size ?: 0L,
                    validityTime = result.data?.expi ?: 0,
                    needPay = false,
                    r = result.data?.r ?: 0
                )
                mvUrlCache[mvid] = videoUrlBean
                if (curVideoId == mvid && !isPreLoad) {
                    curVideoUrl = fixedUrl
                } else if (isPreLoad) {
                    // 地址到位后让空闲播放器开始预加载
                    prepareAround()
                }
            }
        }) {
            api.getMvUrl(mvid)
        }
    }

    fun getMvDetailAndFill(mvid: String?) {
        if (mvid.isNullOrBlank()) return
        launch(handleSuccessBlock = { result ->
            result.data?.let {
                firstVideo = mapMvDetailToVideo(it)
                getMvUrlAndFill(it.id?.toString())
            }
        }) {
            api.getMvDetail(mvid)
        }
    }

    private fun getMvUrlAndFill(mvid: String?) {
        if (mvid.isNullOrBlank()) return
        launch(handleSuccessBlock = { result ->
            val url = result.data?.url.orEmpty()
            if (url.isNotBlank()) {
                val fixedUrl = if (url.startsWith("http://", ignoreCase = true)) {
                    url.replaceFirst("http://", "https://", ignoreCase = true)
                } else {
                    url
                }
                firstVideo.urls = listOf(
                    VideoUrlBean(
                        id = mvid,
                        url = fixedUrl,
                        size = result.data?.size ?: 0L,
                        validityTime = result.data?.expi ?: 0,
                        needPay = false,
                        r = result.data?.r ?: 0
                    )
                )
                mvUrlCache[mvid] = firstVideo.urls!!.first()
                curVideoId = mvid
                curVideoUrl = fixedUrl
            }
        }) {
            api.getMvUrl(mvid)
        }
    }

    private fun mapMvDetailToVideo(detail: MvDetailData): Video {
        val author = detail.artists?.firstOrNull()
        return Video(
            scm = null,
            coverUrl = detail.cover,
            height = 1920,
            width = 1080,
            title = detail.name,
            description = detail.desc ?: detail.briefDesc.orEmpty(),
            commentCount = detail.commentCount?.toInt() ?: 0,
            shareCount = detail.shareCount?.toInt() ?: 0,
            creator = Subscribers(
                userId = author?.id ?: detail.artistId ?: 0L,
                avatarUrl = author?.img1v1Url.orEmpty(),
                nickname = author?.name ?: detail.artistName.orEmpty()
            ),
            previewUrl = "",
            relateSong = null,
            vid = detail.id?.toString(),
            durationms = detail.duration?.toInt() ?: 0,
            playTime = detail.playCount?.toInt() ?: 0,
            praisedCount = detail.subCount?.toInt() ?: 0
        )
    }

    private fun prefetchAround(curDisplayIndex: Int) {
        val pagingItems = videoPagingItems ?: return
        val nextDisplayIndex = curDisplayIndex + 1
        // 主动触发下一页数据读取，减少滑到末尾时的空等。
        pagingItems[nextDisplayIndex]
        pagingItems[nextDisplayIndex + 1]

        val nextMv = if (nextDisplayIndex > 0) {
            pagingItems.itemSnapshotList.getOrNull(nextDisplayIndex - 1)
        } else {
            null
        }
        val nextMvId = nextMv?.id?.toString()
        if (!nextMvId.isNullOrBlank() && mvUrlCache[nextMvId] == null) {
            getMvUrlById(nextMvId, isPreLoad = true)
        }
    }

    override fun onCleared() {
        mUpdateDuringTask?.cancel()
        release()
        super.onCleared()
    }

    private fun startUpdateDuringTask() {
        mUpdateDuringTask?.cancel()
        mUpdateDuringTask = object : TimerTask() {
            override fun run() {
                if (!seeking) {
                    mHandler.post {
                        videoProgress = (exoPlayer.currentPosition.toFloat() * 100 / exoPlayer.duration).toInt()
                    }
                }
            }
        }.apply {
            mTimer.schedule(this, 0, 1000)
        }
    }
}
