package xyz.larkzhh.echomusic.viewmodel

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import xyz.larkzhh.echomusic.core.player.IPlayerListener
import xyz.larkzhh.echomusic.core.player.EchoPlayer
import xyz.larkzhh.echomusic.core.player.PlayerStatus
import xyz.larkzhh.echomusic.core.player.event.ChangeSongEvent
import xyz.larkzhh.echomusic.core.viewstate.BaseViewStateViewModel
import xyz.larkzhh.echomusic.core.viewstate.ViewStateMutableStateFlow
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.model.LyricResult
import xyz.larkzhh.echomusic.model.SongBean
import xyz.larkzhh.echomusic.utils.LyricUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject
import xyz.larkzhh.echomusic.model.SongCommentResult

@HiltViewModel
class PlayMusicViewModel @Inject constructor(private val api: NeteaseApi) : BaseViewStateViewModel(), IPlayerListener {

    // disk旋转动画
    val sheetDiskRotate by mutableStateOf(Animatable(0f))

    // 上一次disk旋转角度
    var lastSheetDiskRotateAngleForSnap = 0f

    // 是否抬起磁针
    var sheetNeedleUp by mutableStateOf(true)

    // 是否显示歌词组件
    var showLyric by mutableStateOf(false)

    var songCommentResult by mutableStateOf<SongCommentResult?>(null)

    var lyricResult = ViewStateMutableStateFlow<LyricResult>()
    val lyricModelList = mutableListOf<LyricModel>()
    var curLyricIndex by mutableStateOf(-1)
    var curPlayPosition = 0

    // 逐字高亮用的播放进度
    var curLyricPosition by mutableIntStateOf(0)

    init {
        EventBus.getDefault().register(this)
        EchoPlayer.addListener(this)
    }

    override fun onCleared() {
        EventBus.getDefault().unregister(this)
        EchoPlayer.removeListener(this)
        super.onCleared()
    }

    fun getSongComment(songBean: SongBean) {

        launch(handleSuccessBlock = {
            songCommentResult = it
        }) {
            api.getSongComment(songBean.id, offset = 0)
        }
    }

    fun getLyric(songBean: SongBean) {
        launch(lyricResult, handleSuccessBlock = {
            lyricModelList.clear()
            lyricModelList.addAll(LyricUtil.parse(it))
            curLyricIndex = lyricModelList.indexOfFirst { lyricModel ->
                curPlayPosition < lyricModel.time
            } - 1
        }) {
            curLyricIndex = -1
            api.getLyric(songBean.id)
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onEvent(event: ChangeSongEvent) {
        curLyricIndex = -1
        curPlayPosition = 0
        lyricModelList.clear()
        songCommentResult = null
    }

    override fun onStatusChanged(status: PlayerStatus) {
    }

    override fun onProgress(totalDuring: Int, currentPosition: Int, percentage: Int) {
        curPlayPosition = currentPosition
        curLyricPosition = currentPosition
        curLyricIndex = lyricModelList.indexOfFirst {
            currentPosition < it.time
        } - 1
        if (currentPosition > (lyricModelList.lastOrNull()?.time ?: 0)) {
            curLyricIndex = lyricModelList.size - 1
        }
    }
}


data class LyricModel(
    val time: Long,
    val lyric: String? = null,
    var tLyric: String? = null,
    // 逐字时间轴，为空表示普通歌词
    val words: List<LyricWord>? = null
)

data class LyricWord(
    val text: String,
    val start: Long,
    val duration: Long
)