package xyz.larkzhh.echomusic.core.player

import android.media.MediaPlayer
import android.util.Log
import xyz.larkzhh.echomusic.core.player.event.PauseSongEvent
import xyz.larkzhh.echomusic.core.player.event.PlaySongEvent
import xyz.larkzhh.echomusic.hilt.entrypoint.EntryPointFinder
import xyz.larkzhh.echomusic.model.SongBean
import xyz.larkzhh.echomusic.service.MusicPlayService
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Created by cloudMusic on 2022/4/23.
 */
object EchoPlayer : IPlayer,
    MediaPlayer.OnCompletionListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnErrorListener {

    private var mStatus: PlayerStatus = PlayerStatus.IDLE
    var mCurSongBean: SongBean? = null

    private val mMediaPlayer = MediaPlayer()

    private val mTimer: Timer = Timer()
    private var mUpdateDuringTask: TimerTask? = null
    private val mListeners = ArrayList<IPlayerListener>()
    private var mJob: Job? = null

    // 使用 SupervisorJob 替代 GlobalScope，便于生命周期管理
    private val playerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val echoApi = EntryPointFinder.getEchoApi()

    init {
        mMediaPlayer.setOnCompletionListener(this)
        mMediaPlayer.setOnPreparedListener(this)
        mMediaPlayer.setOnBufferingUpdateListener(this)
        mMediaPlayer.setOnErrorListener(this)
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
                mMediaPlayer.reset()
                mMediaPlayer.setDataSource(url)
                mMediaPlayer.prepareAsync()
                Log.d("cloudMusic", "prepareAsync()")
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

    override fun pause() {
        if (mStatus == PlayerStatus.STARTED) {
            Log.d("cloudMusic", "pause()")
            mUpdateDuringTask?.cancel()
            setStatus(PlayerStatus.PAUSED)
            mMediaPlayer.pause()
            EventBus.getDefault().post(PauseSongEvent())
        }
    }

    override fun resume() {
        Log.d("cloudMusic", "resume()")
        innerStartPlay()
    }

    override fun stop() {
        mUpdateDuringTask?.cancel()
        mMediaPlayer.stop()
        setStatus(PlayerStatus.STOPPED)
        setStatus(PlayerStatus.IDLE)
        Log.d("cloudMusic", "stop()")
    }

    override fun seekTo(position: Int) {
        mMediaPlayer.seekTo(position)
    }

    override fun onCompletion(mp: MediaPlayer) {
        mUpdateDuringTask?.cancel()
        setStatus(PlayerStatus.COMPLETED)
        Log.d("cloudMusic", "onCompletion done")
    }

    override fun onPrepared(mp: MediaPlayer?) {
        setStatus(PlayerStatus.PREPARED)
        innerStartPlay()
    }

    private fun innerStartPlay() {
        Log.d("cloudMusic", "innerStartPlay()")
        mMediaPlayer.start()
        setStatus(PlayerStatus.STARTED)
        mCurSongBean?.let {
            EventBus.getDefault().post(PlaySongEvent())
        }
        mUpdateDuringTask?.cancel()
        mUpdateDuringTask = object : TimerTask() {
            override fun run() {
                setProgress()
            }
        }.apply {
            mTimer.schedule(this, 0, 1000)
        }
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.e("cloudMusic", "onError what=${what},extra=${extra}")
        mUpdateDuringTask?.cancel()
        setStatus(PlayerStatus.ERROR)
        setStatus(PlayerStatus.IDLE)
        return true
    }

    private fun setStatus(status: PlayerStatus) {
        mStatus = status
        mListeners.forEach {
            it.onStatusChanged(mStatus)
        }
    }

    private fun setProgress() {
        mListeners.forEach {
            val percentage = ((mMediaPlayer.currentPosition * 100f / mMediaPlayer.duration) + 0.5f).toInt()
            it.onProgress(mMediaPlayer.duration, mMediaPlayer.currentPosition, percentage)
        }
    }
}
