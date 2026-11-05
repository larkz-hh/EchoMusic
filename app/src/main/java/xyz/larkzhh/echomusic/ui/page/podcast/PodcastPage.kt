package xyz.larkzhh.echomusic.ui.page.podcast

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
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
import xyz.larkzhh.echomusic.core.viewstate.BaseViewStateViewModel
import xyz.larkzhh.echomusic.core.viewstate.LoadingComponent
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.model.DjRadio
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.nav.RouterUrls
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun PodcastPage(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    val viewModel: PodcastViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(AppColorsProvider.current.background)
    ) {
        CommonTopAppBar(
            title = "播客",
            leftIconResId = R.drawable.ic_drawer_toggle,
            leftClick = {
                scope.launch {
                    if (drawerState.isOpen) {
                        drawerState.close()
                    } else {
                        drawerState.open()
                    }
                }
            }
        )

        when {
            viewModel.loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingComponent(loadingWidth = 90.cdp, loadingHeight = 75.cdp)
            }

            viewModel.radios.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无播客",
                    color = AppColorsProvider.current.secondText,
                    fontSize = 26.csp
                )
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.cdp, vertical = 16.cdp)
            ) {
                item {
                    Text(
                        text = "推荐播客",
                        color = AppColorsProvider.current.firstText,
                        fontSize = 34.csp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.cdp)
                    )
                }
                items(viewModel.radios) { radio ->
                    PodcastRadioRow(radio)
                }
            }
        }
    }
}

@Composable
private fun PodcastRadioRow(radio: DjRadio) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { openRadio(radio) }
            .padding(vertical = 10.cdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CommonNetworkImage(
            url = radio.picUrl,
            modifier = Modifier
                .size(140.cdp)
                .clip(RoundedCornerShape(16.cdp)),
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
                fontSize = 28.csp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.cdp))
            Text(
                text = radio.dj?.nickname.orEmpty(),
                color = AppColorsProvider.current.secondText,
                fontSize = 22.csp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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

private fun openRadio(radio: DjRadio) {
    EchoNavController.instance.navigate(
        "${RouterUrls.PODCAST_DETAIL}/${radio.id}/${Uri.encode(radio.name.orEmpty())}"
    )
}

@HiltViewModel
class PodcastViewModel @Inject constructor(
    private val api: NeteaseApi,
) : BaseViewStateViewModel() {

    var radios by mutableStateOf<List<DjRadio>>(emptyList())
        private set

    var loading by mutableStateOf(true)
        private set

    private var loaded = false

    fun load() {
        if (loaded) {
            return
        }
        loaded = true
        viewModelScope.launch {
            runCatching { api.getDjRecommend() }
                .onSuccess { radios = it.data.orEmpty() }
            loading = false
        }
    }
}
