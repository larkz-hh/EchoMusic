package xyz.larkzhh.echomusic.ui.page.login

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import xyz.larkzhh.echomusic.R
import xyz.larkzhh.echomusic.core.AppGlobalData
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.nav.RouterUrls
import xyz.larkzhh.echomusic.core.viewstate.BaseViewStateViewModel
import xyz.larkzhh.echomusic.core.viewstate.LoadingComponent
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.model.AccountInfoResult
import xyz.larkzhh.echomusic.model.LoginResult
import xyz.larkzhh.echomusic.model.QrcodeAuthResult
import xyz.larkzhh.echomusic.ui.theme.AppColorsProvider
import xyz.larkzhh.echomusic.utils.QrcodeUtil
import xyz.larkzhh.echomusic.utils.cdp
import xyz.larkzhh.echomusic.utils.csp
import xyz.larkzhh.echomusic.utils.toPx
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun QrcodeLoginPage() {
    val viewModel: QrcodeLoginViewModel = hiltViewModel()
    var showCookieInput by remember { mutableStateOf(false) }
    var cookieInput by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        viewModel.qrcodeAuth()
    }
    LaunchedEffect(viewModel.navigateToHome) {
        if (viewModel.navigateToHome) {
            EchoNavController.instance.popBackStack()
            EchoNavController.instance.navigate(RouterUrls.HOME)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColorsProvider.current.primary)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Box(
                Modifier
                    .padding(top = 205.cdp, start = 5.cdp)
                    .size(190.cdp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_splash_logo),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 200.cdp)
                    .size(200.cdp)
                    .clip(RoundedCornerShape(50)),
                tint = AppColorsProvider.current.primaryVariant
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 100.cdp)
                .width(540.cdp)
                .background(AppColorsProvider.current.pure, shape = RoundedCornerShape(16.cdp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "扫码登录体验",
                fontSize = 40.csp,
                color = AppColorsProvider.current.firstText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 32.cdp, bottom = 16.cdp)
            )
            if (viewModel.qrcodeAuthStatus == 801 || viewModel.qrcodeAuthStatus == 802) {
                Image(
                    bitmap = viewModel.qrcodeBitmap!!.asImageBitmap(),
                    modifier = Modifier.size(400.cdp),
                    contentDescription = "登录二维码"
                )
            } else {
                Box(modifier = Modifier.size(400.cdp), contentAlignment = Alignment.Center) {
                    LoadingComponent(loadingWidth = 90.cdp, loadingHeight = 75.cdp)
                }
            }

            val tip = when (viewModel.qrcodeAuthStatus) {
                800 -> "二维码已过期，正在重新加载..."
                801 -> "请使用某抑云音乐app扫码授权登录"
                802 -> "扫描成功，请在手机上确认登录"
                803 -> "正在获取用户信息..."
                else -> "正在加载二维码"
            }

            Text(
                text = tip,
                fontSize = 28.csp,
                textAlign = TextAlign.Center,
                color = AppColorsProvider.current.secondIcon,
                modifier = Modifier.padding(top = 16.cdp, start = 32.cdp, end = 32.cdp)
            )
            Text(
                text = "(仅供学习使用)",
                fontSize = 28.csp,
                textAlign = TextAlign.Center,
                color = AppColorsProvider.current.secondIcon,
                modifier = Modifier.padding(bottom = 16.cdp, start = 32.cdp, end = 32.cdp)
            )

            if (viewModel.errorTip.isNotEmpty()) {
                Text(
                    text = viewModel.errorTip,
                    fontSize = 26.csp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFE5484D),
                    modifier = Modifier.padding(top = 8.cdp, start = 32.cdp, end = 32.cdp)
                )
            }

            Text(
                text = "先逛逛（游客模式）",
                fontSize = 30.csp,
                textAlign = TextAlign.Center,
                color = AppColorsProvider.current.primary,
                modifier = Modifier
                    .padding(bottom = 8.cdp, top = 8.cdp)
                    .clickable {
                        // 游客模式
                        AppGlobalData.isGuest = true
                        EchoNavController.instance.popBackStack()
                        EchoNavController.instance.navigate(RouterUrls.HOME)
                    }
            )

            Text(
                text = "使用 Cookie 登录",
                fontSize = 28.csp,
                textAlign = TextAlign.Center,
                color = AppColorsProvider.current.secondText,
                modifier = Modifier
                    .padding(top = 8.cdp, bottom = if (showCookieInput) 8.cdp else 40.cdp)
                    .clickable { showCookieInput = !showCookieInput }
            )

            if (showCookieInput) {
                OutlinedTextField(
                    value = cookieInput,
                    onValueChange = { cookieInput = it },
                    label = { Text("粘贴 MUSIC_U 或整段 Cookie") },
                    maxLines = 3,
                    modifier = Modifier
                        .padding(start = 32.cdp, end = 32.cdp)
                        .fillMaxWidth()
                        .heightIn(min = 90.cdp)
                )
                Button(
                    onClick = { viewModel.loginByCookie(cookieInput) },
                    modifier = Modifier
                        .padding(top = 16.cdp, bottom = 32.cdp)
                        .width(300.cdp)
                        .height(70.cdp)
                ) {
                    Text(text = "登录", fontSize = 30.csp, color = Color.White)
                }
            }
        }
    }
}

@HiltViewModel
class QrcodeLoginViewModel @Inject constructor(private val api: NeteaseApi) : BaseViewStateViewModel() {

