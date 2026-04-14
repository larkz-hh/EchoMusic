package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep


@Keep
data class VideoGroupTabsResult(
    val data: List<VideoGroupTabBean>
) : BaseResult()

@Keep
data class VideoGroupTabBean(val id: Int, val name: String)