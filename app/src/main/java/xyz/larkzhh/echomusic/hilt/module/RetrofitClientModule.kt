package xyz.larkzhh.echomusic.hilt.module

import android.content.Context
import android.util.Log
import xyz.larkzhh.echomusic.core.UserSpace
import xyz.larkzhh.echomusic.http.CookieIntercept
import xyz.larkzhh.echomusic.http.LoginExpiredInterceptor
import xyz.larkzhh.echomusic.http.OfflineCacheInterceptor
import xyz.larkzhh.echomusic.http.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RetrofitClientModule {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class EchoRetrofitClientQualifier

    private const val CACHE_SIZE_BYTES = 50L * 1024 * 1024

    @Provides
    @Singleton
    @EchoRetrofitClientQualifier
    fun provideNCRetrofitClient(@ApplicationContext context: Context): RetrofitClient {
        // 每个数据空间一个 client，缓存目录隔离
        val callFactory = object : Call.Factory {
            private val clients = HashMap<String, OkHttpClient>()

            override fun newCall(request: Request): Call {
                val space = UserSpace.id
                val client = synchronized(clients) {
                    clients.getOrPut(space) { buildClient(context, space) }
                }
                return client.newCall(request)
            }
        }

        return RetrofitClient(
            Retrofit.Builder()
                .callFactory(callFactory)
                .addConverterFactory(GsonConverterFactory.create())
        )
    }

    private fun buildClient(context: Context, space: String): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.e("OkHttp", "OkHttp====:$message")
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .cache(Cache(UserSpace.cacheDirFor(context, space), CACHE_SIZE_BYTES))
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            // 须在 cookie 之前，保证重试 URL 一致
            .addInterceptor(OfflineCacheInterceptor())
            .addInterceptor(CookieIntercept())
            .addInterceptor(LoginExpiredInterceptor())
            .build()
    }
}
