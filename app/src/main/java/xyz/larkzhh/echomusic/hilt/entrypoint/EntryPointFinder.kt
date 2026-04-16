package xyz.larkzhh.echomusic.hilt.entrypoint

import xyz.larkzhh.echomusic.EchoApplication
import xyz.larkzhh.echomusic.http.NeteaseApi
import dagger.hilt.EntryPoints


object EntryPointFinder {
    fun getEchoApi(): NeteaseApi {
        val entryPoint = EntryPoints.get(EchoApplication.context, EchoApiEntryPoint::class.java)
        return entryPoint.getEchoApi()
    }
}

