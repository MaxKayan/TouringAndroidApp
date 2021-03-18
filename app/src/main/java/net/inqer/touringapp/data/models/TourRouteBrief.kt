package net.inqer.touringapp.data.models

import com.google.gson.annotations.SerializedName

data class TourRouteBrief(
        @SerializedName("pk")
        val id: Int,
        val created_at: String,
        val description: String,
        val image: Any,
        val title: String,
        val updated_at: String
)