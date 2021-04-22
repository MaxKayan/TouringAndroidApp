package net.inqer.touringapp.data.models

import net.inqer.touringapp.util.GeoHelpers

data class TargetPoint(
        val distanceResult: GeoHelpers.DistanceResult,
        val waypoint: Waypoint
)
