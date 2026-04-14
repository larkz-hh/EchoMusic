package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep

@Keep
data class MvUrlResult(
    val data: MvUrlData?,
) : BaseResult()

@Keep
data class MvUrlData(
    val id: Long?,
    val url: String?,
    val r: Int?,
    val size: Long?,
    val md5: String?,
    val code: Int?,
    val expi: Int?,
    val fee: Int?,
    val mvFee: Int?,
    val st: Int?,
    val promotionVo: Any?,
    val msg: String?,
)

