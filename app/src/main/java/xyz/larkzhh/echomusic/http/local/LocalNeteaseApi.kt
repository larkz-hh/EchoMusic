package xyz.larkzhh.echomusic.http.local

import com.google.gson.annotations.SerializedName
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.model.AccountInfoResult
import xyz.larkzhh.echomusic.model.BaseResult
import xyz.larkzhh.echomusic.model.DiscoverBannerResult
import xyz.larkzhh.echomusic.model.FloorCommentResult
import xyz.larkzhh.echomusic.model.HotSearchResult
import xyz.larkzhh.echomusic.model.LikeListResult
import xyz.larkzhh.echomusic.model.LyricResult
import xyz.larkzhh.echomusic.model.MvAllResult
import xyz.larkzhh.echomusic.model.MvDetailResult
import xyz.larkzhh.echomusic.model.MvFirstResult
import xyz.larkzhh.echomusic.model.MvUrlResult
import xyz.larkzhh.echomusic.model.NewCommentResult
import xyz.larkzhh.echomusic.model.PersonalizedResult
import xyz.larkzhh.echomusic.model.PlaylistDetailResult
import xyz.larkzhh.echomusic.model.QrcodeAuthResult
import xyz.larkzhh.echomusic.model.QrcodeKeyBean
import xyz.larkzhh.echomusic.model.QrcodeKeyResult
import xyz.larkzhh.echomusic.model.QrcodeValueBean
import xyz.larkzhh.echomusic.model.QrcodeValueResult
import xyz.larkzhh.echomusic.model.SearchResult
import xyz.larkzhh.echomusic.model.SearchSuggestResult
import xyz.larkzhh.echomusic.model.SingleListResult
import xyz.larkzhh.echomusic.model.SongCommentResult
import xyz.larkzhh.echomusic.model.SongDetailResult
import xyz.larkzhh.echomusic.model.SongUrlBean
import xyz.larkzhh.echomusic.model.TopListResult
import xyz.larkzhh.echomusic.model.UserPlaylistResult
import xyz.larkzhh.echomusic.model.UserRecordResult
import xyz.larkzhh.echomusic.model.VideoGroupResult
import xyz.larkzhh.echomusic.model.VideoGroupTabsResult
import xyz.larkzhh.echomusic.model.VideoUrlsResult
import java.io.IOException

