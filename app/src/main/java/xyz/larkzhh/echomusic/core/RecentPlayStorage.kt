package xyz.larkzhh.echomusic.core

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV
import xyz.larkzhh.echomusic.model.SongBean

/** 本地最近播放，按数据空间隔离 */
object RecentPlayStorage {

    private const val MAX_SIZE = 100
    private const val KEY_PREFIX = "recent_play"
    private val gson = Gson()

    private fun key(): String = "${KEY_PREFIX}_${UserSpace.id}"

    fun load(): List<SongBean> {
        val json = MMKV.defaultMMKV().decodeString(key()) ?: return emptyList()
        return runCatching {
            val type = object : TypeToken<List<SongBean>>() {}.type
            gson.fromJson<List<SongBean>>(json, type) ?: emptyList()
        }.getOrDefault(emptyList())
    }

    fun add(song: SongBean) {
        val list = load().toMutableList()
        list.removeAll { it.id == song.id }
        list.add(0, song)
        save(list.take(MAX_SIZE))
    }

    fun clear() {
        MMKV.defaultMMKV().removeValueForKey(key())
    }

    private fun save(list: List<SongBean>) {
        MMKV.defaultMMKV().encode(key(), gson.toJson(list))
    }
}
