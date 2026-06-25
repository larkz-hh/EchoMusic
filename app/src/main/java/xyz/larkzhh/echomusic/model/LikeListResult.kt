package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep

@Keep
data class LikeListResult(
    val ids: List<Long>? = null,
) : BaseResult()
