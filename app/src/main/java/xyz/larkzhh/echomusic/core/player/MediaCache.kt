package xyz.larkzhh.echomusic.core.player

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File


/** 播放缓存，音视频分开，各自 LRU 淘汰 */
@UnstableApi
object MediaCache {

    private const val AUDIO_MAX_SIZE = 1024L * 1024 * 1024
    private const val VIDEO_MAX_SIZE = 2L * 1024 * 1024 * 1024

    @Volatile
    private var audioCache: SimpleCache? = null

    @Volatile
    private var videoCache: SimpleCache? = null

    fun audio(context: Context): SimpleCache {
        audioCache?.let { return it }
        return synchronized(this) {
            audioCache ?: build(context, "audio", AUDIO_MAX_SIZE).also { audioCache = it }
        }
    }

    fun video(context: Context): SimpleCache {
        videoCache?.let { return it }
        return synchronized(this) {
            videoCache ?: build(context, "video", VIDEO_MAX_SIZE).also { videoCache = it }
        }
    }

    private fun build(context: Context, name: String, maxBytes: Long): SimpleCache {
        return SimpleCache(
            File(File(context.cacheDir, "media_cache"), name),
            LeastRecentlyUsedCacheEvictor(maxBytes),
            StandaloneDatabaseProvider(context),
        )
    }
}
