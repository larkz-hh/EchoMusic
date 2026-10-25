package xyz.larkzhh.echomusic.hilt.module

import xyz.larkzhh.echomusic.BuildConfig
import xyz.larkzhh.echomusic.core.AppConfig
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.http.RetrofitClient
import xyz.larkzhh.echomusic.http.local.LocalNeteaseApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {

    @Provides
    @Singleton
    fun provideEchoApi(@RetrofitClientModule.EchoRetrofitClientQualifier retrofit: RetrofitClient): NeteaseApi {
        // 打包时用 -PlocalApi=true 切换到直连模式
        return if (BuildConfig.USE_LOCAL_API) {
            LocalNeteaseApi()
        } else {
            retrofit.setBaseUrl(AppConfig.BASE_URL).create(NeteaseApi::class.java)
        }
    }

}
