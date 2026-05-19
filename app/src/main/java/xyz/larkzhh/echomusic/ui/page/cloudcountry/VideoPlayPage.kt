package xyz.larkzhh.echomusic.ui.page.cloudcountry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.paging.compose.collectAsLazyPagingItems
import xyz.larkzhh.echomusic.core.viewstate.listener.ComposeLifeCycleListener
import xyz.larkzhh.echomusic.model.MvAllItem
import xyz.larkzhh.echomusic.model.Subscribers
import xyz.larkzhh.echomusic.model.Video
import xyz.larkzhh.echomusic.compose.CommonTopAppBar
import xyz.larkzhh.echomusic.compose.LifeCycleObserverComponent
import xyz.larkzhh.echomusic.ui.page.cloudcountry.component.CpnVideoPlay
import xyz.larkzhh.echomusic.ui.page.comment.FloorCommentSheet
import xyz.larkzhh.echomusic.ui.page.comment.VideoCommentSheet
import xyz.larkzhh.echomusic.ui.page.comment.showVideoCommentSheet
import xyz.larkzhh.echomusic.viewmodel.VideoPlayViewModel
import xyz.larkzhh.echomusic.viewmodel.CommentViewModel


/**
 * 歌曲播放页面
 */
@Composable
fun PlayVideoPage(firstVideo: Video, videoGroupId: Int, videoOffsetIndex: Int) {
    val viewModel: VideoPlayViewModel = hiltViewModel()
    viewModel.firstVideo = firstVideo
    viewModel.initExoPlayerIfNeeded(LocalContext.current)
    LaunchedEffect(firstVideo.vid) {
        viewModel.getMvDetailAndFill(firstVideo.vid)
    }

    LifeCycleObserverComponent(lifeCycleListener = object : ComposeLifeCycleListener {
        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            viewModel.pauseVideo()
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            viewModel.resumeVideo()
        }
    }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            VideoList(videoGroupId, videoOffsetIndex)

            if(!showVideoCommentSheet) {
                CommonTopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding(),
                    backgroundColor = Color.Transparent,
                    contentColor = Color.White
                )
            }
        }
    }
}

@Composable
private fun VideoList(videoGroupId: Int, videoOffsetIndex: Int) {
    val viewModel: VideoPlayViewModel = hiltViewModel()
    if (viewModel.videoPagingItems == null) {
        // 获取MV列表，供上下滑切换
        viewModel.buildVideoPager(videoGroupId, videoOffsetIndex)
    }
    val mvPagingItems = viewModel.videoFlows?.collectAsLazyPagingItems()
    viewModel.videoPagingItems = mvPagingItems

    val lazyListState = rememberLazyListState()

    LaunchedEffect(viewModel.curVideoUrl) {
        if (viewModel.curVideoUrl != null) {
            viewModel.loadVideo(viewModel.curVideoUrl!!)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = lazyListState
    ) {
        item {
            CpnVideoPlay(0, lazyListState, viewModel.firstVideo)
        }

        mvPagingItems?.let { items ->
            items(items.itemCount) { index ->
                items[index]?.let { mv ->
                    CpnVideoPlay(index + 1, lazyListState, mv.toVideo())
                }
            }
        }
    }

    VideoCommentSheet()
    FloorCommentSheet(CommentViewModel.TYPE_VIDEO)
}

private fun MvAllItem.toVideo(): Video {
    return Video(
        scm = null,
        coverUrl = cover,
        height = 1920,
        width = 1080,
        title = name.orEmpty(),
        description = desc ?: briefDesc.orEmpty(),
        commentCount = 0,
        shareCount = 0,
        creator = Subscribers(
            userId = artistId ?: 0L,
            avatarUrl = "",
            nickname = artistName ?: "未知"
        ),
        previewUrl = "",
        relateSong = null,
        vid = id?.toString(),
        durationms = duration?.toInt() ?: 0,
        playTime = playCount?.toInt() ?: 0,
        praisedCount = 0
    )
}


