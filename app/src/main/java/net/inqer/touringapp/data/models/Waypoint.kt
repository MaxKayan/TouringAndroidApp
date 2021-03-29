package net.inqer.touringapp.data.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class Waypoint(
        @SerializedName("pk")
        val id: Long,
        @SerializedName("route")
        val routeId: Int,
        val index: Int,
        val label: String,
        val latitude: String,
        val longitude: String,

        @SerializedName("created_at")
        val createdAt: Date,
        @SerializedName("updated_at")
        val updatedAt: Date,
)