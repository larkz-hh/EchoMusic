package xyz.larkzhh.echomusic.utils

import android.annotation.SuppressLint
import android.app.Application
import android.os.Looper
import android.widget.Toast
import java.lang.Exception


private var toast: Toast? = null
private var application: Application? = null


@SuppressLint("ShowToast")
@JvmOverloads
fun showToast(content: String, duration: Int = Toast.LENGTH_SHORT) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        if (toast == null) {
            toast = Toast.makeText(getApplication(), content, duration)
        } else {
            toast?.setText(content)
        }
        toast?.show()
    }
}

private fun getApplication(): Application {
    if (application == null) {
        try {
            application = Class.forName("android.app.ActivityThread")
                .getMethod("currentApplication")
                .invoke(null) as Application
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
    return application!!
}
