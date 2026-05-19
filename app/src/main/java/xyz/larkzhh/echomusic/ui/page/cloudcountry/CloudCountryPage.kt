package xyz.larkzhh.echomusic.ui.page.cloudcountry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.DrawerState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.larkzhh.echomusic.core.viewstate.ViewStateComponent
import xyz.larkzhh.echomusic.compose.CommonTabLayout
import xyz.larkzhh.echomusic.compose.CommonTabLayoutStyle
import xyz.larkzhh.echomusic.compose.CommonTopAppBar
import kotlinx.coroutines.launch
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp
import xyz.larkzhh.echomusic.viewmodel.CloudCountryViewModel

/**
 * 云村
 */
@Composable
fun CloudCountryPage(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) {
        CommonTopAppBar(title = "云村",
            leftIconResId = R.drawable.ic_drawer_toggle,
            leftClick = {
                scope.launch {
                    if (drawerState.isOpen) {
                        drawerState.close()
                    } else {
                        drawerState.open()
                    }
                }
            })

        val viewModel: CloudCountryViewModel = hiltViewModel()

        ViewStateComponent(viewStateFlow = viewModel.videoGroupTabsResult,
            loadDataBlock = {
                viewModel.getVideoGroupTabs()
            }) { data ->

            var selectedIndex by remember { mutableStateOf(0) }
            val coroutineScope = rememberCoroutineScope()
            val pagerState = rememberPagerState(
                initialPage = 0,
                pageCount = { data.data.size }
            )

            Column {
                CommonTabLayout(
                    tabTexts = data.data.map {
                        it.name
                    },
                    style = CommonTabLayoutStyle(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(88.cdp),
                        indicatorPaddingBottom = 24.cdp,
                        indicatorWidth = 80.cdp,
                        isScrollable = true,
                        selectedTextSize = 28.csp,
                        unselectedTextSize = 28.csp
                    ),
                    selectedIndex = selectedIndex
                ) {
                    selectedIndex = it
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(it)
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = true,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppColorsProvider.current.background)
                ) { pagePosition ->
                    selectedIndex = pagerState.currentPage
                    VideoStaggeredGridPage(data.data[pagePosition].id, pagePosition)
                }
            }
        }
    }
}
