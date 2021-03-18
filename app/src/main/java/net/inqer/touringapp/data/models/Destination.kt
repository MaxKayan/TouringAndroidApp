package net.inqer.touringapp.data.models

data class Destination(
        val created_at: String,
        val description: String,
        val latitude: String,
        val longitude: String,
        val photos: List<Photo>,
        val radius: Int,
        val route: Int,
        val title: String,
        val type: String,
        val updated_at: String
)