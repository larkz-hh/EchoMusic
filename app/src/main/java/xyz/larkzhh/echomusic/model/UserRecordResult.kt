package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep


/** 听歌排行 */
@Keep
data class UserRecordResult(
    val allData: List<UserRecordItem> = emptyList(),
    val weekData: List<UserRecordItem> = emptyList(),
) : BaseResult()

@Keep
data class UserRecordItem(
    val playCount: Int = 0,
    val score: Long = 0,
    val song: SongBean,
)
