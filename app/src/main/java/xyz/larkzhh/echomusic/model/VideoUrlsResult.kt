package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep


@Keep
data class VideoUrlsResult(
    val urls: List<VideoUrlBean>
): BaseResult()

@Keep
data class VideoUrlBean(
    val id: String,
    val url: String,
    val size: Long,
    val validityTime: Int,
    val needPay: Boolean,
    val r: Int
)