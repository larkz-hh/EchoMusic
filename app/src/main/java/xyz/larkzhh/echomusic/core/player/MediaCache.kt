package xyz.larkzhh.echomusic.core.player

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File


/** 播放缓存，LRU 淘汰 */
@UnstableApi
object MediaCache {

    private const val MAX_SIZE = 1024L * 1024 * 1024

    @Volatile
    private var instance: SimpleCache? = null

    fun get(context: Context): SimpleCache {
        instance?.let { return it }
        return synchronized(this) {
            instance ?: SimpleCache(
                File(context.cacheDir, "media_cache"),
                LeastRecentlyUsedCacheEvictor(MAX_SIZE),
                StandaloneDatabaseProvider(context),
            ).also { instance = it }
        }
    }
}
