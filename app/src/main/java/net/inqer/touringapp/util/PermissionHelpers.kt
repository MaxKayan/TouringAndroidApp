package net.inqer.touringapp.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.IntRange
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

object PermissionHelpers {

    fun requestPermissionsIfNecessary(context: Context, permissions: Array<String>, activity: Activity, @IntRange(from = 0) requestCode: Int = 123) {
        val permissionsToRequest = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.size > 0) {

            activity.let {
                ActivityCompat.requestPermissions(
                        it,
                        permissionsToRequest.toTypedArray(),
                        requestCode)
            }
        }
    }
}