package net.inqer.touringapp.data.models

import android.util.Log
import androidx.lifecycle.MutableLiveData

/**
 * TODO: This is probably a temporary solution. LiveData bus is not very fast. Should look into binding the [net.inqer.touringapp.service.RouteService]
 */
data class ActiveRouteDataBus(
        /**
         * The destination that is currently in active range if any.
         */
        val activeDestination: MutableLiveData<Destination?> = MutableLiveData(),

        /**
         * The target waypoint that user should head onto.
         */
        val targetWaypoint: MutableLiveData<Waypoint?> = MutableLiveData(),
        /**
         * The target waypoint index that user should head onto.
         */
        val targetWaypointIndex: MutableLiveData<Int?> = MutableLiveData(),

        /**
         *  The calculated distance and bearing to the current target waypoint.
         */
        val targetWaypointCalculatedDistance: MutableLiveData<CalculatedPoint?> = MutableLiveData(),
        /**
         *  The calculated distance and bearing to the closest waypoint.
         */
        val closestWaypointCalculatedPoint: MutableLiveData<CalculatedPoint?> = MutableLiveData()
) {
    fun clear() {
        Log.i(TAG, "clear: called! Setting all route bus data to null.")
        closestWaypointCalculatedPoint.value = null
        activeDestination.value = null
        targetWaypoint.value = null
        targetWaypointIndex.value = null
        targetWaypointCalculatedDistance.value = null
    }

    companion object {
        private const val TAG = "ActiveRouteDataBus"
    }
}
