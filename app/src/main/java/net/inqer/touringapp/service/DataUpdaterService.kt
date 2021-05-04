package net.inqer.touringapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.inqer.touringapp.data.repository.main.MainRepository
import javax.inject.Inject

/**
 * Updates full [net.inqer.touringapp.data.models.TourRoute] data from the API.
 */
@AndroidEntryPoint
class DataUpdaterService : LifecycleService() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var routeRepository: MainRepository

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: called")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(TAG, "onStartCommand: $intent")

        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateRouteData(routeId: Long) {
        lifecycleScope.launchWhenCreated {
            launch {
                routeRepository.refreshFullRouteData(routeId)
                Log.d(TAG, "updateRouteData: finished.")
                stopSelf()
            }
        }
    }


    /**
     * Create the foreground service notification channel for API >= 26
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

        notificationManager.createNotificationChannel(channel)
    }


    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID).let {
            it.setContentTitle("Синхронизация данных маршрута...")
            it.setProgress(100, 50, true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.setChannelId(CHANNEL_ID)
                it.priority = NotificationManager.IMPORTANCE_LOW
            }

            return@let it.build()
        }
    }

    companion object {
        private const val TAG = "DataUpdaterService"
        private const val EXTRA_ROUTE_ID = "EXTRA_ROUTE_ID"

        private const val CHANNEL_ID = "TOUR_DATA_UPDATER_SERVICE_ID"
        private const val CHANNEL_NAME = "TOUR_DATA_UPDATER_SERVICE_NAME"

        private const val NOTIFICATION_ID = 131214

        fun initiateRouteSync(context: Context, routeId: Long) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, DataUpdaterService::class.java).apply {
                    this.putExtra(EXTRA_ROUTE_ID, routeId)
                }
            )
        }
    }
}