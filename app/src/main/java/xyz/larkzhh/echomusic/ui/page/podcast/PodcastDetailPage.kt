package xyz.larkzhh.echomusic.ui.page.podcast

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.compose.CommonTopAppBar
import xyz.larkzhh.echomusic.core.MusicPlayController
import xyz.larkzhh.echomusic.core.viewstate.BaseViewStateViewModel
import xyz.larkzhh.echomusic.core.viewstate.LoadingComponent
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.model.Al
import xyz.larkzhh.echomusic.model.Ar
import xyz.larkzhh.echomusic.model.DjProgram
import xyz.larkzhh.echomusic.model.DjRadio
import xyz.larkzhh.echomusic.model.SongBean
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.ui.page.playmusic.PlayListSheet
import xyz.larkzhh.echomusic.ui.page.playmusic.PlayMusicSheet
import xyz.larkzhh.echomusic.ui.page.playmusic.component.CpnBottomPlayMusic
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.StringUtil
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun PodcastDetailPage(radioId: Long, radioName: String) {
    val viewModel: PodcastDetailViewModel = hiltViewModel()

    LaunchedEffect(radioId) {
        viewModel.load(radioId)
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
                title = viewModel.radio?.name ?: radioName,
                leftClick = { EchoNavController.instance.popBackStack() },
            )

            when {
                viewModel.loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingComponent(loadingWidth = 90.cdp, loadingHeight = 75.cdp)
                }

                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { PodcastHeader(viewModel.radio) }
                    itemsIndexed(viewModel.programs) { index, program ->
                        PodcastProgramRow(program) {
                            val songs = viewModel.songList()
                            if (index in songs.indices) {
                                MusicPlayController.setDataSource(songs, index)
                                MusicPlayController.showCpnBottomMusicPlay = false
                                MusicPlayController.showPlayMusicSheet = true
                            }
                        }
                    }
                    if (viewModel.more) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.cdp),
                                contentAlignment = Alignment.Center
                            ) {
                                TextButton(onClick = { viewModel.loadMore() }) {
                                    Text(text = "加载更多", color = AppColorsProvider.current.primary)
                                }
                            }
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

@Composable
private fun PodcastHeader(radio: DjRadio?) {
    if (radio == null) {
        return
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.cdp, vertical = 16.cdp)
    ) {
        CommonNetworkImage(
            url = radio.picUrl,
            modifier = Modifier
                .size(180.cdp)
                .clip(RoundedCornerShape(18.cdp)),
            placeholder = R.drawable.ic_default_placeholder_video,
            error = R.drawable.ic_default_placeholder_video
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 20.cdp)
        ) {
            Text(
                text = radio.name.orEmpty(),
                color = AppColorsProvider.current.firstText,
                fontSize = 30.csp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.cdp))
            Text(
                text = "主播：${radio.dj?.nickname.orEmpty()}",
                color = AppColorsProvider.current.secondText,
                fontSize = 22.csp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(6.cdp))
            Text(
                text = "${radio.programCount} 期节目",
                color = AppColorsProvider.current.thirdText,
                fontSize = 20.csp
            )
        }
    }
}

@Composable
private fun PodcastProgramRow(program: DjProgram, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.cdp, vertical = 12.cdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CommonNetworkImage(
            url = program.coverUrl,
            modifier = Modifier
                .size(80.cdp)
                .clip(RoundedCornerShape(12.cdp)),
            placeholder = R.drawable.ic_default_placeholder_video,
            error = R.drawable.ic_default_placeholder_video
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.cdp)
        ) {
            Text(
                text = program.name.orEmpty(),
                color = AppColorsProvider.current.firstText,
                fontSize = 26.csp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.cdp))
            Text(
                text = "${StringUtil.formatMilliseconds(program.duration.toInt())} · ${program.listenerCount} 次收听",
                color = AppColorsProvider.current.thirdText,
                fontSize = 20.csp
            )
        }
    }
}

@HiltViewModel
class PodcastDetailViewModel @Inject constructor(
    private val api: NeteaseApi,
) : BaseViewStateViewModel() {

    var radio by mutableStateOf<DjRadio?>(null)
        private set

    var programs by mutableStateOf<List<DjProgram>>(emptyList())
        private set

    var loading by mutableStateOf(true)
        private set

    var more by mutableStateOf(false)
        private set

    private var radioId = 0L
    private var loadingMore = false

    fun load(id: Long) {
        if (radioId == id) {
            return
        }
        radioId = id
        loading = true
        viewModelScope.launch {
            runCatching { api.getDjDetail(id) }
                .onSuccess { radio = it.data }
            runCatching { api.getDjProgram(id, 30, 0) }
                .onSuccess {
                    programs = it.programs.orEmpty()
                    more = it.more == true
                }
            loading = false
        }
    }

    fun loadMore() {
        if (loadingMore || !more) {
            return
        }
        loadingMore = true
        viewModelScope.launch {
            runCatching { api.getDjProgram(radioId, 30, programs.size) }
                .onSuccess {
                    programs = programs + it.programs.orEmpty()
                    more = it.more == true
                }
            loadingMore = false
        }
    }

    fun songList(): List<SongBean> = programs.mapNotNull { it.toSongBean(radio) }
}

/** 节目主音轨转成播放用的歌曲对象 */
private fun DjProgram.toSongBean(radio: DjRadio?): SongBean? {
    val songId = mainTrackId.takeIf { it > 0 }
        ?: mainSong?.id?.takeIf { it > 0 }
        ?: return null
    val artist = mainSong?.artists
        ?.mapNotNull { it.name?.takeIf { name -> name.isNotBlank() } }
        ?.joinToString("/")
        ?.takeIf { it.isNotBlank() }
        ?: radio?.dj?.nickname.orEmpty()
    return SongBean(
        id = songId,
        name = name ?: mainSong?.name.orEmpty(),
        al = Al(
            id = mainSong?.album?.id ?: 0L,
            name = mainSong?.album?.name ?: radio?.name.orEmpty(),
            picUrl = mainSong?.album?.picUrl ?: coverUrl ?: radio?.picUrl.orEmpty(),
        ),
        ar = listOf(Ar(id = 0, name = artist)),
    )
}
