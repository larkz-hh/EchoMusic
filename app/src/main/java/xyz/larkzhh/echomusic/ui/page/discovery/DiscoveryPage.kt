package xyz.larkzhh.echomusic.ui.page.discovery

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.compose.CommonTopAppBar
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.nav.RouterUrls
import xyz.larkzhh.echomusic.ui.page.discovery.component.CpnDiscoveryBanner
import xyz.larkzhh.echomusic.ui.page.discovery.component.CpnDiscoveryCuratedPlaylists
import xyz.larkzhh.echomusic.ui.page.discovery.component.CpnDiscoveryEntryGrid
import xyz.larkzhh.echomusic.ui.page.discovery.component.CpnDiscoveryRanking
import xyz.larkzhh.echomusic.ui.page.discovery.component.CpnDiscoveryNewSongs
import xyz.larkzhh.echomusic.ui.page.discovery.component.defaultCuratedPlaylists
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.viewmodel.DiscoveryViewModel
import kotlinx.coroutines.launch

@Composable
fun DiscoveryPage(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    val viewModel: DiscoveryViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.getDiscoverBanner()
        viewModel.getPersonalized()
        viewModel.getMvFirst()
        viewModel.getToplist()
    }

    Column(
        Modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) {
        CommonTopAppBar(title = "发现",
            leftIconResId = R.drawable.ic_drawer_toggle,
            leftClick = {
                scope.launch {
                    if (drawerState.isOpen) {
                        drawerState.close()
                    } else {
                        drawerState.open()
                    }
                }
            },
            rightIconResId = R.drawable.ic_search,
            rightClick = {
                EchoNavController.instance.navigate(RouterUrls.SEARCH)
            })
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.cdp)
        ) {
            item { CpnDiscoveryBanner(viewModel.banners) }
            item {
                CpnDiscoveryEntryGrid { title ->
                    val cat = discoverEntryCat(title)
                    EchoNavController.instance.navigate(
                        "${RouterUrls.PLAYLIST_SQUARE}/${Uri.encode(cat)}"
                    )
                }
            }

            item {
                CpnDiscoveryCuratedPlaylists(
                    playlists = viewModel.curatedPlaylists.ifEmpty { defaultCuratedPlaylists() }
                )
            }
            item {
                CpnDiscoveryNewSongs(
                    songs = viewModel.newSongDiskItems
                )
            }
            item {
                CpnDiscoveryRanking(items = viewModel.rankingItems)
            }
            item { Spacer(modifier = Modifier.height(28.cdp)) }
        }
    }
}

/** 发现页入口对应的歌单分类 */
private fun discoverEntryCat(title: String): String = when (title) {
    "排行榜" -> "榜单"
    "歌单" -> "全部"
    "原创" -> "官方"
    "VIP" -> "官方"
    "经典" -> "经典"
    "欧美" -> "欧美"
    "粤语" -> "粤语"
    "驾车" -> "驾车"
    else -> "全部"
}
