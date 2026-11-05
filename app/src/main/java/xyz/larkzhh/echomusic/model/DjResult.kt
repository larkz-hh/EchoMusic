package xyz.larkzhh.echomusic.model

import androidx.annotation.Keep

/** 推荐电台 */
@Keep
data class DjRecommendResult(
    val data: List<DjRadio>? = null,
) : BaseResult()

/** 电台详情 */
@Keep
data class DjDetailResult(
    val data: DjRadio? = null,
) : BaseResult()

/** 电台节目列表 */
@Keep
data class DjProgramResult(
    val programs: List<DjProgram>? = null,
    val more: Boolean? = null,
    val count: Int = 0,
) : BaseResult()

@Keep
data class DjRadio(
    val id: Long = 0,
    val name: String? = null,
    val picUrl: String? = null,
    val desc: String? = null,
    val programCount: Int = 0,
    val subCount: Long = 0,
    val playCount: Long = 0,
    val dj: DjUser? = null,
)

@Keep
data class DjUser(
    val nickname: String? = null,
    val avatarUrl: String? = null,
)

@Keep
data class DjProgram(
    val id: Long = 0,
    val name: String? = null,
    val mainTrackId: Long = 0,
    val duration: Long = 0,
    val coverUrl: String? = null,
    val description: String? = null,
    val listenerCount: Int = 0,
    val mainSong: DjMainSong? = null,
)

@Keep
data class DjMainSong(
    val id: Long = 0,
    val name: String? = null,
    val duration: Long = 0,
    val artists: List<DjArtist>? = null,
    val album: DjAlbum? = null,
)

@Keep
data class DjArtist(
    val id: Long = 0,
    val name: String? = null,
)

@Keep
data class DjAlbum(
    val id: Long = 0,
    val name: String? = null,
    val picUrl: String? = null,
)
