package xyz.larkzhh.echomusic.viewmodel

import android.content.Context
import android.os.Vibrator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import xyz.larkzhh.echomusic.EchoApplication
import xyz.larkzhh.echomusic.core.viewstate.BaseViewStateViewModel
import xyz.larkzhh.echomusic.core.viewstate.ViewStateMutableStateFlow
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.model.PlaylistBean
import xyz.larkzhh.echomusic.model.UserPlaylistResult
import xyz.larkzhh.echomusic.compose.DragStatus
import xyz.larkzhh.echomusic.core.AppGlobalData
import xyz.larkzhh.echomusic.core.viewstate.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject



@HiltViewModel
class MineViewModel @Inject constructor(private val api: NeteaseApi) : BaseViewStateViewModel() {

    var favoritePlayList: PlaylistBean? by mutableStateOf(null)
    var selfCreatePlayList: List<PlaylistBean>? by mutableStateOf(null)
    var collectPlayList: List<PlaylistBean>? by mutableStateOf(null)

    val userPlaylistResult = ViewStateMutableStateFlow<UserPlaylistResult>()

    var dragStatus by mutableStateOf<DragStatus>(DragStatus.Idle)
    var selectedTabIndex by mutableIntStateOf(0)

    var selfCreatePlayListHeaderIndex = 0
    var collectPlayListHeaderIndex = 0
    var songHelperIndex = 0

    var vibratorService = EchoApplication.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    fun getUserPlayList() {
        val uid = AppGlobalData.currentUid
        if (uid == null) {
            // 游客无歌单
            selfCreatePlayList = emptyList()
            collectPlayList = emptyList()
            selfCreatePlayListHeaderIndex = 0
            collectPlayListHeaderIndex = 2
            songHelperIndex = 4
            userPlaylistResult.value = ViewState.Success(UserPlaylistResult(playlist = emptyList()))
            return
        }
        launch(userPlaylistResult, handleSuccessBlock = {
            val selfCreateList = mutableListOf<PlaylistBean>()
            val collectList = mutableListOf<PlaylistBean>()
            val myUid = AppGlobalData.sLoginResult?.profile?.userId

            // 按 creator 区分自建与收藏
            it.playlist.forEach { playListBean ->
                if (myUid != null && playListBean.creator.userId == myUid) {
                    selfCreateList.add(playListBean)
                } else {
                    collectList.add(playListBean)
                }
            }
            selfCreatePlayList = selfCreateList
            collectPlayList = collectList

            selfCreatePlayListHeaderIndex = 0
            val selfCreateListSize = if (selfCreateList.isEmpty()) 1 else selfCreateList.size
            val collectListSize = if (collectList.isEmpty()) 1 else collectList.size
            collectPlayListHeaderIndex =  selfCreateListSize + 2
            songHelperIndex = collectPlayListHeaderIndex + collectListSize + 1
        }) {
            api.getUserPlayList(uid)
        }
    }

    fun vibrator() {
        vibratorService.vibrate(50)
    }
}
