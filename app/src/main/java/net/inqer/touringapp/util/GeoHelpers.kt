package net.inqer.touringapp.util

import android.location.Location
import net.inqer.touringapp.data.models.Waypoint
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint


object GeoHelpers {
    fun calculatePointBetween(p1: GeoPoint, p2: GeoPoint): GeoPoint {
        val bearing = p1.bearingTo(p2)
        val distance = p1.distanceToAsDouble(p2)
        return p1.destinationPoint(distance / 2, bearing)
    }

    fun calculateArea(points: List<GeoPoint>): BoundingBox {
        var nord = 0.0
        var sud = 0.0
        var ovest = 0.0
        var est = 0.0
        for (i in points.indices) {
            val lat = points[i].latitude
            val lon = points[i].longitude
            if (i == 0 || lat > nord) nord = lat
            if (i == 0 || lat < sud) sud = lat
            if (i == 0 || lon < ovest) ovest = lon
            if (i == 0 || lon > est) est = lon
        }
        return BoundingBox(nord, est, sud, ovest)
    }

    data class DistanceResult(
            val distance: Float,
            val initialBearing: Float,
            val finalBearing: Float
    )


    fun distanceBetween(location: Location, waypoint: Waypoint) =
            distanceBetween(location.latitude, location.longitude, waypoint.latitude, waypoint.longitude)


    fun distanceBetween(waypoint: Waypoint, waypoint2: Waypoint) =
            distanceBetween(waypoint.latitude, waypoint.longitude, waypoint2.latitude, waypoint2.longitude)


    private fun distanceBetween(startLatitude: Double, startLongitude: Double,
                                endLatitude: Double, endLongitude: Double): DistanceResult {
        val results = FloatArray(3)
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results)
        return DistanceResult(results[0], results[1], results[2])
    }
}