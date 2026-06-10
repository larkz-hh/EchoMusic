package xyz.larkzhh.echomusic.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities


object NetworkUtil {

    /** 是否连在 Wi-Fi / 有线网络 */
    fun isWifi(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}
