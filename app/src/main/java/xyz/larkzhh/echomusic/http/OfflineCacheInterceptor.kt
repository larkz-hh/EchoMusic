package xyz.larkzhh.echomusic.http

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


/** 网络异常时回退到本地缓存 */
class OfflineCacheInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return try {
            chain.proceed(request)
        } catch (e: IOException) {
            val cacheOnly = request.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()
            val cached = try {
                chain.proceed(cacheOnly)
            } catch (cacheError: IOException) {
                throw e
            }
            if (cached.code == 504) throw e
            cached
        }
    }
}