/** 直连某抑云，不经过本地 Node 服务 */
class LocalNeteaseApi(
    private val transport: WeapiTransport = WeapiTransport(),
) : NeteaseApi {

    private suspend inline fun <reified T> post(path: String, data: Map<String, Any?>): T =
        transport.post(path, data, T::class.java)

    override suspend fun getLoginQrcodeKey(timeStamp: Long): QrcodeKeyResult {
        val raw = post<RawQrcodeKey>("/weapi/login/qrcode/unikey", mapOf("type" to 1))
        return QrcodeKeyResult(QrcodeKeyBean(raw.unikey)).apply { code = 200 }
    }

    override suspend fun getLoginQrcodeValue(key: String, timeStamp: Long): QrcodeValueResult {
        val url = "https://music.163.com/login?codekey=$key"
        return QrcodeValueResult(QrcodeValueBean(url, null)).apply { code = 200 }
    }

    override suspend fun checkQrcodeAuthStatus(key: String, timeStamp: Long): QrcodeAuthResult =
        post("/weapi/login/qrcode/client/login", mapOf("key" to key, "type" to 1))

    override suspend fun getAccountInfo(cookie: String?): AccountInfoResult =
        transport.post("/weapi/w/nuser/account/get", emptyMap(), AccountInfoResult::class.java, cookie)

    override suspend fun getUserRecord(uid: String, type: Int): UserRecordResult =
        post("/weapi/v1/play/record", mapOf("uid" to uid, "type" to type))

    override suspend fun getUserPlayList(uid: String): UserPlaylistResult =
        post(
            "/weapi/user/playlist",
            mapOf("uid" to uid, "limit" to 1000, "offset" to 0, "includeVideo" to true)
        )

    override suspend fun getHotSearch(): HotSearchResult =
        post("/weapi/hotsearchlist/get", emptyMap())

    override suspend fun getSearchSuggest(keywords: String, type: String): SearchSuggestResult =
        post("/weapi/search/suggest/keyword", mapOf("s" to keywords))

    override suspend fun scrobble(id: Long, sourceId: Long, time: Long): BaseResult {
        val logs = """[{"action":"play","json":{"download":0,"end":"playend","id":$id,""" +
            """"sourceId":$sourceId,"time":$time,"type":"song","wifi":0,"source":"list",""" +
            """"mainsite":1,"content":""}}]"""
        return post("/weapi/feedback/weblog", mapOf("logs" to logs))
    }

    override suspend fun like(id: Long, like: Boolean): BaseResult =
        post(
            "/weapi/radio/like",
            mapOf("alg" to "itembased", "trackId" to id, "like" to like, "time" to "3")
        )

    override suspend fun getLikeList(uid: String): LikeListResult =
        post("/weapi/song/like/get", mapOf("uid" to uid))

    override suspend fun likeComment(id: String, commentId: Long, like: Int, type: Int): BaseResult {
        val action = if (like == 1) "like" else "unlike"
        return post(
            "/weapi/v1/comment/$action",
            mapOf("threadId" to threadId(type, id), "commentId" to commentId)
        )
    }

    override suspend fun getPlaylistDetail(id: Long): PlaylistDetailResult =
        post("/weapi/v6/playlist/detail", mapOf("id" to id, "n" to 100000, "s" to 8))

    override suspend fun getSongDetail(ids: String): SongDetailResult {
        val c = ids.split(",")
            .filter { it.isNotBlank() }
            .joinToString(",", "[", "]") { """{"id":$it}""" }
        return post("/weapi/v3/song/detail", mapOf("c" to c))
    }

    override suspend fun getSearch(keywords: String, type: Int, limit: Int): SearchResult =
        post(
            "/weapi/cloudsearch/pc",
            mapOf("s" to keywords, "type" to type, "limit" to limit, "offset" to 0, "total" to true)
        )

    override suspend fun getSongUrl(id: Long, br: Int): SongUrlBean =
        post("/weapi/song/enhance/player/url", mapOf("ids" to "[$id]", "br" to br))

    override suspend fun getSongComment(id: Long, limit: Int, offset: Int): SongCommentResult =
        post(
            "/weapi/v1/resource/comments/R_SO_4_$id",
            mapOf("rid" to id, "limit" to limit, "offset" to offset, "beforeTime" to 0)
        )

    override suspend fun getNewComment(
        id: String,
        type: Int,
        pageNo: Int,
        pageSize: Int,
        sortType: Int,
        cursor: String,
    ): NewCommentResult = post(
        "/weapi/v2/resource/comments/${threadId(type, id)}",
        mapOf(
            "cursor" to cursor.ifBlank { ((pageNo - 1) * pageSize).toString() },
            "pageSize" to pageSize,
            "sortType" to sortType,
            "showInner" to true,
        )
    )

    override suspend fun getCommentFloor(
        parentCommentId: Long,
        id: String,
        type: Int,
        limit: Int,
        time: Long,
    ): FloorCommentResult = throw IOException("本地模式暂不支持楼层评论")

    override suspend fun getLyric(id: Long): LyricResult =
        post(
            "/weapi/song/lyric",
            mapOf("id" to id, "tv" to -1, "lv" to -1, "rv" to -1, "kv" to -1, "_nmclfl" to 1)
        )

    override suspend fun getVideoGroupTabs(): VideoGroupTabsResult =
        throw IOException("本地模式暂不支持视频分组")

    override suspend fun getVideoGroup(id: Int, offset: Int): VideoGroupResult =
        throw IOException("本地模式暂不支持视频分组")

    override suspend fun getMvAll(area: String, offset: Int, limit: Int): MvAllResult {
        val tags = """{"地区":"${if (area.isBlank()) "全部" else area}","类型":"全部","排序":"上升最快"}"""
        return post(
            "/weapi/mv/all",
            mapOf("tags" to tags, "offset" to offset, "total" to "true", "limit" to limit)
        )
    }

    override suspend fun getMvDetail(mvid: String): MvDetailResult =
        post("/weapi/v1/mv/detail", mapOf("id" to mvid))

    override suspend fun getMvUrl(id: String): MvUrlResult =
        post("/weapi/song/enhance/play/mv/url", mapOf("id" to id, "r" to 1080))

    override suspend fun getDiscoverBanner(): DiscoverBannerResult =
        post("/weapi/v2/banner/get", mapOf("clientType" to "android"))

    override suspend fun getPersonalized(limit: Int): PersonalizedResult =
        post("/weapi/personalized/playlist", mapOf("limit" to limit, "total" to true, "n" to 1000))

    override suspend fun getMvFirst(limit: Int): MvFirstResult =
        post("/weapi/mv/first", mapOf("area" to "", "limit" to limit, "total" to true))

    override suspend fun getVideoUrl(id: String): VideoUrlsResult =
        throw IOException("本地模式暂不支持视频播放")

    override suspend fun getTopList(t: String): TopListResult =
        post("/weapi/toplist/detail", emptyMap())

    override suspend fun getSingerList(type: String, area: String): SingleListResult =
        post(
            "/weapi/v1/artist/list",
            mapOf("type" to type, "area" to area, "initial" to -1, "offset" to 0, "limit" to 1000, "total" to true)
        )

    private fun threadId(type: Int, id: String): String = when (type) {
        1 -> "R_AL_3_$id"
        2 -> "R_VI_62_$id"
        4 -> "R_MV_5_$id"
        else -> "R_SO_4_$id"
    }

    private data class RawQrcodeKey(
        @SerializedName("unikey") val unikey: String = "",
    )
}
