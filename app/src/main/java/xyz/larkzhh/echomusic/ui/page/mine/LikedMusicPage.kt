package xyz.larkzhh.echomusic.ui.page.mine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.compose.CommonIcon
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.compose.CommonTopAppBar
import xyz.larkzhh.echomusic.core.AppGlobalData
import xyz.larkzhh.echomusic.core.LikeStore
import xyz.larkzhh.echomusic.core.MusicPlayController
import xyz.larkzhh.echomusic.core.viewstate.BaseViewStateViewModel
import xyz.larkzhh.echomusic.core.viewstate.LoadingComponent
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.model.SongBean
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.ui.page.playmusic.PlayListSheet
import xyz.larkzhh.echomusic.ui.page.playmusic.PlayMusicSheet
import xyz.larkzhh.echomusic.ui.page.playmusic.component.CpnBottomPlayMusic
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp
import xyz.larkzhh.echomusic.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@Composable
fun LikedMusicPage() {
    val viewModel: LikedMusicViewModel = hiltViewModel()

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
                title = "我喜欢的音乐",
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
                        text = "还没有喜欢的音乐",
                        color = AppColorsProvider.current.secondText,
                        fontSize = 26.csp
                    )
                }

                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(viewModel.songs) { index, song ->
                        LikedSongRow(
                            song = song,
                            onClick = {
                                MusicPlayController.setDataSource(viewModel.songs, index)
                                MusicPlayController.showCpnBottomMusicPlay = false
                                MusicPlayController.showPlayMusicSheet = true
                            },
                            onUnlike = { viewModel.unlike(song) }
                        )
                    }
                }
            }
        }

        CpnBottomPlayMusic()
        PlayMusicSheet()
        PlayListSheet()
    }
}

@Composable
private fun LikedSongRow(song: SongBean, onClick: () -> Unit, onUnlike: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.cdp, vertical = 10.cdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CommonNetworkImage(
            url = song.al.picUrl,
            placeholder = R.drawable.ic_default_disk_cover,
            error = R.drawable.ic_default_disk_cover,
            modifier = Modifier
                .size(88.cdp)
                .clip(RoundedCornerShape(12.cdp))
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.cdp)
        ) {
            Text(
                text = song.name,
                color = AppColorsProvider.current.firstText,
                fontSize = 26.csp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.ar.joinToString("/") { it.name },
                color = AppColorsProvider.current.secondText,
                fontSize = 22.csp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.cdp)
            )
        }
        CommonIcon(
            resId = R.drawable.ic_like_no,
            tint = Color(0xFFFF4D4F),
            modifier = Modifier
                .size(44.cdp)
                .clickable(onClick = onUnlike)
        )
    }
}


@HiltViewModel
class LikedMusicViewModel @Inject constructor(private val api: NeteaseApi) : BaseViewStateViewModel() {

    var songs by mutableStateOf<List<SongBean>>(emptyList())
        private set

    var loading by mutableStateOf(true)
        private set

    fun load() {
        val uid = AppGlobalData.currentUid
        if (uid == null) {
            loading = false
            return
        }
        viewModelScope.launch {
            runCatching {
                val ids = api.getLikeList(uid).ids.orEmpty()
                if (ids.isEmpty()) {
                    emptyList()
                } else {
                    ids.chunked(200).flatMap { chunk ->
                        api.getSongDetail(chunk.joinToString(",")).songs
                    }
                }
            }.onSuccess { result ->
                songs = result
                LikeStore.setAll(result.map { it.id }.toSet())
            }
            loading = false
        }
    }

    fun unlike(song: SongBean) {
        viewModelScope.launch {
            runCatching { api.like(song.id, false) }
                .onSuccess {
                    LikeStore.setLiked(song.id, false)
                    songs = songs.filterNot { it.id == song.id }
                }
                .onFailure { showToast("取消喜欢失败") }
        }
    }
}
