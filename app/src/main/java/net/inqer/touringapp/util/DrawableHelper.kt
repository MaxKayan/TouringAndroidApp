package net.inqer.touringapp.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

object DrawableHelper {
    private const val TAG = "DrawableHelper"

    @JvmOverloads
    fun modifyFab(context: Context, fab: FloatingActionButton,
                  @DrawableRes iconRes: Int? = null,
                  @ColorRes fabColor: Int? = null, @ColorRes iconColor: Int? = null) {

        if (iconRes != null) {
            val fabIcon = ContextCompat.getDrawable(context, iconRes)
            if (fabIcon == null) {
                Log.e(TAG, "setFabState: fab icon is null!")
                return
            }

            fabIcon.mutate().colorFilter = PorterDuffColorFilter(
                    if (iconColor != null) ContextCompat.getColor(context, iconColor) else Color.WHITE,
                    PorterDuff.Mode.MULTIPLY
            )

            fab.setImageDrawable(fabIcon)
        }

        if (fabColor != null)
            fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, fabColor))
    }
}