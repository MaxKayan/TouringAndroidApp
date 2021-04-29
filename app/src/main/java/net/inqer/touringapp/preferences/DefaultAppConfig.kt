package net.inqer.touringapp.preferences

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import net.inqer.touringapp.R
import net.inqer.touringapp.SettingsConstants
import javax.inject.Inject


class DefaultAppConfig @Inject constructor(
        @ApplicationContext private val context: Context,
        private val preferences: SharedPreferences
) : AppConfig {

    private val mutableLiveData: MutableLiveData<AppConfig> = MutableLiveData(this)
    override val liveData: LiveData<AppConfig> = mutableLiveData

    /**
     * @param key String key to access a value from shared preferences.
     * @param value Should be of type either [String], [Int] or [Boolean].
     */
    private fun <T> write(key: String, value: T, notify: Boolean = true) where T : Any {
        preferences.edit().let {
            when (value) {
                is String -> {
                    it.putString(key, value)
                }
                is Int -> {
                    it.putInt(key, value)
                }
                is Boolean -> {
                    it.putBoolean(key, value)
                }

                else -> {
                    Log.e(TAG, "write: unexpected value type! ${value::class.java} ; $value ")
                    return
                }
            }

            if (it.commit() && notify) {
                mutableLiveData.postValue(this)
            }
        }
    }


    private val baseUrlKey = context.getString(R.string.key_main_server_address)
    override var baseUrl: String
        get() = preferences.getString(
                baseUrlKey,
                SettingsConstants.DEFAULT_URL
        ) ?: SettingsConstants.DEFAULT_URL
        set(value) = write(baseUrlKey, value)

    private val locationPollIntervalKey = context.getString(R.string.key_location_poll_interval)
    override var locationPollInterval: Int
        get() = preferences.getInt(
                locationPollIntervalKey,
                10000
        )
        set(value) = write(locationPollIntervalKey, value)

    private val waypointEnterRadiusKey = context.getString(R.string.key_location_waypoint_radius)
    override var waypointEnterRadius: Int
        get() {
            val result = preferences.getInt(
                    waypointEnterRadiusKey,
                    15
            )

            Log.d(TAG, "waypointEnterRadius: $result ; ${preferences.all}")

            return result
        }
        set(value) = write(waypointEnterRadiusKey, value)

    private val alwaysShortenPathsKey = context.getString(R.string.key_always_shorten_path)
    override var alwaysShortenPaths: Boolean
        get() = preferences.getBoolean(alwaysShortenPathsKey, false)
        set(value) = write(alwaysShortenPathsKey, value)

    companion object {
        private const val TAG = "DefaultAppConfig"
    }
}
