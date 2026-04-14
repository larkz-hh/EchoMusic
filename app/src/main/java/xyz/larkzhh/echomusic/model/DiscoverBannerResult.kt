package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep

@Keep
data class DiscoverBannerResult(
    val trp: DiscoverBannerTrp?,
    val banners: List<DiscoverBannerItem>?,
) : BaseResult()

@Keep
data class DiscoverBannerTrp(
    val rules: List<String>?,
)

@Keep
data class DiscoverBannerItem(
    val targetId: Long?,
    val bigImageUrl: String?,
    val imageUrl: String?,
    val targetType: Int?,
    val typeTitle: String?,
    val s_ctrp: String?,
    val url: String?,
)
