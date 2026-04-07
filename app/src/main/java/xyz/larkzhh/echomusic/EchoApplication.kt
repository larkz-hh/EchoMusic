package xyz.larkzhh.echomusic

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import xyz.larkzhh.echomusic.utils.KVCache
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EchoApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        KVCache.init(this)
        //MusicPlayService.start()
    }
}