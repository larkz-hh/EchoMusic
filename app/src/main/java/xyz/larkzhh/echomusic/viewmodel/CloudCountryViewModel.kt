package xyz.larkzhh.echomusic.viewmodel

import android.util.Log
import androidx.paging.PagingData
import xyz.larkzhh.echomusic.core.viewstate.BaseViewStateViewModel
import xyz.larkzhh.echomusic.core.viewstate.ViewStateMutableStateFlow
import xyz.larkzhh.echomusic.core.viewstate.paging.AppPagingConfig
import xyz.larkzhh.echomusic.core.viewstate.paging.buildPager
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.model.MvAllItem
import xyz.larkzhh.echomusic.model.VideoGroupTabsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class CloudCountryViewModel @Inject constructor(
    private val api: NeteaseApi,
) : BaseViewStateViewModel() {
    val areas = listOf("全部", "内地", "港台", "欧美", "日本", "韩国")
    val videoGroupTabsResult = ViewStateMutableStateFlow<VideoGroupTabsResult>()


    fun getVideoGroupTabs() {
        launch(videoGroupTabsResult) {
            api.getVideoGroupTabs()
        }
    }

    fun buildVideoGroupPager(index: Int): Flow<PagingData<MvAllItem>> {
        return buildPager(
            config = AppPagingConfig(
                pageSize = 10,
                initialLoadSize = 10,
                prefetchDistance = 2,
                enableLoadMore = true,
            ),
            transformListBlock = { it?.data },
        ) { curPage, pageSize ->
            val area = areas.getOrElse(index) { "全部" }
            api.getMvAll(
                area = area,
                offset = (curPage - 1) * pageSize,
                limit = pageSize,
            )
        }
    }
}