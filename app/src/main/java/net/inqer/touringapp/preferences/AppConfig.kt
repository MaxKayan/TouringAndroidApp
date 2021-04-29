package net.inqer.touringapp.preferences

import androidx.lifecycle.LiveData

interface AppConfig {
    val liveData: LiveData<AppConfig>

    /**
     * Full URL for the API root.
     * Example: https://tour-up.ru/api/
     */
    var baseUrl: String

    /**
     * Location request interval in milliseconds.
     */
    var locationPollInterval: Int

    /**
     * Enter radius for the waypoint in meters.
     */
    var waypointEnterRadius: Int

    /**
     * Always shorten path to the closest waypoint forwards.
     */
    var alwaysShortenPaths: Boolean
}