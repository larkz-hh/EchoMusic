package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep

@Keep
data class MvFirstResult(
    val data: List<MvFirstItem>?,
) : BaseResult()

@Keep
data class MvFirstItem(
    val id: Long?,
    val cover: String?,
    val name: String?,
    val playCount: Long?,
    val briefDesc: String?,
    val desc: String?,
    val artistName: String?,
    val artistId: Long?,
    val duration: Long?,
    val mark: Long?,
    val subed: Boolean?,
    val artists: List<MvFirstArtist>?,
    val alias: List<String>?,
)

@Keep
data class MvFirstArtist(
    val id: Long?,
    val name: String?,
)
