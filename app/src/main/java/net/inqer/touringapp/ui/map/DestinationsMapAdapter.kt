package net.inqer.touringapp.ui.map

import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.FragmentManager
import net.inqer.touringapp.data.models.Destination
import net.inqer.touringapp.databinding.DestinationInfoWindowBinding
import net.inqer.touringapp.ui.dialogs.DestinationBottomSheet
import net.inqer.touringapp.ui.map.overlays.DestinationMarkerOverlay
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class DestinationsMapAdapter(
    private val map: MapView,
    private val layoutInflater: LayoutInflater,
    private val fragmentManager: FragmentManager
) {

    private var currentList: List<Destination> = listOf()

    private fun Destination.geoPoint(): GeoPoint = GeoPoint(this.latitude, this.longitude)

    /**
     * key - [Int] index of the overlay at [MapView.getOverlayManager]
     *
     * value - Wrapper data class ([DestinationMarkerOverlay]), that contains destination status,
     * instance and map marker.
     */
    private val overlaysMap = mutableMapOf<Int, DestinationMarkerOverlay>()

    private var activeDestination: Destination? = null

    fun submitList(list: List<Destination>) {
        currentList = list

        clearMarkers()

        for (destination in list) {
            val marker = DestinationMarkerOverlay(map, destination).apply {
                position = destination.geoPoint()
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = destination.title

                setOnMarkerClickListener { marker, _ ->
                    if (marker.isInfoWindowShown) return@setOnMarkerClickListener false

                    closeAllInfoWindows()
                    this.showInfoWindow()
                    val currentZoom = map.zoomLevelDouble
                    Log.d(TAG, "submitList: zoomLevelDouble = $currentZoom")
                    map.controller.animateTo(
                        position,
                        if (currentZoom < 16.0) 16.0 else currentZoom,
                        1000L
                    )
                    Log.d(TAG, "submitList: $marker")
                    true
                }

                infoWindow = DestinationInfoWindow(
                    DestinationInfoWindowBinding.inflate(layoutInflater),
                    map,
                    destination,
                    {
                        showBottomSheetIfRequired(this, true)
                    },
                    {
                        this.drawRangeEnabled = true
                    },
                    {
                        this.drawRangeEnabled = false
                    }
                )
            }

            map.overlayManager.add(marker)
            val index = map.overlayManager.indexOf(marker)

            overlaysMap[index] = marker

        }
    }


    private fun showBottomSheetIfRequired(
        marker: DestinationMarkerOverlay,
        forced: Boolean = false
    ) {
        if (!forced) {
            val existingSheet = fragmentManager.findFragmentByTag(TAG)
            if (existingSheet !== null) {
                Log.w(
                    TAG,
                    "showBottomSheetIfRequired: This sheet is already opened, skipping. $existingSheet"
                )
                return
            }

            if (marker.detailsViewed) {
                Log.i(
                    TAG, "showBottomSheetIfRequired: this destination sheet is already viewed," +
                            " skipping. $marker"
                )
                return
            }
        }

        DestinationBottomSheet.newInstance(marker.destination) {
            marker.detailsViewed = true
        }.show(fragmentManager, TAG)
    }


    fun setActiveDestination(destination: Destination?) {
        Log.d(TAG, "setActiveDestination: $destination")
        // TODO this optimization cannot be applied currently, because we loose state when we recreate the fragment
//        if (activeDestination != null && activeDestination?.id == destination?.id) {
//            return
//        }

        destination?.let {
            getDestinationMarker(it)?.let { marker ->
                if (marker.status == Destination.Companion.DestinationStatus.VISITED) {
                    Log.w(
                        TAG, "setActiveDestination: Activating destination marker that was " +
                                "already visited before! ; $marker"
                    )
                }

                marker.status = Destination.Companion.DestinationStatus.ACTIVE
                showBottomSheetIfRequired(marker)
            }
        }

        // Set previous active destination as visited, if any.
        activeDestination?.let {
            updateMarkerAppearance(
                it,
                Destination.Companion.DestinationStatus.EMPTY
            )
        }

        // Update current destination variable field
        activeDestination = destination
    }


    private fun getDestinationMarker(destination: Destination): DestinationMarkerOverlay? =
        overlaysMap.values.firstOrNull { it.destination.id == destination.id }


    private fun updateMarkerAppearance(
        destination: Destination,
        newStatus: Destination.Companion.DestinationStatus
    ) {
        getDestinationMarker(destination)?.let { it.status = newStatus }
    }


    private fun clearMarkers() {
        for (index in overlaysMap.keys) {
            map.overlayManager.removeAt(index)
        }
        overlaysMap.clear()
    }

    fun closeAllInfoWindows() {
        overlaysMap.values.forEach { it.closeInfoWindow() }
    }

    companion object {
        private const val TAG = "DestinationsMapAdapter"
    }
}