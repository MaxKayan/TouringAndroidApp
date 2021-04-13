package net.inqer.touringapp.ui.map

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import net.inqer.touringapp.R
import net.inqer.touringapp.data.models.Destination
import net.inqer.touringapp.databinding.DestinationInfoWindowBinding
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class DestinationsMapAdapter(
        private val map: MapView,
        private val layoutInflater: LayoutInflater
) {
    private val context: Context = map.context

    private var currentList: List<Destination> = listOf()

    private fun Destination.geoPoint(): GeoPoint = GeoPoint(this.latitude, this.longitude)

    private val overlaysMap = mutableMapOf<Int, Marker>()

    fun submitList(list: List<Destination>) {
        currentList = list

        clearMarkers()

        for (destination in list) {
            val marker = Marker(map).apply {
                position = destination.geoPoint()
                icon = ContextCompat.getDrawable(context, R.drawable.ic_location)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = destination.title

                setOnMarkerClickListener { marker, _ ->
                    if (marker.isInfoWindowShown) return@setOnMarkerClickListener false

                    closeAllInfoWindows()
                    this.showInfoWindow()
                    Log.d(TAG, "submitList: $marker")
                    true
                }

                infoWindow = DestinationInfoWindow(DestinationInfoWindowBinding.inflate(layoutInflater), map, destination)
            }

            map.overlayManager.add(marker)
            val index = map.overlayManager.indexOf(marker)

            overlaysMap[index] = marker
        }
    }

    private fun clearMarkers() {
        for (index in overlaysMap.keys) {
            map.overlayManager.removeAt(index)
        }
        overlaysMap.clear()
    }

    fun closeAllInfoWindows() {
        overlaysMap.forEach { (_, marker) -> marker.closeInfoWindow() }
    }

    companion object {
        private const val TAG = "DestinationsMapAdapter"
    }
}