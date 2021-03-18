package net.inqer.touringapp.data.models

import com.google.gson.annotations.SerializedName

data class Waypoint(
        @SerializedName("pk")
        val id: Int,
        @SerializedName("route")
        val routeId: Int,
        val index: Int,
        val label: String,
        val latitude: String,
        val longitude: String,
        val created_at: String,
        val updated_at: String
)