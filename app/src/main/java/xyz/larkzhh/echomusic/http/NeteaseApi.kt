package xyz.larkzhh.echomusic.http

import xyz.larkzhh.echomusic.model.AccountInfoResult
import xyz.larkzhh.echomusic.model.DiscoverBannerResult
import xyz.larkzhh.echomusic.model.FloorCommentResult
import xyz.larkzhh.echomusic.model.LyricResult
import xyz.larkzhh.echomusic.model.NewCommentResult
import xyz.larkzhh.echomusic.model.PlaylistDetailResult
import xyz.larkzhh.echomusic.model.QrcodeAuthResult
import xyz.larkzhh.echomusic.model.QrcodeKeyResult
import xyz.larkzhh.echomusic.model.QrcodeValueResult
import xyz.larkzhh.echomusic.model.SearchResult
import xyz.larkzhh.echomusic.model.SongCommentResult
import xyz.larkzhh.echomusic.model.SongDetailResult
import xyz.larkzhh.echomusic.model.SongUrlBean
import xyz.larkzhh.echomusic.model.SingleListResult
import xyz.larkzhh.echomusic.model.UserPlaylistResult
import xyz.larkzhh.echomusic.model.MvAllResult
import xyz.larkzhh.echomusic.model.MvDetailResult
import xyz.larkzhh.echomusic.model.MvUrlResult
import xyz.larkzhh.echomusic.model.MvFirstResult
import xyz.larkzhh.echomusic.model.PersonalizedResult
import xyz.larkzhh.echomusic.model.VideoGroupResult
import xyz.larkzhh.echomusic.model.VideoGroupTabsResult
import xyz.larkzhh.echomusic.model.VideoUrlsResult
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Date
import xyz.larkzhh.echomusic.model.TopListResult
import xyz.larkzhh.echomusic.model.UserRecordResult


interface NeteaseApi {

    @GET("/login/qr/key")
    suspend fun getLoginQrcodeKey(@Query("timeStamp") timeStamp: Long = Date().time): QrcodeKeyResult

    @GET("/login/qr/create")
    suspend fun getLoginQrcodeValue(
        @Query("key") key: String,
        @Query("timeStamp") timeStamp: Long = Date().time
    ): QrcodeValueResult

    @GET("/login/qr/check")
    suspend fun checkQrcodeAuthStatus(
        @Query("key") key: String,
        @Query("timeStamp") timeStamp: Long = Date().time
    ): QrcodeAuthResult

//    @GET("/user/account")
//    suspend fun getAccountInfo(
//        @Query("cookie") cookie: String,
//    ): AccountInfoResult

    @GET("/user/account")
    suspend fun getAccountInfo(
        @Query("cookie") cookie: String? = null
    ): AccountInfoResult

    // 最近播放
    @GET("/user/record")
    suspend fun getUserRecord(
        @Query("uid") uid: String,
        @Query("type") type: Int = 1,
    ): UserRecordResult

    @GET("/user/playlist")
    suspend fun getUserPlayList(@Query("uid") uid: String): UserPlaylistResult

    @GET("/playlist/detail")
    suspend fun getPlaylistDetail(@Query("id") id: Long): PlaylistDetailResult

    @GET("/song/detail")
    suspend fun getSongDetail(@Query("ids") ids: String): SongDetailResult


    @GET("/cloudsearch")
    suspend fun getSearch(
        @Query("keywords") keywords: String,
        @Query("type") type: Int = 1,
        @Query("limit") limit: Int = 10,
    ): SearchResult

    @GET("/song/url")
    suspend fun getSongUrl(
        @Query("id") id: Long,
        @Query("br") br: Int = 128000
    ): SongUrlBean

    @GET("/comment/music")
    suspend fun getSongComment(
        @Query("id") id: Long,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int
    ): SongCommentResult

    @GET("/comment/new")
    suspend fun getNewComment(
        @Query("id") id: String,
        @Query("type") type: Int,
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int,
        @Query("sortType") sortType: Int,
        @Query("cursor") cursor: String,
    ): NewCommentResult

    @GET("/comment/floor")
    suspend fun getCommentFloor(
        @Query("parentCommentId") parentCommentId: Long,
        @Query("id") id: String,
        @Query("type") type: Int = 0,
        @Query("limit") limit: Int,
        @Query("time") time: Long,
    ): FloorCommentResult

    @GET("/lyric")
    suspend fun getLyric(@Query("id") id: Long): LyricResult

    @GET("/video/group/list")
    suspend fun getVideoGroupTabs(): VideoGroupTabsResult

    @GET("/video/group")
    suspend fun getVideoGroup(
        @Query("id") id: Int,
        @Query("offset") offset: Int
    ): VideoGroupResult

    @GET("/mv/all")
    suspend fun getMvAll(
        @Query("area") area: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 30,
    ): MvAllResult

    @GET("/mv/detail")
    suspend fun getMvDetail(@Query("mvid") mvid: String): MvDetailResult

    @GET("/mv/url")
    suspend fun getMvUrl(@Query("id") id: String): MvUrlResult

    @GET("/banner")
    suspend fun getDiscoverBanner(): DiscoverBannerResult

    @GET("/personalized")
    suspend fun getPersonalized(@Query("limit") limit: Int = 30): PersonalizedResult

    @GET("/mv/first")
    suspend fun getMvFirst(@Query("limit") limit: Int = 5): MvFirstResult

    @GET("/video/url")
    suspend fun getVideoUrl(@Query("id") id: String): VideoUrlsResult

    //排行榜
    @GET("/toplist/detail")
    suspend fun getTopList(@Query("t") t: String): TopListResult


    @GET("/artist/list")
    suspend fun getSingerList(
        @Query("type") type: String,
        @Query("area") area: String
      ): SingleListResult
}