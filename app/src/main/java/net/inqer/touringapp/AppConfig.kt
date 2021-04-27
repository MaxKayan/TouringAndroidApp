package net.inqer.touringapp

interface AppConfig {
    /**
     * Full URL for the API root.
     * Example: https://tour-up.ru/api/
     */
    val baseUrl: String

    /**
     * Location request interval in milliseconds.
     */
    val locationPollInterval: Int

    /**
     * Enter radius for the waypoint in meters.
     */
    val waypointEnterRadius: Int
}