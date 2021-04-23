package net.inqer.touringapp.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import org.osmdroid.api.IMapView
import org.osmdroid.util.NetworkLocationIgnorer
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer
import java.util.*
import javax.inject.Inject

/**
 * location provider, by default, uses [LocationManager.GPS_PROVIDER] and [LocationManager.NETWORK_PROVIDER]
 */
class GpsLocationProvider @Inject constructor(
        @ApplicationContext context: Context,
        private var mLocationManager: LocationManager
) : GpsMyLocationProvider(context) {
//    private var mLocationManager: LocationManager?
    private var mLocation: Location? = null
    private var mMyLocationConsumer: IMyLocationConsumer? = null

    //    /**
//     * Set the minimum distance for location updates. See
//     * [LocationManager.requestLocationUpdates]. Note
//     * that you should call this before calling [MyLocationNewOverlay.enableMyLocation].
//     *
//     * in meters
//     */
//    var locationUpdateMinDistance = 0.0f
    private var mIgnorer: NetworkLocationIgnorer? = NetworkLocationIgnorer()
    private val locationSources: MutableSet<String> = HashSet()


    init {
        mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationSources.add(LocationManager.GPS_PROVIDER)
        locationSources.add(LocationManager.NETWORK_PROVIDER)
    }


    // ===========================================================
    // Getter & Setter
    // ===========================================================
    /**
     * removes all sources, again, only useful before startLocationProvider is called
     */
    override fun clearLocationSources() {
        locationSources.clear()
    }

    /**
     * adds a new source to listen for location data. Has no effect after startLocationProvider has been called
     * unless startLocationProvider is called again
     */
    override fun addLocationSource(source: String) {
        locationSources.add(source)
    }

    /**
     * returns the live list of GPS sources that we accept, changing this list after startLocationProvider
     * has no effect unless startLocationProvider is called again
     * @return
     */
    override fun getLocationSources(): Set<String> {
        return locationSources
    }
    //
    // IMyLocationProvider
    //
    /**
     * Enable location updates and show your current location on the map. By default this will
     * request location updates as frequently as possible, but you can change the frequency and/or
     * distance by calling [.setLocationUpdateMinTime] and/or [ ][.setLocationUpdateMinDistance] before calling this method.
     */
    override fun startLocationProvider(myLocationConsumer: IMyLocationConsumer): Boolean {
        mMyLocationConsumer = myLocationConsumer
        var result = false

        if (mLocationManager != null) {
            mLocationManager?.let {
                for (provider in it.getProviders(true)) {
                    if (locationSources.contains(provider)) {
                        try {
                            it.requestLocationUpdates(provider, locationUpdateMinTime,
                                    locationUpdateMinDistance, this)
                            result = true
                        } catch (ex: SecurityException) {
                            Log.e(IMapView.LOGTAG, "Unable to attach listener for location provider $provider check permissions?", ex)
                        }
                    }
                }
                return result
            }
        } else {
            Log.e(TAG, "startLocationProvider: location manager is null! this instance - $this")
        }

        return false
    }

    @SuppressLint("MissingPermission")
    override fun stopLocationProvider() {
        mMyLocationConsumer = null
        if (mLocationManager != null) {
            try {
                mLocationManager?.removeUpdates(this)
            } catch (ex: Throwable) {
                Log.w(IMapView.LOGTAG, "Unable to deattach location listener", ex)
            }
        }
    }

    override fun getLastKnownLocation(): Location? {
        return mLocation
    }

    override fun destroy() {
        stopLocationProvider()
        mLocation = null
//        mLocationManager = null
        mMyLocationConsumer = null
        mIgnorer = null
    }

    //
    // LocationListener
    //
    override fun onLocationChanged(location: Location) {
        if (mIgnorer == null) {
            Log.w(IMapView.LOGTAG, "GpsMyLocation provider, mIgnore is null, unexpected. Location update will be ignored")
            return
        }
        if (location.provider == null) return
        // ignore temporary non-gps fix
        if (mIgnorer?.shouldIgnore(location.provider, System.currentTimeMillis()) == true) return
        mLocation = location
        if (mMyLocationConsumer != null && mLocation != null) mMyLocationConsumer?.onLocationChanged(mLocation, this)
    }

    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    companion object {
        private const val TAG = "GpsLocationProvider"
    }
}