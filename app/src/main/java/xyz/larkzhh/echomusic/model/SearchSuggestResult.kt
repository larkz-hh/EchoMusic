package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep

@Keep
data class SearchSuggestResult(
    val result: SuggestResult? = null,
) : BaseResult()

@Keep
data class SuggestResult(
    val allMatch: List<SuggestMatch>? = null,
)

@Keep
data class SuggestMatch(
    val keyword: String = "",
)
