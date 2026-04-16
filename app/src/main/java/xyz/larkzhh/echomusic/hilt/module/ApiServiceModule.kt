package xyz.larkzhh.echomusic.hilt.module

import xyz.larkzhh.echomusic.core.AppConfig
import xyz.larkzhh.echomusic.http.NeteaseApi
import xyz.larkzhh.echomusic.http.RetrofitClient
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
        return retrofit.setBaseUrl(AppConfig.BASE_URL).create(NeteaseApi::class.java)
    }

}
