package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep

@Keep
data class TopListResult(
    val list: List<TopListItem>?,
) : BaseResult()

@Keep
data class TopListItem(
    val tracks: List<TopListTrack>?,
    val updateFrequency: String?,
    val coverImgUrl: String?,
    val playCount: Long?,
    val subscribed: Boolean?,
    val subscribedCount: Long?,
    val specialType: Int?,
    val coverImgId: Long?,
    val coverImgId_str: String?,
    val name: String?,
    val id: Long?,
    val status: Int?,
    val tags: List<String>?,
    val description: String?,
    val ToplistType: String?,
    val updateTime: Long?,
) {
}

@Keep
data class TopListTrack(
    val first: String?,
    val second: String?,
)

