package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep

@Keep
data class SearchResult(
    val result: SearchResultData?,
    val trp: SearchTrp?,
) : BaseResult()

@Keep
data class SearchResultData(
    val songs: List<SearchSong>?,
    val hasMore: Boolean?,
    val songCount: Int?,
)

@Keep
data class SearchSong(
    val album: SearchAlbum?,
    val fee: Int?,
    val duration: Long?,
    val rtype: Int?,
    val ftype: Int?,
    val artists: List<SearchArtist>?,
    val copyrightId: Long?,
    val mvid: Long?,
    val name: String?,
    val alias: List<String>?,
    val id: Long?,
    val mark: Long?,
    val status: Int?,
    val transNames: List<String>?,
)

@Keep
data class SearchAlbum(
    val publishTime: Long?,
    val size: Int?,
    val artist: SearchArtist?,
    val copyrightId: Long?,
    val name: String?,
    val id: Long?,
    val picId: Long?,
    val mark: Long?,
    val status: Int?,
    val alia: List<String>?,
)

@Keep
data class SearchArtist(
    val img1v1Url: String?,
    val musicSize: Int?,
    val albumSize: Int?,
    val img1v1: Long?,
    val name: String?,
    val alias: List<String>?,
    val id: Long?,
    val picId: Long?,
)

@Keep
data class SearchTrp(
    val rules: List<String>?,
)

