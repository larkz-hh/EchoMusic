package xyz.larkzhh.echomusic.http.local

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import xyz.larkzhh.echomusic.core.AppGlobalData
import java.io.IOException
import java.util.concurrent.TimeUnit

/** 直连 music.163.com 的 weapi 传输层 */
class WeapiTransport {

    private val gson = Gson()

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun <T> post(
        path: String,
        data: Map<String, Any?>,
        clazz: Class<T>,
        cookieOverride: String? = null,
    ): T =
        withContext(Dispatchers.IO) {
            val cookie = cookieOverride ?: AppGlobalData.sLoginResult?.cookie.orEmpty()
            val csrf = CSRF_PATTERN.find(cookie)?.groupValues?.get(1).orEmpty()
            val payload = data.filterValues { it != null }.toMutableMap()
            payload["csrf_token"] = csrf
            val (params, encSecKey) = WeapiCrypto.encrypt(gson.toJson(payload))
            val body = FormBody.Builder()
                .add("params", params)
                .add("encSecKey", encSecKey)
                .build()
            val request = Request.Builder()
                .url("https://music.163.com$path")
                .post(body)
                .header("User-Agent", USER_AGENT)
                .header("Referer", "https://music.163.com/")
                .header("Cookie", cookie)
                .build()
            client.newCall(request).execute().use { response ->
                val text = response.body?.string().orEmpty()
                Log.d("Weapi", "$path -> ${response.code} ${text.take(120)}")
                if (text.isBlank()) {
                    throw IOException("空响应 $path")
                }
                gson.fromJson(text, clazz) ?: throw IOException("解析失败 $path")
            }
        }

    private companion object {
        const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/120.0.0.0 Safari/537.36"
        val CSRF_PATTERN = Regex("__csrf=([^;]+)")
    }
}
