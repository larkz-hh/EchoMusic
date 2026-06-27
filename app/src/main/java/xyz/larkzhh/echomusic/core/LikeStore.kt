package xyz.larkzhh.echomusic.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import xyz.larkzhh.echomusic.http.NeteaseApi

/** 喜欢的歌曲 id 缓存，供 UI 显示状态 */
object LikeStore {

    var likedIds by mutableStateOf<Set<Long>>(emptySet())
        private set

    private var loaded = false

    fun isLiked(id: Long): Boolean = likedIds.contains(id)

    fun setLiked(id: Long, liked: Boolean) {
        likedIds = if (liked) likedIds + id else likedIds - id
    }

    fun setAll(ids: Set<Long>) {
        likedIds = ids
        loaded = true
    }

    suspend fun load(api: NeteaseApi, uid: String?) {
        if (loaded || uid == null) {
            return
        }
        runCatching { api.getLikeList(uid) }
            .onSuccess {
                likedIds = it.ids?.toSet() ?: emptySet()
                loaded = true
            }
    }

    fun reset() {
        loaded = false
        likedIds = emptySet()
    }
}
