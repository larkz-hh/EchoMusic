package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep

@Keep
data class MvDetailResult(
    val loadingPic: String?,
    val bufferPic: String?,
    val loadingPicFS: String?,
    val bufferPicFS: String?,
    val subed: Boolean?,
    val mp: MvDetailPrivilege?,
    val data: MvDetailData?,
) : BaseResult()

@Keep
data class MvDetailPrivilege(
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
data class MvDetailData(
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
    val brs: List<MvDetailBr>?,
    val artists: List<MvDetailArtist>?,
    val commentThreadId: String?,
    val videoGroup: List<MvDetailVideoGroup>?,
)

@Keep
data class MvDetailBr(
    val size: Long?,
    val br: Int?,
    val point: Int?,
)

@Keep
data class MvDetailArtist(
    val id: Long?,
    val name: String?,
    val img1v1Url: String?,
    val followed: Boolean?,
)

@Keep
data class MvDetailVideoGroup(
    val id: Long?,
    val name: String?,
    val type: Int?,
)

