package net.inqer.touringapp.ui.map.overlays

import android.location.Location
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class LocationOverlay : MyLocationNewOverlay {
    constructor(mapView: MapView) : super(mapView)
    constructor(myLocationProvider: IMyLocationProvider, mapView: MapView) : super(myLocationProvider, mapView)

    private lateinit var onLocationChangedListener: (location: Location?, source: IMyLocationProvider?) -> Unit
    fun setOnLocationChangedListener(listener: (location: Location?, source: IMyLocationProvider?) -> Unit) {
        onLocationChangedListener = listener
    }

    override fun onLocationChanged(location: Location?, source: IMyLocationProvider?) {
        super.onLocationChanged(location, source)

        if (this::onLocationChangedListener.isInitialized)
            onLocationChangedListener(location, source)
    }
}