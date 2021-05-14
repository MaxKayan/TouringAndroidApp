package net.inqer.touringapp.ui.map.overlays

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import net.inqer.touringapp.R
import net.inqer.touringapp.data.models.Destination
import net.inqer.touringapp.util.DrawableHelpers
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

    private val circlePaint = Paint().apply {
//        this.setARGB(0, 100, 100, 255)
        this.color = Color.GREEN
        this.isAntiAlias = true
        this.alpha = 50
        this.style = Paint.Style.FILL
    }
    private val circleOutlinePaint = Paint().apply {
//        this.setARGB(0, 100, 100, 255)
        this.color = Color.GREEN
        this.isAntiAlias = true
        this.alpha = 150
        this.style = Paint.Style.STROKE
    }

    var drawRangeEnabled = true
    var status: Destination.Companion.DestinationStatus =
        Destination.Companion.DestinationStatus.UNVISITED
        set(value) {
            if (value == status) return

            when (value) {
                Destination.Companion.DestinationStatus.UNVISITED -> {
                    icon = DrawableHelpers.getResPaintedDrawable(
                        context, ICON_RES, android.R.color.holo_blue_bright
                    )
                }
                Destination.Companion.DestinationStatus.ACTIVE -> {
                    icon = DrawableHelpers.getThemePaintedDrawable(
                        context, ICON_RES, R.attr.colorSecondary
                    )
                }
                Destination.Companion.DestinationStatus.VISITED -> {
                    icon = DrawableHelpers.getResPaintedDrawable(
                        context, ICON_RES, android.R.color.darker_gray
                    )
                }
            }

            field = value
        }

    init {
        title = destination.title
        snippet = destination.description
        ContextCompat.getDrawable(context, ICON_RES)?.let { icon = it }
    }

    override fun drawAt(pCanvas: Canvas?, pX: Int, pY: Int, pOrientation: Float) {
        super.drawAt(pCanvas, pX, pY, pOrientation)
    }

    override fun draw(canvas: Canvas?, projection: Projection?) {
        if (mIcon == null) return
        if (canvas == null) return
        if (projection == null) return
        if (!isEnabled) return

        projection.toPixels(mPosition, mPositionPixels)

        val rotationOnScreen = if (mFlat) -mBearing else -projection.orientation - mBearing
        drawAt(canvas, mPositionPixels.x, mPositionPixels.y, rotationOnScreen)
        if (isInfoWindowShown) {
            //showInfoWindow()
            mInfoWindow.draw()
        }

        if (drawRangeEnabled) {
//            val radius = destination.radius / TileSystem.GroundResolution(
//                destination.latitude,
//                projection.zoomLevel
//            ).toFloat()

            val radius = destination.radius

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
        @DrawableRes
        private const val ICON_RES: Int = R.drawable.ic_location
    }
}