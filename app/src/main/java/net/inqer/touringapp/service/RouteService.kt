package net.inqer.touringapp.service

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import net.inqer.touringapp.MainActivity
import net.inqer.touringapp.R
import net.inqer.touringapp.data.models.*
import net.inqer.touringapp.di.qualifiers.ActiveTourRouteLiveData
import net.inqer.touringapp.util.GeoHelpers.findClosestWaypoint
import javax.inject.Inject

@AndroidEntryPoint
class RouteService : LifecycleService() {
    @Inject
    @ActiveTourRouteLiveData
    lateinit var activeTourRouteLiveData: LiveData<TourRoute?>

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var routeDataBus: ActiveRouteDataBus

    private var currentStatus: ServiceAction = ServiceAction.STOP

    private lateinit var serviceChannel: NotificationChannel

    private var activeRoute: TourRoute? = null

    //    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        intent?.let {
            val actionType = it.getSerializableExtra(EXTRA_INTENT_TYPE) as ServiceAction?

            if (actionType == currentStatus) return@let

            when (actionType) {
                ServiceAction.START -> {
                    launchService()
                }
                ServiceAction.STOP -> {
                    stopService()
                }

                else -> return@let
            }

            currentStatus = actionType
        }

        return START_STICKY
    }


    private fun launchService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        subscribeObservers()
        requestLocationUpdates()

//        startService(Intent(applicationContext, RouteService::class.java))
        startForeground(NOTIFICATION_IDENTIFIER, createForegroundNotification())
    }


    private fun stopService() {
        removeLocationUpdates()
        stopSelf()
    }


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: called")
    }


    private fun subscribeObservers() {
        activeTourRouteLiveData.observe(this) { route ->
            Log.d(TAG, "onCreate: got route - $route")
            activeRoute = route
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel =
                NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_LOW
                )

        notificationManager.createNotificationChannel(channel)
    }


    private fun createLocationRequest(): LocationRequest = LocationRequest.create().apply {
        interval = UPDATE_INTERVAL_IN_MILLISECONDS
        fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            Log.d(TAG, "onLocationResult: $locationResult")

            activeRoute?.waypoints?.let { waypoints ->
                Log.d(TAG, "onLocationResult: calculating closest point...")
                val targetPoint = findClosestWaypoint(locationResult.lastLocation, waypoints)
                Log.i(TAG, "onLocationResult: targetPoint - $targetPoint ; ${targetPoint?.distanceResult?.distance}")

                onNewTargetFound(targetPoint)
            }
        }

        override fun onLocationAvailability(p0: LocationAvailability) {
            super.onLocationAvailability(p0)
            Log.d(TAG, "onLocationAvailability: $p0")
        }
    }


    private fun onNewTargetFound(point: TargetPoint?) {
        if (point == null) {
            Log.w(TAG, "onNewTargetFound: target point is null!")
        }
        routeDataBus.closestPoint.postValue(point)

        notificationManager.notify(
                NOTIFICATION_IDENTIFIER,
                createForegroundNotification("Цель - ${point?.waypoint?.latitude} ; ${point?.waypoint?.longitude}" +
                        "Расстояние: ${point?.distanceResult?.distance}")
        )

        Log.i(TAG, "onNewTargetFound: notification sent. - $point")
    }


    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e(TAG, "requestLocationUpdates: Missing required permissions!")
            return
        }
        fusedLocationClient.requestLocationUpdates(
                createLocationRequest(),
                locationCallback,
                Looper.getMainLooper()
        )
    }


    private fun removeLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
//        stopSelf()
    }


    private fun createForegroundNotification(contentText: String = getString(R.string.route_service_text)): Notification? {
        Log.d(TAG, "createForegroundNotification: $contentText")

        val serviceClosePendingIntent = PendingIntent.getActivity(
                this,
                NOTIFICATION_REQUEST_CODE,
                Intent(this, RouteService::class.java).apply {
//                    this.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra(EXTRA_INTENT_TYPE, ServiceAction.STOP)
                },
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        // The PendingIntent to launch activity.
        val activityPendingIntent = PendingIntent.getActivity(this, 0,
                Intent(this, MainActivity::class.java), 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .addAction(R.drawable.ic_baseline_launch_24, getString(R.string.open),
                        activityPendingIntent)
                .addAction(R.drawable.ic_baseline_close_24, getString(R.string.cancel),
                        serviceClosePendingIntent)
                .setContentText(contentText)
                .setContentTitle(getText(R.string.route_service_title))
                .setOngoing(true)
                .setSmallIcon(R.drawable.osm_ic_center_map)
                .setTicker(getText(R.string.route_service_ticker))
//                .setWhen(System.currentTimeMillis())

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.apply {
                setChannelId(CHANNEL_ID)
                priority = NotificationManager.IMPORTANCE_HIGH
            }
        }
        return builder.build()
    }

    companion object {
        private const val TAG = "RouteService"
        private const val NOTIFICATION_IDENTIFIER = 121212
        private const val NOTIFICATION_REQUEST_CODE = 420
        private const val CHANNEL_ID = "TourRouteControllerService"
        private const val CHANNEL_NAME = "Foreground Service Channel"

        private const val EXTRA_INTENT_TYPE = "EXTRA_INTENT_TYPE"

        private const val PENDING_INTENT_NOTIFICATION_CODE = 0

        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS: Long = UPDATE_INTERVAL_IN_MILLISECONDS / 2

        fun startService(context: Context) {
            val startIntent = Intent(context, RouteService::class.java).apply {
                putExtra(EXTRA_INTENT_TYPE, ServiceAction.START)
            }
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, RouteService::class.java).apply {
                putExtra(EXTRA_INTENT_TYPE, ServiceAction.STOP)
            }
            context.startService(stopIntent)
        }

        enum class ServiceAction {
            START,
            STOP
        }
    }
}