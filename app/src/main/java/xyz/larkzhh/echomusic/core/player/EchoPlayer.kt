package xyz.larkzhh.echomusic.core.player

import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import xyz.larkzhh.echomusic.EchoApplication
import xyz.larkzhh.echomusic.core.UserSpace
import xyz.larkzhh.echomusic.core.player.event.PauseSongEvent
import xyz.larkzhh.echomusic.core.player.event.PlaySongEvent
import xyz.larkzhh.echomusic.hilt.entrypoint.EntryPointFinder
import xyz.larkzhh.echomusic.model.SongBean
import xyz.larkzhh.echomusic.service.MusicPlayService
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList


object EchoPlayer : IPlayer {

    private var mStatus: PlayerStatus = PlayerStatus.IDLE
    var mCurSongBean: SongBean? = null

    private var mPlayer: ExoPlayer? = null
    private var mProgressJob: Job? = null
    private val mListeners = ArrayList<IPlayerListener>()
    private var mJob: Job? = null

    private val playerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val echoApi = EntryPointFinder.getEchoApi()

    private fun player(): ExoPlayer = mPlayer ?: buildPlayer().also { mPlayer = it }

    private fun buildPlayer(): ExoPlayer {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
        return ExoPlayer.Builder(EchoApplication.context)
            .setAudioAttributes(audioAttributes, true)
            .build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            // 只在首次就绪时自动开播，避免暂停后重新缓冲把播放拉起来
                            if (mStatus == PlayerStatus.IDLE) {
                                setStatus(PlayerStatus.PREPARED)
                                innerStartPlay()
                            }
                        }
                        Player.STATE_ENDED -> {
                            mProgressJob?.cancel()
                            setStatus(PlayerStatus.COMPLETED)
                            Log.d("cloudMusic", "onCompletion done")
                        }
                        // 空闲与缓冲无需额外处理
                        Player.STATE_IDLE, Player.STATE_BUFFERING -> Unit
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    Log.e("cloudMusic", "onError ${error.errorCodeName}")
                    mProgressJob?.cancel()
                    setStatus(PlayerStatus.ERROR)
                    setStatus(PlayerStatus.IDLE)
                }
            })
        }
    }

    fun addListener(listener: IPlayerListener) {
        mListeners.add(listener)
    }

    fun removeListener(listener: IPlayerListener) {
        mListeners.remove(listener)
    }

    override fun setDataSource(songBean: SongBean) {
        mCurSongBean = songBean
    }

    override fun start() {
        Log.d("cloudMusic", "start()")
        if (mStatus == PlayerStatus.STARTED
            || mStatus == PlayerStatus.PREPARED
            || mStatus == PlayerStatus.PAUSED
            || mStatus == PlayerStatus.COMPLETED
        ) {
            Log.d("cloudMusic", "start stop ()")
            stop()
        }
        mCurSongBean?.let {
            MusicPlayService.start()
            getSongUrlAndPlay(it.id)
        }
    }

    private fun getSongUrlAndPlay(songId: Long) {
        mJob?.cancel()
        mJob = playerScope.launch {
            try {
                val rawUrl = echoApi.getSongUrl(songId).data.firstOrNull()?.url
                    ?: "https://music.163.com/song/media/outer/url?id=$songId.mp3"
                val url = if (rawUrl.startsWith("http://", ignoreCase = true)) {
                    rawUrl.replaceFirst("http://", "https://", ignoreCase = true)
                } else {
                    rawUrl
                }
                playUrl(url, songId)
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e("cloudMusic", "getSongUrlAndPlay e = $e")
                    e.printStackTrace()
                    mListeners.forEach {
                        it.onStatusChanged(PlayerStatus.ERROR)
                    }
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun playUrl(url: String, songId: Long) {
        val dataSourceFactory = CacheDataSource.Factory()
            .setCache(MediaCache.audio(EchoApplication.context))
            .setUpstreamDataSourceFactory(
                DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
            )
            .setCacheKeyFactory { "${UserSpace.id}:$songId" }
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(url))

        val exoPlayer = player()
        exoPlayer.stop()
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    override fun pause() {
        if (mStatus == PlayerStatus.STARTED) {
            Log.d("cloudMusic", "pause()")
            mProgressJob?.cancel()
            setStatus(PlayerStatus.PAUSED)
            mPlayer?.pause()
            EventBus.getDefault().post(PauseSongEvent())
        }
    }

    override fun resume() {
        Log.d("cloudMusic", "resume()")
        innerStartPlay()
    }

    override fun stop() {
        mProgressJob?.cancel()
        mPlayer?.stop()
        setStatus(PlayerStatus.STOPPED)
        setStatus(PlayerStatus.IDLE)
        Log.d("cloudMusic", "stop()")
    }

    override fun seekTo(position: Int) {
        mPlayer?.seekTo(position.toLong())
    }

    private fun innerStartPlay() {
        Log.d("cloudMusic", "innerStartPlay()")
        mPlayer?.play()
        setStatus(PlayerStatus.STARTED)
        mCurSongBean?.let {
            EventBus.getDefault().post(PlaySongEvent())
        }
        mProgressJob?.cancel()
        // 主线程轮询进度，ExoPlayer 不允许跨线程访问
        mProgressJob = playerScope.launch {
            while (isActive) {
                setProgress()
                delay(1000)
            }
        }
    }

    private fun setStatus(status: PlayerStatus) {
        mStatus = status
        mListeners.forEach {
            it.onStatusChanged(mStatus)
        }
    }

    private fun setProgress() {
        val exoPlayer = mPlayer ?: return
        val duration = exoPlayer.duration
        if (duration <= 0) {
            return
        }
        val position = exoPlayer.currentPosition
        val percentage = (position * 100f / duration + 0.5f).toInt()
        mListeners.forEach {
            it.onProgress(duration.toInt(), position.toInt(), percentage)
        }
    }
}
