package xyz.larkzhh.echomusic.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.paging.PagingData
import xyz.larkzhh.echomusic.core.viewstate.BaseViewStateViewModel
import xyz.larkzhh.echomusic.core.viewstate.paging.buildPager
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.model.CommentBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class CommentViewModel @Inject constructor(private val api: NeteaseApi) : BaseViewStateViewModel() {

    companion object {
        // 歌曲
        const val TYPE_SONG = 0
        // 视频
        const val TYPE_VIDEO = 5
    }

    val commentSortTabs = listOf(
        CommentSortTab("推荐", 1),
        CommentSortTab("最热", 2),
        CommentSortTab("最新", 3)
    )
    var title by mutableStateOf("评论")
    private var cursors = HashMap<Int, String>()

    fun buildNewCommentListPager(id: String, sortType: Int, type: Int): Flow<PagingData<CommentBean>> {
        FloorCommentViewModel.Companion.resourceId = id

        return buildPager(
            transformListBlock = {
                cursors[sortType] = it?.data?.cursor ?: ""
                val commentCount = it?.data?.totalCount ?: 0
                title = if (commentCount > 0) "评论(${commentCount})" else "评论"
                it?.data?.comments
            }) { curPage, pageSize ->
            api.getNewComment(
                id = id,
                type = type,
                sortType = sortType,
                cursor = cursors[sortType] ?: "",
                pageSize = pageSize,
                pageNo = curPage
            )
        }
    }
}


data class CommentSortTab(var title: String, var type: Int)
