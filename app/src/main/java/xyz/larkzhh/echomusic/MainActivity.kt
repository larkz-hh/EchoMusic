package xyz.larkzhh.echomusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import xyz.larkzhh.echomusic.nav.EchoNavGraph
import xyz.larkzhh.echomusic.ui.theme.AppTheme
import xyz.larkzhh.echomusic.ui.theme.themeTypeState
import xyz.larkzhh.echomusic.utils.FixSystemBarsColor
import xyz.larkzhh.echomusic.utils.setAndroidNativeLightStatusBar
import xyz.larkzhh.echomusic.utils.transparentStatusBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        setAndroidNativeLightStatusBar()
        enableEdgeToEdge()
        setContent {
            AppTheme(themeTypeState.value) {
                val navController = rememberNavController()
                val bottomDp: Int = WindowInsets.navigationBars.getBottom(LocalDensity.current)
                Box(
                    modifier = Modifier
                        .fillMaxSize()

                ) {
                    EchoNavGraph(navController) {
                        finish()
                    }

                    FixSystemBarsColor()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}