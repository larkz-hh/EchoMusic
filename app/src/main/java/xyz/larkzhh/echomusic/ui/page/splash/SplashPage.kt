package xyz.larkzhh.echomusic.ui.page.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.core.AppGlobalData
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.nav.RouterUrls
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.cdp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@Composable
fun SplashPage() {
    val viewModel: SplashViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        // 有登录态时校验一次，失效由拦截器清理
        AppGlobalData.sLoginResult?.cookie?.takeIf { it.isNotBlank() }?.let {
            viewModel.checkLogin(it)
        }
        delay(1500)
        EchoNavController.instance.popBackStack()
        // 已登录或游客直接进首页
        val loggedIn = AppGlobalData.sLoginResult?.cookie?.isNotBlank() == true
        val enterHome = loggedIn || AppGlobalData.isGuest
        EchoNavController.instance.navigate(if (enterHome) RouterUrls.HOME else RouterUrls.LOGIN)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorsProvider.current.primary),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            Modifier
                .padding(top = 485.cdp)
                .size(190.cdp)
                .clip(RoundedCornerShape(50))
                .background(Color.White)
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_splash_logo),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 480.cdp)
                .size(200.cdp)
                .clip(RoundedCornerShape(50)),
            tint = AppColorsProvider.current.primaryVariant
        )
    }

}

@HiltViewModel
class SplashViewModel @Inject constructor(private val api: NeteaseApi) : ViewModel() {

    /** 校验本地 cookie */
    fun checkLogin(cookie: String) {
        viewModelScope.launch {
            runCatching { api.getAccountInfo(cookie) }
        }
    }
}