package xyz.larkzhh.echomusic.core

import android.content.Context
import java.io.File


/** 本机数据空间：登录用户 / 游客各一份缓存 */
object UserSpace {

    private const val GUEST = "guest"
    private const val ANONYMOUS = "anonymous"

    val id: String
        get() {
            val uid = AppGlobalData.sLoginResult?.profile?.userId
            if (uid != null) {
                return "user_$uid"
            }
            return if (AppGlobalData.isGuest) GUEST else ANONYMOUS
        }

    fun cacheDir(context: Context): File = cacheDirFor(context, id)

    fun cacheDirFor(context: Context, space: String): File =
        File(File(context.cacheDir, "http_cache"), space)
}
