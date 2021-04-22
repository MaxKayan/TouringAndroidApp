package net.inqer.touringapp.data.models

import androidx.lifecycle.MutableLiveData

data class ActiveRouteDataBus(
        val activeDestinationLiveData: MutableLiveData<Destination> = MutableLiveData(),
        val currentWaypointLiveData: MutableLiveData<Waypoint> = MutableLiveData(),
        val targetWaypointLiveData: MutableLiveData<TargetPoint> = MutableLiveData(),
        val closestPoint: MutableLiveData<TargetPoint> = MutableLiveData()
)
