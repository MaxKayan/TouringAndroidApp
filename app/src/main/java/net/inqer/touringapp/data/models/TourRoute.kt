package net.inqer.touringapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "routes")
data class TourRoute(
        @SerializedName("pk")
        @PrimaryKey
        val id: Int,
        val created_at: String,
        val description: String,
        val destinations: List<Destination>,
        val image: String,
        val title: String,
        val updated_at: String,
        val waypoints: List<Waypoint>
)