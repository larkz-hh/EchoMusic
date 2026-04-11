package xyz.larkzhh.echomusic.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import xyz.larkzhh.echomusic.model.LoginResult
import xyz.larkzhh.echomusic.utils.KVCache
import xyz.larkzhh.echomusic.utils.KVCacheParcelableExt
import xyz.larkzhh.echomusic.utils.kvCache



object AppGlobalData {

    var sLoginRefreshFlag by mutableStateOf(false)

    // 带版本后缀，兼容旧数据
    var sLoginResult by KVCacheParcelableExt("sLoginResultV2", LoginResult::class.java)
    var sIsLogin by mutableStateOf(false)

    /** 游客模式 */
    var isGuest by kvCache(false)

    /** 当前登录用户 uid */
    val currentUid: String?
        get() = sLoginResult?.profile?.userId?.toString()

    /** 退出登录 */
    fun logout() {
        KVCache.remove("sLoginResultV2")
        isGuest = false
        sLoginRefreshFlag = !sLoginRefreshFlag
    }

    var uid by mutableStateOf("32953014")
}
