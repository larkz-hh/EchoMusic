package xyz.larkzhh.echomusic.core.viewstate.paging


const val DEFAULT_EVERY_PAGE_SIZE = 20
const val DEFAULT_INITIAL_LOAD_SIZE = 20
const val DEFAULT_PREFETCH_DISTANCE = 4

data class AppPagingConfig(
    val pageSize: Int = DEFAULT_EVERY_PAGE_SIZE,
    val initialLoadSize: Int = DEFAULT_INITIAL_LOAD_SIZE,
    val prefetchDistance:Int = DEFAULT_PREFETCH_DISTANCE,
    val maxSize:Int = Int.MAX_VALUE,
    val enablePlaceholders:Boolean = false,
    val enableLoadMore: Boolean = true,
    val minRequestCycle: Long = 500L
)