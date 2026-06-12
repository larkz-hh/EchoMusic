package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep

@Keep
data class SearchResult(
    val result: SearchResultData? = null,
) : BaseResult()

@Keep
data class SearchResultData(
    val songs: List<SearchSong>? = null,
    val hasMore: Boolean? = null,
    val songCount: Int? = null,
)

@Keep
data class SearchSong(
    val id: Long? = null,
    val name: String? = null,
    val ar: List<SearchArtist>? = null,
    val al: SearchAlbum? = null,
    val dt: Long? = null,
    val mv: Long? = null,
)

@Keep
data class SearchAlbum(
    val id: Long? = null,
    val name: String? = null,
    val picUrl: String? = null,
)

@Keep
data class SearchArtist(
    val id: Long? = null,
    val name: String? = null,
)
