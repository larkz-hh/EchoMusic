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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import xyz.larkzhh.echomusic.compose.CommonTopAppBar
import xyz.larkzhh.echomusic.core.AppGlobalData
import xyz.larkzhh.echomusic.core.MusicPlayController
import xyz.larkzhh.echomusic.core.RecentPlayStorage
import xyz.larkzhh.echomusic.core.viewstate.BaseViewStateViewModel
import xyz.larkzhh.echomusic.core.viewstate.LoadingComponent
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.model.SongBean
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.ui.page.mine.component.CpnSongItem
import xyz.larkzhh.echomusic.ui.page.playmusic.PlayListSheet
import xyz.larkzhh.echomusic.ui.page.playmusic.PlayMusicSheet
import xyz.larkzhh.echomusic.ui.page.playmusic.component.CpnBottomPlayMusic
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@Composable
fun RecentPlayPage() {
    val viewModel: RecentPlayViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

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

            when {
                viewModel.loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingComponent(loadingWidth = 90.cdp, loadingHeight = 75.cdp)
                }

                viewModel.songs.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无最近播放",
                        color = AppColorsProvider.current.secondText,
                        fontSize = 26.csp
                    )
                }

                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(viewModel.songs) { index, song ->
                        CpnSongItem(index, song) {
                            MusicPlayController.setDataSource(viewModel.songs, index)
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

    var songs by mutableStateOf<List<SongBean>>(emptyList())
        private set

    var loading by mutableStateOf(true)
        private set

    fun load() {
        val local = RecentPlayStorage.load()
        if (local.isNotEmpty()) {
            songs = local
            loading = false
            return
        }
        val uid = AppGlobalData.currentUid
        if (uid == null) {
            loading = false
            return
        }
        viewModelScope.launch {
            runCatching { api.getUserRecord(uid = uid, type = 0) }
                .onSuccess { result ->
                    val items = result.allData.ifEmpty { result.weekData }
                    songs = items.mapNotNull { it.song }
                }
            loading = false
        }
    }
}
