package xyz.larkzhh.echomusic.http.local

import android.util.Log
import android.util.LruCache
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import xyz.larkzhh.echomusic.core.AppGlobalData
import xyz.larkzhh.echomusic.core.UserSpace
import java.io.IOException
import java.util.concurrent.TimeUnit

/** 直连 music.163.com 的 weapi 传输层 */
class WeapiTransport {

    private val gson = Gson()

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    /** 读接口的短期缓存，避免同页面反复请求 */
    private val memoryCache = LruCache<String, CacheEntry>(MAX_CACHE_COUNT)

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
            val cacheKey = "${UserSpace.id}|$path|${gson.toJson(payload)}"
            val cacheable = isCacheable(path)

            if (cacheable) {
                memoryCache.get(cacheKey)?.let { entry ->
                    if (System.currentTimeMillis() - entry.time <= CACHE_TTL_MS) {
                        return@withContext gson.fromJson(entry.json, clazz)
                    }
                    memoryCache.remove(cacheKey)
                }
            }

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
                Log.d("Weapi", "$path -> ${response.code} cached=$cacheable ${text.take(120)}")
                if (text.isBlank()) {
                    throw IOException("空响应 $path")
                }
                if (cacheable && response.code == 200 && isSuccess(text)) {
                    memoryCache.put(cacheKey, CacheEntry(System.currentTimeMillis(), text))
                }
                gson.fromJson(text, clazz) ?: throw IOException("解析失败 $path")
            }
        }

    private fun isSuccess(text: String): Boolean = runCatching {
        JsonParser.parseString(text).asJsonObject.get("code")?.asInt == 200
    }.getOrDefault(false)

    private fun isCacheable(path: String): Boolean = CACHEABLE_PATHS.any { path.startsWith(it) }

    private data class CacheEntry(val time: Long, val json: String)

    private companion object {
        const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/120.0.0.0 Safari/537.36"
        val CSRF_PATTERN = Regex("__csrf=([^;]+)")

        const val CACHE_TTL_MS = 3 * 60 * 1000L
        const val MAX_CACHE_COUNT = 80

        // 只缓存读接口，取链/点赞/上报/最近播放不缓存
        val CACHEABLE_PATHS = listOf(
            "/weapi/v2/banner/get",
            "/weapi/personalized/playlist",
            "/weapi/toplist/detail",
            "/weapi/hotsearchlist/get",
            "/weapi/search/suggest/keyword",
            "/weapi/cloudsearch/pc",
            "/weapi/v6/playlist/detail",
            "/weapi/v3/song/detail",
            "/weapi/song/lyric",
            "/weapi/v1/resource/comments/R_SO_4_",
            "/weapi/resource/comment/floor/get",
            "/weapi/v1/artist/list",
            "/weapi/mv/all",
            "/weapi/mv/first",
            "/weapi/cloudvideo/group/list",
            "/weapi/videotimeline/videogroup/otherclient/get",
            "/weapi/playlist/list",
            "/weapi/djradio/personalize/rcmd",
            "/weapi/djradio/v2/get",
            "/weapi/dj/program/byradio",
        )
    }
}
