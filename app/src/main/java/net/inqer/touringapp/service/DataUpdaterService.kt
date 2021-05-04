package net.inqer.touringapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
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

    companion object {
        private const val TAG = "DataUpdaterService"
    }
}