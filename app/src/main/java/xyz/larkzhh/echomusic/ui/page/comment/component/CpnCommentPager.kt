package xyz.larkzhh.echomusic.ui.page.comment.component

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.itemKey
import xyz.larkzhh.echomusic.core.viewstate.ViewStateListPagingComponent
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.viewmodel.CommentViewModel
import xyz.larkzhh.echomusic.viewmodel.FloorCommentViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CpnCommentPager(id: String, sortType: Int, type: Int) {
    val viewModel: CommentViewModel = hiltViewModel()

    ViewStateListPagingComponent(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorsProvider.current.background),
        key = "CpnCommentPager-${sortType}-${type}",
        loadDataBlock = { viewModel.buildNewCommentListPager(id, sortType, type) },
        viewStateComponentModifier = Modifier
            .fillMaxSize()
            .background(AppColorsProvider.current.background),
        viewStateContentAlignment = BiasAlignment(0f, -0.6f),
        enableRefresh = false
    ) { commentBeanList ->
//        items(commentBeanList) { index, data ->
//            data?.let {
//                CpnCommentItem(data) { commentBean ->
//                    FloorCommentViewModel.floorOwnerCommentId = commentBean.commentId
//                    FloorCommentViewModel.showFloorCommentSheet = true
//                }
//            }
//        }
        items(
            count = commentBeanList.itemCount,
            key = commentBeanList.itemKey { it.commentId }
        ) { index ->
            commentBeanList[index]?.let { commentBean ->
                CpnCommentItem(comment = commentBean) { clicked ->
                    FloorCommentViewModel.floorOwnerCommentId = clicked.commentId
                    FloorCommentViewModel.showFloorCommentSheet = true
                }
            }
        }
    }
}





