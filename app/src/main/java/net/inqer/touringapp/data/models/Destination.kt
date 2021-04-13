package net.inqer.touringapp.data.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class Destination(
        val description: String,
        val latitude: Double,
        val longitude: Double,
        val photos: List<Photo>,
        val radius: Int,

        @SerializedName("route")
        val routeId: Long,

        val title: String,
        val type: String,

        @SerializedName("created_at")
        val createdAt: Date,
        @SerializedName("updated_at")
        val updatedAt: Date,
)