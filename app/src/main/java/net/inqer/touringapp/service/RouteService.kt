package net.inqer.touringapp.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import net.inqer.touringapp.MainActivity
import net.inqer.touringapp.R
import net.inqer.touringapp.data.models.Destination
import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.data.models.Waypoint
import net.inqer.touringapp.di.qualifiers.ActiveTourRouteLiveData
import javax.inject.Inject

@AndroidEntryPoint
class RouteService : LifecycleService() {
    @Inject
    @ActiveTourRouteLiveData
    lateinit var activeTourRouteLiveData: LiveData<TourRoute?>

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var notificationManager: NotificationManager

    private lateinit var serviceChannel: NotificationChannel

    private var activeRoute: TourRoute? = null

    //    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        Log.d(TAG, "onStartCommand: $intent ; ${intent?.action} ; $flags ; $startId")

        val input = intent?.getStringExtra(INIT_MESSAGE_EXTRA)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
                this,
                PENDING_INTENT_NOTIFICATION_CODE,
                notificationIntent, 0
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.route_following))
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_outline_map_24)
                .setContentIntent(pendingIntent)
                .build()

        startForeground(1, notification)
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: called")

        activeTourRouteLiveData.observe(this) { route ->
            Log.d(TAG, "onCreate: got route - $route")
            activeRoute = route
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(LocationRequest.create(), object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                Log.d(TAG, "onLocationResult: $p0")
                Toast.makeText(applicationContext, "Локация: ${p0.lastLocation}", Toast.LENGTH_SHORT).show()

                activeRoute?.let { route ->
                    route.waypoints?.forEach {

                    }
                }
            }

            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
                Log.d(TAG, "onLocationAvailability: $p0")
            }
        }, Looper.getMainLooper())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel =
                NotificationChannel(
                        CHANNEL_ID,
                        "Foreground Service Channel",
                        NotificationManager.IMPORTANCE_LOW
                )

        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val TAG = "RouteService"
        const val CHANNEL_ID = "TourRouteControllerService"
        const val CHANNEL_NAME = "Foreground Service Channel"

        private const val INIT_MESSAGE_EXTRA = "initMessageExtra"

        private const val PENDING_INTENT_NOTIFICATION_CODE = 0

        val activeDestination: MutableLiveData<Destination> = MutableLiveData()
        val currentWaypoint: MutableLiveData<Waypoint> = MutableLiveData()
        val targetWaypoint: MutableLiveData<Waypoint> = MutableLiveData()

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