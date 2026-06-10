package xyz.larkzhh.echomusic.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_READY
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
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
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject


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

    // 当前播放的视频id
    var curVideoId by mutableStateOf<String?>(null)

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
    lateinit var exoPlayer: ExoPlayer

    // 是否拖动进度条中
    private var seeking = false

    private val mTimer: Timer = Timer()
    private var mUpdateDuringTask: TimerTask? = null
    private var mHandler = Handler(Looper.getMainLooper())

    private var exoPlayListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            exoPlayStatus = playbackState
            if (exoPlayStatus == STATE_READY && MusicPlayController.isPlaying()) {
                MusicPlayController.pause()
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
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
     * 初始化exo播放器
     */
    fun initExoPlayerIfNeeded(context: Context) {
        if (!::exoPlayer.isInitialized) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                addListener(exoPlayListener)
            }
        }
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
        exoPlayer.removeListener(exoPlayListener)
        exoPlayer.release()
    }

    /**
     * 加载视频
     */
    @OptIn(UnstableApi::class)
    fun loadVideo(url: String) {
        // 缓存键用视频 id，避免地址带签名变化后缓存失效
        val cacheKey = "${UserSpace.id}:video:${curVideoId ?: url}"
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

        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(url)))

        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        exoPlayer.playWhenReady = true
        exoPlayer.prepare()
    }

    /**
     * 切换视频播放地址
     */
    fun switchVideoUrl(newIndex: Int) {
        stopVideo()
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
