package xyz.larkzhh.echomusic.ui.page.discovery

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.compose.CommonTopAppBar
import xyz.larkzhh.echomusic.core.viewstate.BaseViewStateViewModel
import xyz.larkzhh.echomusic.core.viewstate.LoadingComponent
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.model.PlaylistBean
import xyz.larkzhh.echomusic.model.SquarePlaylist
import xyz.larkzhh.echomusic.model.Subscribers
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.nav.RouterUrls
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

/** 歌单广场，按分类展示 */
@Composable
fun PlaylistSquarePage(cat: String) {
    val viewModel: PlaylistSquareViewModel = hiltViewModel()

    LaunchedEffect(cat) {
        viewModel.load(cat)
    }

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
            title = cat,
            leftClick = { EchoNavController.instance.popBackStack() },
        )

        when {
            viewModel.loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingComponent(loadingWidth = 90.cdp, loadingHeight = 75.cdp)
            }

            viewModel.playlists.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无歌单",
                    color = AppColorsProvider.current.secondText,
                    fontSize = 26.csp
                )
            }

            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 24.cdp, vertical = 16.cdp),
                horizontalArrangement = Arrangement.spacedBy(16.cdp),
                verticalArrangement = Arrangement.spacedBy(20.cdp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(viewModel.playlists) { item ->
                    SquarePlaylistCard(item)
                }
            }
        }
    }
}

@Composable
private fun SquarePlaylistCard(item: SquarePlaylist) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { openPlaylist(item) }
    ) {
        CommonNetworkImage(
            url = item.coverImgUrl,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(14.cdp)),
            placeholder = R.drawable.ic_default_placeholder_video,
            error = R.drawable.ic_default_placeholder_video
        )
        Text(
            text = item.name.orEmpty(),
            color = AppColorsProvider.current.firstText,
            fontSize = 22.csp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 10.cdp)
        )
        Text(
            text = "${formatPlayCount(item.playCount ?: 0L)} 次播放",
            color = AppColorsProvider.current.secondText,
            fontSize = 18.csp,
            maxLines = 1,
            modifier = Modifier.padding(top = 4.cdp)
        )
    }
}

private fun openPlaylist(item: SquarePlaylist) {
    val playlistBean = PlaylistBean(
        tracks = null,
        trackIds = null,
        creator = item.creator ?: Subscribers(userId = 0L, avatarUrl = "", nickname = ""),
        name = item.name.orEmpty(),
        coverImgUrl = item.coverImgUrl.orEmpty(),
        trackCount = 0,
        id = item.id ?: 0L,
        playCount = item.playCount ?: 0L,
        description = null,
        shareCount = 0,
        commentCount = 0,
    )
    val json = Uri.encode(Gson().toJson(playlistBean))
    EchoNavController.instance.navigate("${RouterUrls.PLAY_LIST}/$json")
}

private fun formatPlayCount(count: Long): String = when {
    count >= 100_000_000 -> String.format(Locale.getDefault(), "%.1f亿", count / 100_000_000.0)
    count >= 10_000 -> String.format(Locale.getDefault(), "%.1f万", count / 10_000.0)
    else -> count.toString()
}

@HiltViewModel
class PlaylistSquareViewModel @Inject constructor(
    private val api: NeteaseApi,
) : BaseViewStateViewModel() {

    var playlists by mutableStateOf<List<SquarePlaylist>>(emptyList())
        private set

    var loading by mutableStateOf(true)
        private set

    private var loadedCat: String? = null

    fun load(cat: String) {
        if (loadedCat == cat) {
            return
        }
        loadedCat = cat
        loading = true
        viewModelScope.launch {
            runCatching { api.getTopPlaylist(cat = cat) }
                .onSuccess { playlists = it.playlists.orEmpty() }
            loading = false
        }
    }
}
