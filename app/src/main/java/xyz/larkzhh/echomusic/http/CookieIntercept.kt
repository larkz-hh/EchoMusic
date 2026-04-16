package xyz.larkzhh.echomusic.http

import xyz.larkzhh.echomusic.core.AppGlobalData
import okhttp3.Interceptor
import okhttp3.Response


class CookieIntercept : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        // 接口已显式指定 cookie 时跳过
        if (request.url.queryParameter("cookie") != null) {
            return chain.proceed(request)
        }
        val cookie = AppGlobalData.sLoginResult?.cookie.orEmpty()
        if (cookie.isBlank()) {
            return chain.proceed(request)
        }
        // 服务端从 query 参数读取 cookie
        val newUrl = request.url.newBuilder()
            .addQueryParameter("cookie", cookie)
            .build()
        return chain.proceed(request.newBuilder().url(newUrl).build())
    }
}
