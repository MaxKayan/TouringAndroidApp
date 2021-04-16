package net.inqer.touringapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import net.inqer.touringapp.MainActivity
import net.inqer.touringapp.R

@AndroidEntryPoint
class RouteService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //do heavy work on a background thread
        val input = intent?.getStringExtra(INIT_MESSAGE_EXTRA)
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
                this,
                0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, Companion.CHANNEL_ID)
                .setContentTitle(getString(R.string.route_following))
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_outline_map_24)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(1, notification)
//        stopSelf()
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT)
            getSystemService(NotificationManager::class.java).apply {
                this.createNotificationChannel(serviceChannel)
            }
        }
    }

    companion object {
        private const val CHANNEL_ID = "TourRouteControllerService"

        private const val INIT_MESSAGE_EXTRA = "initMessageExtra"

        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, RouteService::class.java)
            startIntent.putExtra(INIT_MESSAGE_EXTRA, message)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, RouteService::class.java)
            context.stopService(stopIntent)
        }
    }
}