    var qrcodeAuthStatus by mutableStateOf<Int?>(null)
    var qrcodeBitmap by mutableStateOf<Bitmap?>(null)
    var errorTip by mutableStateOf("")
    var navigateToHome by mutableStateOf(false)
        private set
    private var mLastQrcodeAuthJob: Job? = null
    private var mCookie = ""

    fun qrcodeAuth() {
        mLastQrcodeAuthJob?.let {
            Log.d("cloudMusic", "----重新授权，取消上次任务")
            it.cancel()
        }
        qrcodeAuthStatus = null
        qrcodeBitmap = null
        errorTip = ""
        mLastQrcodeAuthJob = viewModelScope.launch {
            while (isActive) {
                try {
                    if (runQrcodeAuth()) return@launch
                } catch (e: Exception) {
                    Log.e("cloudMusic", "扫码登录异常", e)
                    delay(2000)
                }
            }
        }
    }

    /** 返回 true 表示登录成功 */
    private suspend fun runQrcodeAuth(): Boolean {
        val key = api.getLoginQrcodeKey().data.unikey
        val qrcodeValueResult = api.getLoginQrcodeValue(key)
        qrcodeBitmap = QrcodeUtil.createQrcodeBitmap(
            qrcodeValueResult.data.qrurl,
            360.cdp.toPx.toInt(),
            360.cdp.toPx.toInt()
        )
        var qrcodeAuthResult = checkQrcodeAuth(key)
        qrcodeAuthStatus = qrcodeAuthResult.code
        var failures = 0

        while (mLastQrcodeAuthJob?.isActive != false) {
            delay(3000)
            qrcodeAuthResult = checkQrcodeAuth(key)
            if (qrcodeAuthResult.code == null) {
                // 状态未知，连续失败则重新生成二维码
                if (++failures >= 5) return false
                continue
            }
            failures = 0
            qrcodeAuthStatus = qrcodeAuthResult.code

            when {
                qrcodeAuthResult.resultOk() -> {
                    Log.d("cloudMusic", "----授权成功")
                    mCookie = qrcodeAuthResult.cookie
                    getAccountInfo()
                    return true
                }
                qrcodeAuthResult.code == 800 -> {
                    Log.d("cloudMusic", "----二维码过期，重新生成")
                    return false
                }
                qrcodeAuthResult.code == 801 -> Log.d("cloudMusic", "----等待扫码")
                qrcodeAuthResult.code == 802 -> Log.d("cloudMusic", "----待确认")
                // 8821 某抑云风控拦截，停止轮询
                qrcodeAuthResult.code == 8821 -> {
                    Log.e("cloudMusic", "----登录被风控拦截")
                    errorTip = "某抑云拦截了本次登录，请稍后重试或换网络"
                    return true
                }
            }
        }
        return false
    }

    /** 异常时返回 null */
    private suspend fun checkQrcodeAuth(key: String): QrcodeAuthResult {
        return try {
            api.checkQrcodeAuthStatus(key)
        } catch (e: Exception) {
            Log.e("cloudMusic", "检查扫码状态失败", e)
            QrcodeAuthResult("").apply { code = null }
        }
    }

    private suspend fun getAccountInfo() {
        try {
            val accountInfoResult = api.getAccountInfo(mCookie)
            if (accountInfoResult.isLoggedIn()) {
                saveLogin(accountInfoResult, mCookie)
            } else {
                Log.e("cloudMusic", "获取用户信息失败: ${accountInfoResult.code} ${accountInfoResult.message}")
                qrcodeAuth()
            }
        } catch (e: Exception) {
            Log.e("cloudMusic", "获取用户信息异常", e)
            qrcodeAuth()
        }
    }

    /** Cookie 登录 */
    fun loginByCookie(rawCookie: String) {
        val cookie = normalizeCookie(rawCookie)
        if (cookie.isEmpty()) {
            errorTip = "Cookie 不能为空"
            return
        }
        viewModelScope.launch {
            errorTip = ""
            try {
                val accountInfoResult = api.getAccountInfo(cookie)
                if (accountInfoResult.isLoggedIn()) {
                    mLastQrcodeAuthJob?.cancel()
                    saveLogin(accountInfoResult, cookie)
                } else {
                    errorTip = "Cookie 无效或已过期"
                }
            } catch (e: Exception) {
                Log.e("cloudMusic", "Cookie 登录失败", e)
                errorTip = "登录失败，请检查网络"
            }
        }
    }

    private fun saveLogin(accountInfoResult: AccountInfoResult, cookie: String) {
        AppGlobalData.sLoginResult = LoginResult(
            account = accountInfoResult.account,
            profile = accountInfoResult.profile,
            cookie = cookie,
        )
        AppGlobalData.isGuest = false
        AppGlobalData.sLoginRefreshFlag = !AppGlobalData.sLoginRefreshFlag
        navigateToHome = true
    }

    /** 只粘 MUSIC_U 的值也能用 */
    private fun normalizeCookie(raw: String): String {
        val text = raw.trim().replace("\r", "").replace("\n", "")
        if (text.isEmpty()) return ""
        return if (text.contains("=")) text else "MUSIC_U=$text"
    }

    @Suppress("SENSELESS_COMPARISON")
    private fun AccountInfoResult.isLoggedIn(): Boolean =
        code == 200 && account != null && profile != null
}