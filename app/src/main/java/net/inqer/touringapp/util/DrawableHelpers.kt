package net.inqer.touringapp.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

object DrawableHelpers {
    private const val TAG = "DrawableHelper"

    @JvmOverloads
    fun modifyFab(
        context: Context, fab: FloatingActionButton,
        @DrawableRes iconRes: Int? = null,
        @ColorRes fabColor: Int? = null, @ColorRes iconColor: Int? = null
    ) {

        if (iconRes != null) {
            val fabIcon = ContextCompat.getDrawable(context, iconRes)
            if (fabIcon == null) {
                Log.e(TAG, "setFabState: fab icon is null!")
                return
            }
            val icon = fabIcon.constantState?.newDrawable()

            icon?.mutate()?.colorFilter = PorterDuffColorFilter(
                if (iconColor != null) ContextCompat.getColor(context, iconColor) else Color.WHITE,
                PorterDuff.Mode.MULTIPLY
            )

            fab.setImageDrawable(icon)
        }

        if (fabColor != null)
            fab.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, fabColor))
    }


    fun modifyButtonIcon(
        context: Context, button: MaterialButton, @DrawableRes iconRes: Int? = null,
        @ColorRes iconColorRes: Int? = null,
    ) {
        modifyButtonIcon(
            button,
            if (iconRes != null) ContextCompat.getDrawable(context, iconRes) else null,
            if (iconColorRes != null) ContextCompat.getColor(context, iconColorRes) else null
        )
    }


    fun modifyButtonIcon(
        button: MaterialButton, icon: Drawable? = null,
        @ColorInt iconColor: Int? = null
    ) {
        if (icon == button.icon) {
            Log.i(TAG, "modifyButtonIcon: icon is the same, skipping")
        }
        if (icon != null) {
            val newIcon = icon.constantState?.newDrawable()
            newIcon?.mutate()?.colorFilter = PorterDuffColorFilter(
                iconColor ?: Color.WHITE,
                PorterDuff.Mode.MULTIPLY
            )

            button.icon = newIcon
        }
    }


    fun getThemePaintedDrawable(
        context: Context,
        @DrawableRes drawableRes: Int,
        @AttrRes colorAttribute: Int,
        alpha: Int = 255,
        mode: PorterDuff.Mode = PorterDuff.Mode.MULTIPLY,
    ): Drawable? =
        getPaintedDrawable(context, drawableRes, context.getThemeColor(colorAttribute), alpha, mode)

    fun getResPaintedDrawable(
        context: Context,
        @DrawableRes drawableRes: Int,
        @ColorRes colorRes: Int,
        alpha: Int = 255,
        mode: PorterDuff.Mode = PorterDuff.Mode.MULTIPLY
    ): Drawable? =
        getPaintedDrawable(
            context,
            drawableRes,
            ContextCompat.getColor(context, colorRes),
            alpha,
            mode
        )

    private fun getPaintedDrawable(
        context: Context,
        @DrawableRes drawableRes: Int,
        @ColorInt color: Int = Color.WHITE,
        alpha: Int = 255,
        mode: PorterDuff.Mode = PorterDuff.Mode.MULTIPLY
    ): Drawable? {
        val drawable = ContextCompat.getDrawable(context, drawableRes) ?: return null

        drawable.mutate().colorFilter = PorterDuffColorFilter(
            color,
            mode
        )

        drawable.alpha = alpha

        return drawable
    }
}

@ColorInt
fun Context.getThemeColor(@AttrRes attribute: Int) =
    TypedValue().let { theme.resolveAttribute(attribute, it, true); it.data }