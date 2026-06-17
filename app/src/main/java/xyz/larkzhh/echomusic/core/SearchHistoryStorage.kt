package xyz.larkzhh.echomusic.core

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV

/** 搜索历史，按数据空间隔离 */
object SearchHistoryStorage {

    private const val MAX_SIZE = 20
    private const val KEY_PREFIX = "search_history"
    private val gson = Gson()

    private fun key(): String = "${KEY_PREFIX}_${UserSpace.id}"

    fun load(): List<String> {
        val json = MMKV.defaultMMKV().decodeString(key()) ?: return emptyList()
        return runCatching {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson<List<String>>(json, type) ?: emptyList()
        }.getOrDefault(emptyList())
    }

    fun add(keyword: String) {
        val trimmed = keyword.trim()
        if (trimmed.isEmpty()) {
            return
        }
        val list = load().toMutableList()
        list.remove(trimmed)
        list.add(0, trimmed)
        save(list.take(MAX_SIZE))
    }

    fun remove(keyword: String) {
        save(load().filterNot { it == keyword })
    }

    fun clear() {
        MMKV.defaultMMKV().removeValueForKey(key())
    }

    private fun save(list: List<String>) {
        MMKV.defaultMMKV().encode(key(), gson.toJson(list))
    }
}
