package xyz.larkzhh.echomusic.http

import xyz.larkzhh.echomusic.model.AccountInfoResult
import xyz.larkzhh.echomusic.model.DiscoverBannerResult
import xyz.larkzhh.echomusic.model.DjDetailResult
import xyz.larkzhh.echomusic.model.DjProgramResult
import xyz.larkzhh.echomusic.model.DjRecommendResult
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
import xyz.larkzhh.echomusic.model.BaseResult
import xyz.larkzhh.echomusic.model.HotSearchResult
import xyz.larkzhh.echomusic.model.LikeListResult
import xyz.larkzhh.echomusic.model.SearchSuggestResult
import xyz.larkzhh.echomusic.model.TopListResult
import xyz.larkzhh.echomusic.model.TopPlaylistResult
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

    @GET("/search/hot/detail")
    suspend fun getHotSearch(): HotSearchResult

    @GET("/search/suggest")
    suspend fun getSearchSuggest(
        @Query("keywords") keywords: String,
        @Query("type") type: String = "mobile",
    ): SearchSuggestResult

    // 播放上报，计入最近播放
    @GET("/scrobble")
    suspend fun scrobble(
        @Query("id") id: Long,
        @Query("sourceid") sourceId: Long = 0,
        @Query("time") time: Long,
    ): BaseResult

    @GET("/like")
    suspend fun like(
        @Query("id") id: Long,
        @Query("like") like: Boolean,
    ): BaseResult

    @GET("/likelist")
    suspend fun getLikeList(@Query("uid") uid: String): LikeListResult

    @GET("/comment/like")
    suspend fun likeComment(
        @Query("id") id: String,
        @Query("cid") commentId: Long,
        @Query("t") like: Int,
        @Query("type") type: Int,
    ): BaseResult

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
        // 服务端按账号权限自动回退
        @Query("br") br: Int = 999000
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

    // 歌单广场
    @GET("/top/playlist")
    suspend fun getTopPlaylist(
        @Query("cat") cat: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("order") order: String = "hot",
    ): TopPlaylistResult

    // 资源点赞，type 见 resourceTypeMap，视频为 5
    @GET("/resource/like")
    suspend fun likeResource(
        @Query("id") id: String,
        @Query("type") type: Int,
        @Query("t") t: Int,
    ): BaseResult

    // 收藏视频
    @GET("/video/sub")
    suspend fun collectVideo(
        @Query("id") id: String,
        @Query("t") t: Int,
    ): BaseResult

    // 推荐电台
    @GET("/dj/personalize/recommend")
    suspend fun getDjRecommend(@Query("limit") limit: Int = 12): DjRecommendResult

    // 电台详情
    @GET("/dj/detail")
    suspend fun getDjDetail(@Query("rid") rid: Long): DjDetailResult

    // 电台节目
    @GET("/dj/program")
    suspend fun getDjProgram(
        @Query("rid") rid: Long,
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0,
    ): DjProgramResult


    @GET("/artist/list")
    suspend fun getSingerList(
        @Query("type") type: String,
        @Query("area") area: String
      ): SingleListResult
}