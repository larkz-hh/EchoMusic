package xyz.larkzhh.echomusic.http

import android.os.Handler
import android.os.Looper
import xyz.larkzhh.echomusic.core.AppGlobalData
import xyz.larkzhh.echomusic.nav.EchoNavController
import xyz.larkzhh.echomusic.nav.RouterUrls
import okhttp3.Interceptor
import okhttp3.Response


/** 登录失效时清理登录态并回到登录页 */
class LoginExpiredInterceptor : Interceptor {

    private val handler = Handler(Looper.getMainLooper())

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (AppGlobalData.sLoginResult == null) {
            return response
        }
        val body = try {
            response.peekBody(16 * 1024).string()
        } catch (e: Exception) {
            ""
        }
        val expired = response.code == 301 || response.code == 302 || response.code == 401 ||
            body.contains("\"code\":301") ||
            body.contains("\"account\":null")
        if (expired) {
            handler.post { backToLogin() }
        }
        return response
    }

    private fun backToLogin() {
        if (AppGlobalData.sLoginResult == null) {
            return
        }
        AppGlobalData.logout()
        runCatching {
            val route = EchoNavController.instance.currentDestination?.route
            // 启动页/登录页由页面自己决定下一步
            if (route != RouterUrls.LOGIN && route != RouterUrls.SPLASH) {
                EchoNavController.instance.navigate(RouterUrls.LOGIN)
            }
        }
    }
}
