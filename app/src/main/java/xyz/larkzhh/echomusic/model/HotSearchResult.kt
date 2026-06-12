package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep

@Keep
data class HotSearchResult(
    val data: List<HotSearchItem>? = null,
) : BaseResult()

@Keep
data class HotSearchItem(
    val searchWord: String = "",
    val score: Long = 0,
)
