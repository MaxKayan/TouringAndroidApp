package net.inqer.touringapp.data.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class Waypoint(
        @SerializedName("pk")
        val id: Long,
        @SerializedName("route")
        val routeId: Long,
        val index: Int,
        val label: String,
        val latitude: Double,
        val longitude: Double,

        @SerializedName("created_at")
        val createdAt: Date,
        @SerializedName("updated_at")
        val updatedAt: Date,
)