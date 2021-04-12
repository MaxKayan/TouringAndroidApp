package net.inqer.touringapp.util

import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint


object GeoHelpers {
    fun calculatePointBetween(p1: GeoPoint, p2: GeoPoint): GeoPoint {
        val bearing = p1.bearingTo(p2)
        val distance = p1.distanceToAsDouble(p2)
        return p1.destinationPoint(distance / 2, bearing)
    }

    fun calculateArea(points: List<GeoPoint>): BoundingBox? {
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
}