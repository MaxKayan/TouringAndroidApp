package net.inqer.touringapp.data.models

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.osmdroid.util.GeoPoint
import java.util.*

data class Destination(
    @PrimaryKey
    @SerializedName("pk")
    val id: Long,

    val latitude: Double,
    val longitude: Double,

    @SerializedName("photos")
    val destinationPhotos: List<DestinationPhoto>,

    val radius: Float,

    @SerializedName("route")
    val routeId: Long,

    val title: String,
    val type: String,

    @SerializedName("short_description")
    val shortDescription: String?,
    val description: String?,
    val address: String?,

    @SerializedName("created_at")
    val createdAt: Date,
    @SerializedName("updated_at")
    val updatedAt: Date,
) {
    fun getGeoPoint() = GeoPoint(this.latitude, this.longitude)

    companion object {
        enum class DestinationStatus {
            EMPTY,
            UNVISITED,
            ACTIVE,
            VISITED
        }
    }
}