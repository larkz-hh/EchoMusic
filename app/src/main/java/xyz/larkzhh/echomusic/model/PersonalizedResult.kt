package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep

@Keep
data class PersonalizedResult(
    val loadingPic: String?,
    val bufferPic: String?,
    val loadingPicFS: String?,
    val bufferPicFS: String?,
    val subed: Boolean?,
    val mp: PersonalizedMp?,
    /** 每日推荐歌单等接口返回的列表（与下方 [data] 不同时存在） */
    val result: List<PersonalizedPlaylistRec>?,
    val data: PersonalizedData?,
) : BaseResult()

@Keep
data class PersonalizedPlaylistRec(
    val id: Long?,
    val name: String?,
    val picUrl: String?,
    val coverImgUrl: String?,
    val playCount: Long?,
)

@Keep
data class PersonalizedMp(
    val id: Long?,
    val fee: Int?,
    val mvFee: Int?,
    val payed: Int?,
    val pl: Int?,
    val dl: Int?,
    val cp: Int?,
    val sid: Long?,
    val st: Int?,
    val normal: Boolean?,
    val unauthorized: Boolean?,
    val msg: String?,
)

@Keep
data class PersonalizedData(
    val id: Long?,
    val name: String?,
    val artistId: Long?,
    val artistName: String?,
    val briefDesc: String?,
    val desc: String?,
    val cover: String?,
    val coverId_str: String?,
    val coverId: Long?,
    val playCount: Long?,
    val subCount: Long?,
    val shareCount: Long?,
    val commentCount: Long?,
    val duration: Long?,
    val nType: Int?,
    val publishTime: String?,
    val price: Any?,
    val brs: List<PersonalizedBr>?,
    val artists: List<PersonalizedArtist>?,
    val commentThreadId: String?,
    val videoGroup: List<PersonalizedVideoGroup>?,
)

@Keep
data class PersonalizedBr(
    val size: Long?,
    val br: Int?,
    val point: Int?,
)

@Keep
data class PersonalizedArtist(
    val id: Long?,
    val name: String?,
    val img1v1Url: String?,
    val followed: Boolean?,
)

@Keep
data class PersonalizedVideoGroup(
    val id: Long?,
    val name: String?,
    val type: Int?,
)
