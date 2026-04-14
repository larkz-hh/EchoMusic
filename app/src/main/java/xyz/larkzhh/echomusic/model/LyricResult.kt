package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep


@Keep
class LyricResult(
    val transUser: LyricContributorBean?,
    val lyricUser: LyricContributorBean?,
    val lrc: LrcBean?,
    val tlyric: LrcBean?
) : BaseResult()

@Keep
data class LyricContributorBean(
    val id: Long,
    val status: Int,
    val demand: Int,
    val userid: Long,
    val nickname: String,
    val uptime: Long
)



@Keep
data class LrcBean(
    val version: Int,
    val lyric: String
)