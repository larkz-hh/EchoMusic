package xyz.larkzhh.echomusic.ui.page.cloudcountry.component

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import xyz.larkzhh.echomusic.core.AppConfig
import xyz.larkzhh.echomusic.model.Video
import xyz.larkzhh.echomusic.utils.onClick
import xyz.larkzhh.echomusic.compose.CommonIcon
import xyz.larkzhh.echomusic.compose.CommonNetworkImage
import xyz.larkzhh.echomusic.compose.SeekBar
import xyz.larkzhh.echomusic.utils.ScreenUtil
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.toPx
import xyz.larkzhh.echomusic.utils.transformDp
import xyz.larkzhh.echomusic.ui.page.comment.videoCommentSheetOffset
import xyz.larkzhh.echomusic.utils.*
import xyz.larkzhh.echomusic.viewmodel.VideoPlayViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs
import xyz.larkzhh.echomusic.R
import androidx.compose.foundation.layout.navigationBars


@Composable
fun CpnVideoPlay(index: Int, lazyListState: LazyListState, video: Video) {
    val viewModel: VideoPlayViewModel = hiltViewModel()

    // 分页数据里没有互动数，按 id 补
    LaunchedEffect(video.vid) {
        viewModel.loadVideoStats(video.vid)
    }

    if (video.urls?.getOrNull(0) == null) {
        LaunchedEffect(Unit) {
            if(index == 0) {
                viewModel.curVideoId = video.vid
            }
            viewModel.ensureVideoUrl(index, video.vid)
        }
    }
    // 预加载
    val navigationBarHeight =  ScreenUtil.getNavigationBarHeight()
    val itemHeight = ScreenUtil.getScreenHeight().transformDp - navigationBarHeight.transformDp - 1.cdp
    val videoWidth = video.width
    val videoHeight = video.height
    val cpnWidth = AppConfig.APP_DESIGN_WIDTH.cdp
    val cpnHeight = ((AppConfig.APP_DESIGN_WIDTH.toFloat() / videoWidth) * videoHeight).cdp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight)
            .videoDragDetect(index, lazyListState)
    ) {

        CpnVideoSurface(index, cpnWidth, cpnHeight, video)

        CpnVideoInfo(video, viewModel.videoStats[video.vid])
        VideoSeekBar(index, video)
    }
}

private fun Modifier.videoDragDetect(
    curIndex: Int,
    lazyListState: LazyListState
) = composed {
    val viewModel: VideoPlayViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    var totalDragAmount = remember { 0f }
    val threshold = remember { ScreenUtil.getScreenHeight() / 8 }

    this.pointerInput(Unit) {
        detectVerticalDragGestures(
            onDragStart = {
                totalDragAmount = 0f
                viewModel.videoInfoAlpha = 1f
            },
            onDragEnd = {
                var newIndex = curIndex
                if (totalDragAmount < 0) {  // 向上滑动
                    if (totalDragAmount < -threshold) {
                        newIndex = curIndex + 1
                        viewModel.switchVideoUrl(newIndex)
                    }
                } else {  //向下滑动
                    if (totalDragAmount > threshold) {
                        newIndex = 0.coerceAtLeast(curIndex - 1)
                        if (newIndex != curIndex) {
                            viewModel.switchVideoUrl(newIndex)
                        }
                    }
                }
                viewModel.videoInfoAlpha = 1f
                scope.launch {
                    lazyListState.animateScrollToItem(newIndex)
                    viewModel.showVideoInfo = true
                    totalDragAmount = 0f
                }
            }
        ) { _, dragAmount ->
            // dragAmount 向上滑动为负
            totalDragAmount += dragAmount
            viewModel.videoInfoAlpha = 0.3f.coerceAtLeast((threshold - abs(totalDragAmount)) / threshold)
            lazyListState.dispatchRawDelta(-dragAmount)
        }
    }
}

@Composable
private fun CpnVideoSurface(index: Int, cpnWidth: Dp, cpnHeight: Dp, video: Video) {
    val viewModel: VideoPlayViewModel = hiltViewModel()
    val navigationBarHeight =  ScreenUtil.getNavigationBarHeight()
    val originMaxVideoHeightPx = remember {
        ScreenUtil.getScreenHeight() - navigationBarHeight - cpnBottomSendCommentHeight.toPx
    }
    val maxHeight = videoCommentSheetOffset.coerceAtMost(originMaxVideoHeightPx).transformDp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(maxHeight),
        contentAlignment = Alignment.Center
    ) {

        // 同一位置只能挂一个 PlayerView，按列表下标判断，避免重复视频 id 同时挂载
        if (index == viewModel.curVideoIndex && viewModel.exoPlayStatus == Player.STATE_READY) {
            AndroidView(
                    modifier = Modifier
                        .aspectRatio(video.width.toFloat() / video.height)
                        .onClick(enableRipple = false) {
                            if (viewModel.videoPlaying) {
                                viewModel.pauseVideo()
                            } else {
                                viewModel.resumeVideo()
                            }
                        },
                    factory = { context ->
                        PlayerView(context).apply {
                            useController = false
                            player = viewModel.exoPlayer
                        }
                    },
                    update = { view -> view.player = viewModel.exoPlayer })

                if (!viewModel.videoPlaying) {
                    CommonIcon(
                        resId = R.drawable.ic_video_play,
                        modifier = Modifier.size(100.cdp),
                        tint = Color.White
                    )
                }
        } else {
            CommonNetworkImage(
                url = video.coverUrl,
                modifier = Modifier
                    .width(cpnWidth)
                    .height(cpnHeight),
                placeholder = -1,
                error = -1
            )
        }
    }
}

@Composable
private fun BoxScope.VideoSeekBar(index: Int, video: Video) {
    val viewModel: VideoPlayViewModel = hiltViewModel()
    if (index == viewModel.curVideoIndex) {
        SeekBar(
            progress = viewModel.videoProgress,
            seeking = {
                viewModel.seeking(it)
            },
            seekTo = {
                viewModel.seekTo(it)
            },
            progressHeight = 2f,
            progressColor = Color.White.copy(0.3f),
            circleColor = Color.LightGray,
            modifier = Modifier
                .padding(bottom = cpnBottomSendCommentHeight)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}


val cpnBottomSendCommentHeight = 100.cdp
