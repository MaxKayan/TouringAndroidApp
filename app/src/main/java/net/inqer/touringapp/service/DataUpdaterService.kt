package net.inqer.touringapp.service

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService

class DataUpdaterService : LifecycleService() {
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