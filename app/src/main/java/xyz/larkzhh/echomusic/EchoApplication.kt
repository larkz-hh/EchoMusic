package xyz.larkzhh.echomusic

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import xyz.larkzhh.echomusic.utils.KVCache
import dagger.hilt.android.HiltAndroidApp
import java.io.File

@HiltAndroidApp
class EchoApplication : Application(), ImageLoaderFactory {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        android.util.Log.i("EchoMode", "USE_LOCAL_API=${BuildConfig.USE_LOCAL_API}")
        KVCache.init(this)
        //MusicPlayService.start()
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.2)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(File(cacheDir, "image_cache"))
                    .maxSizeBytes(256L * 1024 * 1024)
                    .build()
            }
            .crossfade(true)
            .build()
    }
}
