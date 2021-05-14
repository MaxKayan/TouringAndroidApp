package net.inqer.touringapp.ui.map

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import net.inqer.touringapp.R
import net.inqer.touringapp.data.models.Destination
import net.inqer.touringapp.databinding.DestinationInfoWindowBinding
import net.inqer.touringapp.util.DrawableHelpers
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class DestinationsMapAdapter(
    private val map: MapView,
    private val layoutInflater: LayoutInflater,
) {
    private val context: Context = map.context

    private var currentList: List<Destination> = listOf()

    private fun Destination.geoPoint(): GeoPoint = GeoPoint(this.latitude, this.longitude)

    /**
     * key - [Int] index of the overlay at [MapView.getOverlayManager]
     *
     * value - Wrapper data class ([DestinationMarker]), that contains destination status,
     * instance and map marker.
     */
    private val overlaysMap = mutableMapOf<Int, DestinationMarker>()

    private var activeDestination: Destination? = null

    fun submitList(list: List<Destination>) {
        currentList = list

        clearMarkers()

        for (destination in list) {
            val marker = Marker(map).apply {
                position = destination.geoPoint()
                icon = ContextCompat.getDrawable(context, ICON_RES)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = destination.title

                setOnMarkerClickListener { marker, _ ->
                    if (marker.isInfoWindowShown) return@setOnMarkerClickListener false

                    closeAllInfoWindows()
                    this.showInfoWindow()
                    Log.d(TAG, "submitList: $marker")
                    true
                }

                infoWindow = DestinationInfoWindow(
                    DestinationInfoWindowBinding.inflate(layoutInflater),
                    map,
                    destination
                )
            }

            map.overlayManager.add(marker)
            val index = map.overlayManager.indexOf(marker)

            overlaysMap[index] = DestinationMarker(
                DestinationMarkerStatus.INACTIVE,
                destination,
                marker
            )

        }
    }


    fun setActiveDestination(destination: Destination?) {
        if (activeDestination != null && activeDestination?.id == destination?.id) {
            return
        }

        if (destination != null) {
            val destinationMarker = getDestinationMarker(destination)
            if (destinationMarker != null) {
                if (destinationMarker.status == DestinationMarkerStatus.VISITED) {
                    Log.w(
                        TAG, "setActiveDestination: Setting destination marker that was " +
                                "already visited before! ; $destinationMarker"
                    )
                }

                updateMarkerAppearance(destinationMarker, DestinationMarkerStatus.ACTIVE)
            }
        }

        // Set previous active destination as visited, if any.
        activeDestination?.let { updateMarkerAppearance(it, DestinationMarkerStatus.VISITED) }
        // Update current destination variable field
        activeDestination = destination
    }


    private fun getDestinationMarker(destination: Destination): DestinationMarker? =
        overlaysMap.values.firstOrNull { it.destination.id == destination.id }


    private fun updateMarkerAppearance(
        destination: Destination,
        newStatus: DestinationMarkerStatus
    ) {
        getDestinationMarker(destination)?.let { updateMarkerAppearance(it, newStatus) }
    }

    private fun updateMarkerAppearance(
        destinationMarker: DestinationMarker,
        newStatus: DestinationMarkerStatus
    ) {
        when (newStatus) {
            DestinationMarkerStatus.INACTIVE -> {
                destinationMarker.marker.icon = DrawableHelpers.getResPaintedDrawable(
                    context, ICON_RES, android.R.color.holo_blue_bright
                )
            }
            DestinationMarkerStatus.ACTIVE -> {
                destinationMarker.marker.icon = DrawableHelpers.getThemePaintedDrawable(
                    context, ICON_RES, R.attr.colorPrimary
                )
            }
            DestinationMarkerStatus.VISITED -> {
                destinationMarker.marker.icon = DrawableHelpers.getResPaintedDrawable(
                    context, ICON_RES, android.R.color.darker_gray
                )
            }
        }
    }


    private fun clearMarkers() {
        for (index in overlaysMap.keys) {
            map.overlayManager.removeAt(index)
        }
        overlaysMap.clear()
    }

    fun closeAllInfoWindows() {
        overlaysMap.values.forEach { it.marker.closeInfoWindow() }
    }

    companion object {
        private const val TAG = "DestinationsMapAdapter"

        @DrawableRes
        private const val ICON_RES: Int = R.drawable.ic_location

        data class DestinationMarker(
            val status: DestinationMarkerStatus,
            val destination: Destination,
            val marker: Marker
        )

        enum class DestinationMarkerStatus {
            INACTIVE,
            ACTIVE,
            VISITED
        }
    }
}