package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep

@Keep
data class MvAllResult(
    val count: Int?,
    val hasMore: Boolean?,
    val data: List<MvAllItem>?,
) : BaseResult()

@Keep
data class MvAllItem(
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
    val artists: List<MvAllArtist>?,
    val transNames: List<String>?,
)

@Keep
data class MvAllArtist(
    val id: Long?,
    val name: String?,
    val alias: List<String>?,
    val transNames: List<String>?,
)

