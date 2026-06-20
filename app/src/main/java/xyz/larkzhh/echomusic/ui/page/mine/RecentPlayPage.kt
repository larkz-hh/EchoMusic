package xyz.larkzhh.echomusic.ui.page.mine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.larkzhh.echomusic.compose.CommonTopAppBar
import xyz.larkzhh.echomusic.core.AppGlobalData
import xyz.larkzhh.echomusic.core.MusicPlayController
import xyz.larkzhh.echomusic.core.viewstate.BaseViewStateViewModel
import xyz.larkzhh.echomusic.core.viewstate.ViewState
import xyz.larkzhh.echomusic.core.viewstate.ViewStateComponent
import xyz.larkzhh.echomusic.core.viewstate.ViewStateMutableStateFlow
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.model.SongBean
import xyz.larkzhh.echomusic.model.UserRecordResult
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.ui.page.mine.component.CpnSongItem
import xyz.larkzhh.echomusic.ui.page.playmusic.PlayListSheet
import xyz.larkzhh.echomusic.ui.page.playmusic.PlayMusicSheet
import xyz.larkzhh.echomusic.ui.page.playmusic.component.CpnBottomPlayMusic
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@Composable
fun RecentPlayPage() {
    val viewModel: RecentPlayViewModel = hiltViewModel()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColorsProvider.current.background)
                .statusBarsPadding()
        ) {
            CommonTopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.cdp),
                title = "最近播放",
                titleAlign = TextAlign.Left,
                leftClick = { EchoNavController.instance.popBackStack() },
            )

            ViewStateComponent(
                modifier = Modifier.fillMaxSize(),
                viewStateFlow = viewModel.recordResult,
                loadDataBlock = { viewModel.load() },
            ) {
                val songs = viewModel.songs
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(songs) { index, song ->
                        CpnSongItem(index, song) {
                            MusicPlayController.setDataSource(songs, index)
                            MusicPlayController.showCpnBottomMusicPlay = false
                            MusicPlayController.showPlayMusicSheet = true
                        }
                    }
                }
            }
        }

        CpnBottomPlayMusic()
        PlayMusicSheet()
        PlayListSheet()
    }
}


@HiltViewModel
class RecentPlayViewModel @Inject constructor(private val api: NeteaseApi) : BaseViewStateViewModel() {

    val recordResult = ViewStateMutableStateFlow<UserRecordResult>()

    var songs by mutableStateOf<List<SongBean>>(emptyList())
        private set

    fun load() {
        val uid = AppGlobalData.currentUid
        if (uid == null) {
            recordResult.value = ViewState.Fail("401", "登录后查看最近播放")
            return
        }
        launch(recordResult, handleSuccessBlock = { result ->
            val items = if (result.allData.isNotEmpty()) result.allData else result.weekData
            songs = items.mapNotNull { it.song }
        }) {
            // type=0 全部时间，type=1 只有最近一周
            api.getUserRecord(uid = uid, type = 0)
        }
    }
}
