package xyz.larkzhh.echomusic.nav

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.gson.Gson
import xyz.larkzhh.echomusic.model.PlaylistBean
import xyz.larkzhh.echomusic.ui.page.cloudcountry.PlayVideoPage
import xyz.larkzhh.echomusic.ui.page.home.HomePage
import xyz.larkzhh.echomusic.ui.page.mine.PlaylistPage
import xyz.larkzhh.echomusic.ui.page.mine.RecentPlayPage
import xyz.larkzhh.echomusic.ui.page.local.LocalMusicPage
import xyz.larkzhh.echomusic.ui.page.profile.ProfilePage
import xyz.larkzhh.echomusic.ui.page.splash.SplashPage
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import xyz.larkzhh.echomusic.ui.page.login.QrcodeLoginPage
import xyz.larkzhh.echomusic.model.SongBean
import xyz.larkzhh.echomusic.model.Video
import xyz.larkzhh.echomusic.ui.page.comment.SongCommentPage
import xyz.larkzhh.echomusic.ui.page.search.SearchPage
import xyz.larkzhh.echomusic.ui.page.web.WebViewPage

object EchoNavController {
    @SuppressLint("StaticFieldLeak")
    lateinit var instance: NavHostController

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EchoNavGraph(
    navController: NavHostController,
    startDestination: String = RouterUrls.SPLASH,
    onFinish: () -> Unit = { }
) {
    EchoNavController.instance = navController
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(RouterUrls.SPLASH) {
            SplashPage()
        }
        composable(RouterUrls.LOGIN) {
            QrcodeLoginPage()
        }
        composable(RouterUrls.HOME) {
            HomePage { onFinish() }
        }
        composable(RouterUrls.SEARCH) {
            SearchPage()
        }
        composable(RouterUrls.RECENT_PLAY) {
            RecentPlayPage()
        }
        composable(RouterUrls.LOCAL_MUSIC) {
            LocalMusicPage()
        }
        composable(
            RouterUrls.PROFILE,
            enterTransition = { EnterTransition.None }) {
            ProfilePage()
        }
        composable("${RouterUrls.PLAY_LIST}/{${RouterKV.PLAY_LIST_BEAN}}") {
            val playlistBeanJson = it.arguments?.getString(RouterKV.PLAY_LIST_BEAN)!!
            val playlistBean = Gson().fromJson(playlistBeanJson, PlaylistBean::class.java)
            PlaylistPage(playlistBean)
        }
        composable("${RouterUrls.SONG_COMMENT}/{${RouterKV.SONG_BEAN}}") {
            val songBeanJson = it.arguments?.getString(RouterKV.SONG_BEAN)!!
            val songBean = Gson().fromJson(songBeanJson, SongBean::class.java)
            SongCommentPage(songBean)
        }
        composable("${RouterUrls.PLAY_VIDEO}/{${RouterKV.VIDEO_BEAN}}/{${RouterKV.VIDEO_GROUP_ID}}/{${RouterKV.VIDEO_OFFSET_INDEX}}",
            arguments = listOf(
                navArgument(RouterKV.VIDEO_BEAN) { type = NavType.StringType },
                navArgument(RouterKV.VIDEO_GROUP_ID) { type = NavType.IntType },
                navArgument(RouterKV.VIDEO_OFFSET_INDEX) { type = NavType.IntType }
            )) {
            val videoBeanJson = it.arguments?.getString(RouterKV.VIDEO_BEAN)!!
            val videoGroupId = it.arguments?.getInt(RouterKV.VIDEO_GROUP_ID)!!
            val videoOffsetIndex = it.arguments?.getInt(RouterKV.VIDEO_OFFSET_INDEX)!!
            val videoBean = Gson().fromJson(videoBeanJson, Video::class.java)
            PlayVideoPage(videoBean, videoGroupId, videoOffsetIndex)
        }
        composable(
            "${RouterUrls.WEB}/{${RouterKV.WEB_URL}}",
            arguments = listOf(
                navArgument(RouterKV.WEB_URL) { type = NavType.StringType }
            )
        ) {
            val webUrl = Uri.decode(it.arguments?.getString(RouterKV.WEB_URL) ?: "")
            WebViewPage(webUrl)
        }
    }
}

