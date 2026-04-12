package xyz.larkzhh.echomusic.utils

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import xyz.larkzhh.echomusic.core.MusicPlayController
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.nav.RouterUrls
import xyz.larkzhh.echomusic.ui.theme.isInDarkTheme


@Composable
fun FixSystemBarsColor() {
    val view = LocalView.current
    val isDarkTheme = isInDarkTheme()

    val navBackStackEntry by EchoNavController.instance.currentBackStackEntryAsState()
    val curRouteName = navBackStackEntry?.destination?.route ?: ""
    val rootRoute = curRouteName.split("/").getOrNull(0) ?: ""

    // 逻辑计算：是否需要强制使用白色图标（Light Icons）
    val forceLightIcons = when {
        rootRoute == RouterUrls.PLAY_LIST && MusicPlayController.showPlayMusicSheet -> true
        curRouteName == RouterUrls.SPLASH -> true
        curRouteName == RouterUrls.LOGIN -> true
        rootRoute == RouterUrls.PLAY_VIDEO -> true
        else -> false
    }

    val isAppearanceLightStatusBars = if (forceLightIcons) false else !isDarkTheme

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)

            // 设置状态栏图标颜色
            insetsController.isAppearanceLightStatusBars = isAppearanceLightStatusBars

            // 设置导航栏图标颜色
            insetsController.isAppearanceLightNavigationBars = !isDarkTheme
        }
    }
}