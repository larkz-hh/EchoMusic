package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep

@Keep
data class SingleListResult(
    val artists: List<SingleArtist>?,
    val more: Boolean?,
) : BaseResult()

@Keep
data class SingleArtist(
    val accountId: Long?,
    val albumSize: Int?,
    val alias: List<String>?,
    val briefDesc: String?,
    val fansCount: Long?,
    val followed: Boolean?,
    val id: Long?,
    val img1v1Id: Long?,
    val img1v1Id_str: String?,
    val img1v1Url: String?,
    val musicSize: Int?,
    val name: String?,
    val picId: Long?,
    val picId_str: String?,
    val picUrl: String?,
    val topicPerson: Int?,
    val trans: String?,
)

