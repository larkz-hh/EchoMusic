package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep

@Keep
data class TopPlaylistResult(
    val playlists: List<SquarePlaylist>? = null,
    val total: Int? = null,
) : BaseResult()

@Keep
data class SquarePlaylist(
    val id: Long? = null,
    val name: String? = null,
    val coverImgUrl: String? = null,
    val playCount: Long? = null,
    val creator: Subscribers? = null,
)
