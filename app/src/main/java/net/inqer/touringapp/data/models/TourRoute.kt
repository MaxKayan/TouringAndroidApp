package net.inqer.touringapp.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import net.inqer.touringapp.data.converters.TourItemConverters
import java.util.*

@Entity(tableName = "routes")
@TypeConverters(TourItemConverters::class)
data class TourRoute(
        @PrimaryKey
        @SerializedName("pk")
        val id: Long,
        val title: String,
        val description: String,
        val image: String,

//        @Transient
        @ColumnInfo(defaultValue = "0")
        val isActive: Boolean = false,

        @SerializedName("created_at")
        val createdAt: Date,
        @SerializedName("updated_at")
        val updatedAt: Date,
        @SerializedName("total_distance")
        val totalDistance: Float?,
        @SerializedName("estimated_duration")
        val estimatedDuration: Float?,

        val waypoints: Array<Waypoint>?,
        val destinations: Array<Destination>?,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TourRoute

        if (id != other.id) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (image != other.image) return false
        if (isActive != other.isActive) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false
        if (totalDistance != other.totalDistance) return false
        if (estimatedDuration != other.estimatedDuration) return false
        if (waypoints != null) {
            if (other.waypoints == null) return false
            if (!waypoints.contentEquals(other.waypoints)) return false
        } else if (other.waypoints != null) return false
        if (destinations != null) {
            if (other.destinations == null) return false
            if (!destinations.contentEquals(other.destinations)) return false
        } else if (other.destinations != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + isActive.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        result = 31 * result + (totalDistance?.hashCode() ?: 0)
        result = 31 * result + (estimatedDuration?.hashCode() ?: 0)
        result = 31 * result + (waypoints?.contentHashCode() ?: 0)
        result = 31 * result + (destinations?.contentHashCode() ?: 0)
        return result
    }
}