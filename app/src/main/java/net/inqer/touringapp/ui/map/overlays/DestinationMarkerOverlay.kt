package net.inqer.touringapp.ui.map.overlays

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import net.inqer.touringapp.R
import net.inqer.touringapp.data.models.Destination
import net.inqer.touringapp.util.DrawableHelpers
import net.inqer.touringapp.util.getThemeColor
import org.osmdroid.util.TileSystem
import org.osmdroid.views.MapView
import org.osmdroid.views.Projection
import org.osmdroid.views.overlay.Marker

class DestinationMarkerOverlay(
    mapView: MapView,
    val destination: Destination,
) : Marker(mapView) {

    private val context: Context = mapView.context

//    private val geoPoint: GeoPoint = destination.getGeoPoint()

//    private val iconPaint: Paint = Paint().apply {
//        isFilterBitmap = true
//    }

    private val defaultColor = context.getThemeColor(R.attr.colorPrimary)
    private val activeColor = ContextCompat.getColor(context, R.color.active_marker)
    private val disabledColor = ContextCompat.getColor(context, android.R.color.darker_gray)

    private val circlePaint = Paint().apply {
        this.color = defaultColor
        this.isAntiAlias = true
        this.alpha = 50
        this.style = Paint.Style.FILL
    }
    private val circleOutlinePaint = Paint().apply {
        this.color = defaultColor
        this.isAntiAlias = true
        this.alpha = 150
        this.style = Paint.Style.STROKE
    }

    var drawRangeEnabled = false

    var detailsViewed = false

    var status: Destination.Companion.DestinationStatus =
        Destination.Companion.DestinationStatus.EMPTY
        set(value) {
            if (value == field) {
                Log.w(TAG, "status: new status is the same, skipping... ; $value ; $field")
                return
            }

            when (value) {
                Destination.Companion.DestinationStatus.UNVISITED,
                Destination.Companion.DestinationStatus.EMPTY -> {
                    icon = DrawableHelpers.getPaintedDrawable(context, ICON_RES, defaultColor)
                    setRangeCircleColor(defaultColor)
                }
                Destination.Companion.DestinationStatus.ACTIVE -> {
                    icon = DrawableHelpers.getPaintedDrawable(context, ICON_RES, activeColor)
                    setRangeCircleColor(activeColor)
                }
                Destination.Companion.DestinationStatus.VISITED -> {
                    icon = DrawableHelpers.getPaintedDrawable(context, ICON_RES, disabledColor)
                    setRangeCircleColor(disabledColor)
                }
            }

            field = value
        }

    init {
        title = destination.title
        snippet = destination.description

        // Sets initial status and marker's icon
        status = Destination.Companion.DestinationStatus.UNVISITED
        alpha = ALPHA
    }


    private fun setRangeCircleColor(@ColorInt color: Int) {
        circlePaint.apply {
            this.color = color
            this.alpha = 50
        }
        circleOutlinePaint.apply {
            this.color = color
            this.alpha = 150
        }
    }


    private fun drawRangeCircle(projection: Projection, canvas: Canvas) {
        val radius = destination.radius / TileSystem.GroundResolution(
            destination.latitude,
            projection.zoomLevel
        ).toFloat()

//        Log.d(TAG, "draw: init radius = ${destination.radius} , final = $radius")

//            val radius = destination.radius

        canvas.drawCircle(
            mPositionPixels.x.toFloat(),
            mPositionPixels.y.toFloat(),
            radius,
            circlePaint
        )

        canvas.drawCircle(
            mPositionPixels.x.toFloat(),
            mPositionPixels.y.toFloat(),
            radius,
            circleOutlinePaint
        )
    }

    override fun drawAt(pCanvas: Canvas?, pX: Int, pY: Int, pOrientation: Float) {
        super.drawAt(pCanvas, pX, pY, pOrientation)
    }

    override fun draw(canvas: Canvas?, projection: Projection?) {
        if (!isEnabled) return
        if (mIcon == null) return
        if (canvas == null) return
        if (projection == null) return

        projection.toPixels(mPosition, mPositionPixels)

        if (drawRangeEnabled) {
            drawRangeCircle(projection, canvas)
        }

        val rotationOnScreen = if (mFlat) -mBearing else -projection.orientation - mBearing
        drawAt(canvas, mPositionPixels.x, mPositionPixels.y, rotationOnScreen)
        if (isInfoWindowShown) {
            //showInfoWindow()
            mInfoWindow.draw()
        }

//        canvas.save()
        // Remove the icon rotation if the maps are rotated so the icon stays upright
//        canvas.rotate(-mapView.mapOrientation, mPositionPixels.x.toFloat(), mPositionPixels.y.toFloat())

        // Draw the bitmap
//        icon?.let {
//            canvas.drawBitmap(
//                it, mPositionPixels.x.toFloat(), mPositionPixels.y.toFloat(), iconPaint
//            )
//        }

//        mCirclePaint.setARGB(0, 100, 100, 255)
//        mCirclePaint.setAntiAlias(true)

//        canvas.restore()

    }


    companion object {
        private const val TAG = "DestinationMarker"

        private const val ALPHA: Float = 0.9F

        @DrawableRes
        private const val ICON_RES: Int = R.drawable.ic_map_pin
    }
}