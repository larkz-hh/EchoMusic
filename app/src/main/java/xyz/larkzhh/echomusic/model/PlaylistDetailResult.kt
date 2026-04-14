package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep


@Keep
data class PlaylistDetailResult(
    val playlist: PlaylistBean,
) : BaseResult()