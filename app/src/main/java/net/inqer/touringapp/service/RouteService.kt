package net.inqer.touringapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import net.inqer.touringapp.MainActivity
import net.inqer.touringapp.R

class RouteService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //do heavy work on a background thread
        val input = intent?.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
                this,
                0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, Companion.CHANNEL_ID)
                .setContentTitle("Foreground Service Kotlin Example")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_baseline_launch_24)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(1, notification)
        //stopSelf();
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java).apply {
                createNotificationChannel(serviceChannel)
            }
        }
    }

    companion object {
        private const val CHANNEL_ID = "ForegroundService Kotlin"
    }
}