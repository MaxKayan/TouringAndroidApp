package net.inqer.touringapp.data.models

import com.google.gson.annotations.SerializedName

data class Photo(
        val created_at: String,
        @SerializedName("destination")
        val destinationId: Int,
        val image: String,
        val updated_at: String
)