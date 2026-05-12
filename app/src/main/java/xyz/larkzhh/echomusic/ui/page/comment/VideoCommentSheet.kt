package xyz.larkzhh.echomusic.ui.page.comment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.larkzhh.echomusic.utils.onClick
import xyz.larkzhh.echomusic.compose.CommonIcon
import xyz.larkzhh.echomusic.compose.CommonTabLayout
import xyz.larkzhh.echomusic.compose.CommonTabLayoutStyle
import xyz.larkzhh.echomusic.utils.ScreenUtil
import xyz.larkzhh.echomusic.ui.page.comment.component.CpnCommentPager
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.transformDp
import xyz.larkzhh.echomusic.utils.*
import xyz.larkzhh.echomusic.viewmodel.VideoPlayViewModel
import xyz.larkzhh.echomusic.viewmodel.CommentViewModel
import kotlinx.coroutines.launch
import xyz.larkzhh.echomusic.R


var showVideoCommentSheet by mutableStateOf(false)
var videoCommentSheetOffset by mutableStateOf(ScreenUtil.getScreenHeight().toFloat())

@Composable
fun VideoCommentSheet() {
    if (showVideoCommentSheet) {
        VideoCommentSheetContent()
    } else {
        val viewModel: CommentViewModel = hiltViewModel()
        viewModel.title = "评论"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoCommentSheetContent() {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BackHandler(showVideoCommentSheet) {
        scope.launch {
            sheetState.hide()
            showVideoCommentSheet = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            showVideoCommentSheet = false
        },
        sheetState = sheetState,
        containerColor = Color.Transparent
    ) {
        VideoCommentList {
            scope.launch {
                sheetState.hide()
                showVideoCommentSheet = false
            }
        }
    }
}


@Composable
private fun VideoCommentList(onBack: () -> Unit) {
    val videoPlayViewModel: VideoPlayViewModel = hiltViewModel()
    val commentViewModel: CommentViewModel = hiltViewModel()
    val height = (ScreenUtil.getScreenHeight() - 720).transformDp
    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { commentViewModel.commentSortTabs.size }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(AppColorsProvider.current.background)
    ) {
        VideoCommentHeader(pagerState, onBack)
        videoPlayViewModel.curVideoId?.let {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                userScrollEnabled = false
            ) { position ->
                CpnCommentPager(it, commentViewModel.commentSortTabs[position].type, CommentViewModel.TYPE_VIDEO)
            }
        }
    }
}


@Composable
private fun VideoCommentHeader(pagerState: PagerState, onBack: () -> Unit) {
    val viewModel: CommentViewModel = hiltViewModel()
    var selectedIndex by remember { mutableStateOf(1) }
    val scopeState = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CommonIcon(
            resId = R.drawable.ic_video_comment_sheet_back,
            modifier = Modifier.size(44.cdp)
                .onClick(enableRipple = false) { onBack.invoke() },
            tint = AppColorsProvider.current.secondIcon
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.cdp)
                .padding(horizontal = 32.cdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = viewModel.title,
                color = AppColorsProvider.current.firstText,
                fontSize = 32.csp,
                fontWeight = FontWeight.Bold
            )

            CommonTabLayout(
                tabTexts = viewModel.commentSortTabs.map { it.title },
                backgroundColor = Color.Transparent,
                style = CommonTabLayoutStyle(isScrollable = false,
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
}
