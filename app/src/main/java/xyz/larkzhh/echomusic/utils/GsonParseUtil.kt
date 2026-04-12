package xyz.larkzhh.echomusic.utils

import com.google.gson.Gson

// 纯解析工具：无Context、纯字符串转实体
object GsonParseUtil {
    private val gson = Gson()

    // 普通对象
    fun <T> fromJson(jsonStr: String, clazz: Class<T>): T? {
        return try {
            gson.fromJson(jsonStr, clazz)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // List集合（备用）
    fun <T> fromJsonList(jsonStr: String, type: java.lang.reflect.Type): List<T>? {
        return try {
            gson.fromJson(jsonStr, type)
        } catch (e: Exception) {
            null
        }
    }
}