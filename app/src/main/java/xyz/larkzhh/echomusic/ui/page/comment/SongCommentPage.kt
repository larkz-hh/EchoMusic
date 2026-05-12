package xyz.larkzhh.echomusic.ui.page.comment

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import xyz.larkzhh.echomusic.model.SongBean
import xyz.larkzhh.echomusic.compose.CommonLocalImage
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.compose.CommonTabLayout
import xyz.larkzhh.echomusic.compose.CommonTabLayoutStyle
import xyz.larkzhh.echomusic.compose.CommonTopAppBar
import xyz.larkzhh.echomusic.ui.page.comment.component.CpnCommentPager
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.viewmodel.CommentViewModel
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.CollapsingToolbarScope
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.utils.csp

@Composable
fun SongCommentPage(songBean: SongBean) {
    Box {
        val viewModel: CommentViewModel = hiltViewModel()
        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { viewModel.commentSortTabs.size }
        )
        val state = rememberCollapsingToolbarScaffoldState()
        CollapsingToolbarScaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColorsProvider.current.background),
            state = state,
            scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
            toolbar = {
                ScrollHeader(songBean, pagerState)
            }
        ) {
            Body(songBean, pagerState)
        }

        FloorCommentSheet(CommentViewModel.TYPE_SONG)
    }
}

@Composable
private fun CollapsingToolbarScope.ScrollHeader(songBean: SongBean, pagerState: PagerState) {
    val viewModel: CommentViewModel = hiltViewModel()
    val statusBarHeight = WindowInsets.statusBars
        .asPaddingValues()
        .calculateTopPadding()
    val maxHeight = statusBarHeight + (88 + 150 + 100 + 20).cdp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(maxHeight)
            .parallax(1f)
            .verticalScroll(rememberScrollState())
    ) {
        SongInfoComponent(songBean)
        StickyHeader(pagerState)
    }

    Column {
        Box(
            Modifier
                .fillMaxWidth()
                .background(AppColorsProvider.current.background)
        )

        CommonTopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.cdp),
            title = viewModel.title,
            titleAlign = TextAlign.Left,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.cdp)
        )
    }
}

@Composable
private fun SongInfoComponent(songBean: SongBean) {
    Column {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 88.cdp)
                .fillMaxWidth()
                .height(150.cdp)
                .padding(horizontal = 42.cdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(104.cdp),
                contentAlignment = Alignment.Center
            ) {
                CommonLocalImage(
                    R.drawable.ic_disc,
                    modifier = Modifier.fillMaxSize()
                )
                CommonNetworkImage(
                    songBean.al.picUrl,
                    placeholder = R.drawable.ic_default_disk_cover,
                    error = R.drawable.ic_default_disk_cover,
                    modifier = Modifier.size(70.cdp)
                        .clip(CircleShape)
                )
            }
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = AppColorsProvider.current.firstText, fontSize = 36.csp)) {
                        append(songBean.name)
                    }
                    withStyle(style = SpanStyle(color = AppColorsProvider.current.secondText, fontSize = 32.csp)) {
                        append(" - ${songBean.ar.getOrNull(0)?.name ?: "未知"}")
                    }
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 22.cdp, end = 48.cdp)
            )
        }

        Divider(
            color = AppColorsProvider.current.divider,
            modifier = Modifier.fillMaxWidth(),
            thickness = 20.cdp
        )
    }
}

@Composable
private fun StickyHeader(pagerState: PagerState) {
    val viewModel: CommentViewModel = hiltViewModel()
    var selectedIndex by remember {
        mutableStateOf(0)
    }
    val scopeState = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.cdp)
            .padding(horizontal = 32.cdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "评论区",
            color = AppColorsProvider.current.firstText,
            fontSize = 32.csp,
            fontWeight = FontWeight.Bold
        )

        CommonTabLayout(
            tabTexts = viewModel.commentSortTabs.map { it.title },
            backgroundColor = Color.Transparent,
            style = CommonTabLayoutStyle(
                isScrollable = false,
                modifier = Modifier.width(300.cdp),
                selectedTextSize = 28.csp,
                unselectedTextSize = 28.csp,
                tabItemDrawBehindBlock = { position ->
                    if (position != viewModel.commentSortTabs.size - 1) {
                        drawLine(
                            Color.LightGray,
                            Offset(size.width, size.height * 0.35f),
                            Offset(size.width, size.height * 0.65f),
                            strokeWidth = 2.cdp.toPx()
                        )
                    }
                },
                customIndicator = { _, _ -> }
            ),
            selectedIndex = selectedIndex
        ) {
            selectedIndex = it
            scopeState.launch {
                pagerState.scrollToPage(selectedIndex)
            }
        }
    }
}

@Composable
private fun Body(songBean: SongBean, pagerState: PagerState) {
    val viewModel: CommentViewModel = hiltViewModel()

    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerState,
        userScrollEnabled = false
    ) { position ->
        CpnCommentPager(songBean.id.toString(), viewModel.commentSortTabs[position].type, CommentViewModel.TYPE_SONG)
    }
}


