package xyz.larkzhh.echomusic.ui.page.home

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import xyz.larkzhh.echomusic.core.MusicPlayController
import xyz.larkzhh.echomusic.compose.BottomNavigationBar
import xyz.larkzhh.echomusic.compose.BottomNavigationItem
import xyz.larkzhh.echomusic.ui.page.cloudcountry.CloudCountryPage
import xyz.larkzhh.echomusic.ui.page.discovery.DiscoveryPage
import xyz.larkzhh.echomusic.ui.page.home.component.CpnHomeDrawer
import xyz.larkzhh.echomusic.ui.page.mine.MinePage
import xyz.larkzhh.echomusic.ui.page.playmusic.PlayListSheet
import xyz.larkzhh.echomusic.ui.page.playmusic.PlayMusicSheet
import xyz.larkzhh.echomusic.ui.page.playmusic.component.CpnBottomPlayMusic
import xyz.larkzhh.echomusic.ui.page.sing.SingPage
import xyz.larkzhh.echomusic.utils.TwoBackFinish
import kotlinx.coroutines.launch
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.ui.page.podcast.PodcastPage
import xyz.larkzhh.echomusic.ui.page.singer.SingerPage
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp


private val bottomNavigationItems = listOf(
    BottomNavigationItem("发现", R.drawable.ic_discovery),
    BottomNavigationItem("播客", R.drawable.ic_podcast),
    BottomNavigationItem("我的", R.drawable.ic_mine),
    BottomNavigationItem("k歌", R.drawable.ic_sing),
    BottomNavigationItem("云村", R.drawable.ic_cloud_country),
)

var selectedHomeTabIndex by mutableIntStateOf(2)


@Composable
fun HomePage(onFinish: () -> Unit = { }) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navBarBottomDp = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()
    BackHandler {
        if (drawerState.isOpen) {
            scope.launch {
                drawerState.close()
            }
        } else {
            if (MusicPlayController.showPlayMusicSheet) {
                MusicPlayController.showPlayMusicSheet = false
                MusicPlayController.showCpnBottomMusicPlay = true
            } else {
                TwoBackFinish().execute(onFinish)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            CpnHomeDrawer(drawerState)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColorsProvider.current.background)
                .padding(bottom = navBarBottomDp)
        ) {
            Body(drawerState)

            // 底部播放器组件
            CpnBottomPlayMusic()
            // 音乐播放Sheet
            PlayMusicSheet()
            // 播放列表Sheet
            PlayListSheet()
        }
    }
}

@Composable
private fun Body(drawerState: DrawerState) {
    Column(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(
            initialPage = selectedHomeTabIndex,
            pageCount = { bottomNavigationItems.size }
        )

        val paddingBottom = if (MusicPlayController.showCpnBottomMusicPlay) {
            104.cdp
        } else {
            0.dp
        }

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier
                .padding(bottom = paddingBottom)
                .weight(1f)
        ) { pagePosition ->
            selectedHomeTabIndex = pagerState.currentPage

            when (pagePosition) {
                0 -> DiscoveryPage(drawerState)
                1 -> PodcastPage(drawerState)
                2 -> MinePage(drawerState)
                3 -> SingerPage(drawerState)
                4 -> CloudCountryPage(drawerState)
            }
        }

        BottomNavigationBar(
            bottomNavigationItems,
            pagerState,
            selectedHomeTabIndex
        ) {
            selectedHomeTabIndex = it
        }
    }
}
