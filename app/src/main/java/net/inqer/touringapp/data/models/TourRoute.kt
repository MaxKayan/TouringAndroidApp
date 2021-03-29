package net.inqer.touringapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "routes")
data class TourRoute(
        @PrimaryKey
        @SerializedName("pk")
        val id: Long,
        val title: String,
        val description: String,
        val image: String,

        @SerializedName("created_at")
        val createdAt: Date,
        @SerializedName("updated_at")
        val updatedAt: Date,
        @SerializedName("total_distance")
        val totalDistance: Float,
        @SerializedName("estimated_duration")
        val estimatedDuration: Float,

        val waypoints: List<Waypoint>,
        val destinations: List<Destination>,
)