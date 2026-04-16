package xyz.larkzhh.echomusic.hilt.entrypoint

import xyz.larkzhh.echomusic.http.NeteaseApi
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@EntryPoint
@InstallIn(SingletonComponent::class)
interface EchoApiEntryPoint {
    //@ApiServiceModule.EchoApiInterfaceQualifier
    fun getEchoApi(): NeteaseApi
}