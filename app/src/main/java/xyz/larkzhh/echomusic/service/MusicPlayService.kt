package xyz.larkzhh.echomusic.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.content.ContextCompat
import xyz.larkzhh.echomusic.EchoApplication
import xyz.larkzhh.echomusic.broadcast.MusicNotificationReceiver
import xyz.larkzhh.echomusic.utils.MusicNotificationHelper


class MusicPlayService : Service() {

    private var mReceiver: MusicNotificationReceiver? = null

    companion object {
        const val ACTION_START_MUSIC_SERVICE = "ACTION_START_MUSIC_SERVICE"
        fun start() {
            val intent =  Intent(EchoApplication.context, MusicPlayService::class.java).apply {
                action = ACTION_START_MUSIC_SERVICE
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                EchoApplication.context.startForegroundService(intent)
            } else {
                EchoApplication.context.startService(intent)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerBroadcastReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let {
            if (it == ACTION_START_MUSIC_SERVICE) {
                MusicNotificationHelper.init {
                    startForeground(MusicNotificationHelper.NOTIFICATION_ID, MusicNotificationHelper.getNotification())
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterBroadcastReceiver()
    }

    override fun onBind(intent: Intent?) = null

    private fun registerBroadcastReceiver() {
        if (mReceiver == null) {
            val receiver = MusicNotificationReceiver()
            mReceiver = receiver
            // Android 14+ 需声明接收器是否导出
            ContextCompat.registerReceiver(
                this,
                receiver,
                IntentFilter(MusicNotificationReceiver.ACTION_MUSIC_NOTIFICATION),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }
    }

    private fun unRegisterBroadcastReceiver() {
        mReceiver?.let {
            unregisterReceiver(it)
        }
    }

}