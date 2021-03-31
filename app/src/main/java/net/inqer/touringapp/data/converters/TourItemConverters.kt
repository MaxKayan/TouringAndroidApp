package net.inqer.touringapp.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import net.inqer.touringapp.data.models.Destination
import net.inqer.touringapp.data.models.Waypoint

class TourItemConverters {
    @TypeConverter
    fun waypointsToJson(value: Array<Waypoint>?): String? = Gson().toJson(value, Array<Waypoint>::class.java)

    @TypeConverter
    fun jsonToWaypoints(json: String?): Array<Waypoint>? = Gson().fromJson(json, Array<Waypoint>::class.java)

    @TypeConverter
    fun destinationsToJson(value: Array<Destination>?): String? = Gson().toJson(value, Array<Destination>::class.java)

    @TypeConverter
    fun jsonToDestinations(json: String?): Array<Destination>? = Gson().fromJson(json, Array<Destination>::class.java)
}