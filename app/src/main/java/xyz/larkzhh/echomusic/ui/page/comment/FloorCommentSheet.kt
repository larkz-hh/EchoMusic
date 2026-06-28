package xyz.larkzhh.echomusic.ui.page.comment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.itemKey
import xyz.larkzhh.echomusic.core.viewstate.ViewStateListPagingComponent
import xyz.larkzhh.echomusic.compose.CommonTopAppBar
import xyz.larkzhh.echomusic.ui.page.comment.component.CpnCommentItem
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.viewmodel.FloorCommentViewModel
import kotlinx.coroutines.launch


@Composable
fun FloorCommentSheet(type: Int) {
    if (FloorCommentViewModel.showFloorCommentSheet) {
        FloorCommentSheetContent(type)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FloorCommentSheetContent(type: Int) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BackHandler(FloorCommentViewModel.showFloorCommentSheet) {
        scope.launch {
            sheetState.hide()
            FloorCommentViewModel.showFloorCommentSheet = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            FloorCommentViewModel.showFloorCommentSheet = false
        },
        sheetState = sheetState,
        containerColor = Color.Transparent
    ) {
        FloorCommentList(type) {
            scope.launch {
                sheetState.hide()
                FloorCommentViewModel.showFloorCommentSheet = false
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FloorCommentList(type: Int, onBack: () -> Unit) {
    val key = "FloorCommentViewModel-${FloorCommentViewModel.floorOwnerCommentId}"
    val viewModel: FloorCommentViewModel = hiltViewModel(key = key)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
            .clip(RoundedCornerShape(topStart = 40.cdp, topEnd = 40.cdp))
            .background(AppColorsProvider.current.background)
            .padding(top = 16.cdp)
    ) {

        CommonTopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.cdp),
            title = viewModel.floorCommentTitle,
            titleAlign = TextAlign.Left,
            backgroundColor = Color.Transparent,
            leftClick = {
                onBack()
            },
        )

        ViewStateListPagingComponent(
            loadDataBlock = { viewModel.getFloorCommentResult(type) },
            viewStateContentAlignment = BiasAlignment(0f, -0.6f),
            enableRefresh = false
        ) { commentBeanList ->
            viewModel.floorOwnerCommentBean?.let {
                item {
                    Column {
                        CpnCommentItem(
                            comment = it,
                            isFloorComment = true,
                            resourceId = FloorCommentViewModel.resourceId,
                            commentType = type
                        )
                        Divider(
                            color = AppColorsProvider.current.divider.copy(0.6f),
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 20.cdp
                        )
                    }
                }
            }
            items(
                count = commentBeanList.itemCount,
                key = commentBeanList.itemKey { it.commentId }
            ) { index ->
                val item = commentBeanList[index]
                item?.let {
                    CpnCommentItem(
                        comment = it,
                        isFloorComment = true,
                        resourceId = FloorCommentViewModel.resourceId,
                        commentType = type
                    )
                }
            }
        }
    }
}
