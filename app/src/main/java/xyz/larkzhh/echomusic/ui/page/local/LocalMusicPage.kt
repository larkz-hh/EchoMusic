package xyz.larkzhh.echomusic.ui.page.local

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import xyz.larkzhh.echomusic.compose.CommonTopAppBar
import xyz.larkzhh.echomusic.core.MusicPlayController
import xyz.larkzhh.echomusic.core.viewstate.LoadingComponent
import xyz.larkzhh.echomusic.model.SongBean
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.ui.page.mine.component.CpnSongItem
import xyz.larkzhh.echomusic.ui.page.playmusic.PlayListSheet
import xyz.larkzhh.echomusic.ui.page.playmusic.PlayMusicSheet
import xyz.larkzhh.echomusic.ui.page.playmusic.component.CpnBottomPlayMusic
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.LocalMusicScanner
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val audioPermission: String
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

@Composable
fun LocalMusicPage() {
    val context = LocalContext.current
    var granted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, audioPermission) == PackageManager.PERMISSION_GRANTED
        )
    }
    var loading by remember { mutableStateOf(false) }
    var songs by remember { mutableStateOf<List<SongBean>>(emptyList()) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        granted = it
    }

    LaunchedEffect(Unit) {
        if (!granted) {
            launcher.launch(audioPermission)
        }
    }

    LaunchedEffect(granted) {
        if (granted) {
            loading = true
            songs = withContext(Dispatchers.IO) { LocalMusicScanner.scan(context) }
            loading = false
        }
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
                title = "本地音乐",
                titleAlign = TextAlign.Left,
                leftClick = { EchoNavController.instance.popBackStack() },
            )

            when {
                !granted -> PermissionTip {
                    launcher.launch(audioPermission)
                }

                loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingComponent(loadingWidth = 90.cdp, loadingHeight = 75.cdp)
                }

                songs.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "没有找到本地音乐",
                        color = AppColorsProvider.current.secondText,
                        fontSize = 26.csp
                    )
                }

                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
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

@Composable
private fun PermissionTip(onRequest: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "需要读取音频权限才能扫描本地音乐",
                color = AppColorsProvider.current.secondText,
                fontSize = 26.csp
            )
            Text(
                text = "去授权",
                color = AppColorsProvider.current.primary,
                fontSize = 28.csp,
                modifier = Modifier
                    .padding(top = 20.cdp)
                    .clip(RoundedCornerShape(20.cdp))
                    .clickable(onClick = onRequest)
                    .padding(horizontal = 24.cdp, vertical = 10.cdp)
            )
        }
    }
}
