package net.inqer.touringapp.ui.map

import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import net.inqer.touringapp.data.models.Destination
import net.inqer.touringapp.databinding.DestinationInfoWindowBinding
import net.inqer.touringapp.ui.map.overlays.CirclePlottingOverlay
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow

class DestinationInfoWindow(
    private val binding: DestinationInfoWindowBinding,
    mapView: MapView,
    private val destination: Destination,
    private val onOpenClick: View.OnClickListener
) : InfoWindow(binding.root, mapView) {
//    constructor(layoutResId: Int, mapView: MapView?) : super(layoutResId, mapView)
//    constructor(v: View?, mapView: MapView?) : super(v, mapView)
//    constructor(binding: DestinationInfoWindowBinding, mapView: MapView?) : super()

    private val context = binding.root.context

    private val openAnimation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
    private val closeAnimation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)

    private var rangeCircle: CirclePlottingOverlay? = null

    override fun onOpen(item: Any?) {
        binding.root.visibility = View.INVISIBLE
        binding.title.text = destination.title
        binding.description.text = destination.description
        Log.d(TAG, "onOpen: $this ; $item")

        binding.root.startAnimation(openAnimation)
        binding.root.visibility = View.VISIBLE

        binding.root.setOnClickListener {
        }

        binding.btnDetails.setOnClickListener(onOpenClick)
    }

    override fun close() {
        closeAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                invokeClose()
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }
        })
        binding.root.startAnimation(closeAnimation)
    }

    private fun invokeClose() {
        super.close()
    }

    override fun onClose() {
//        binding.root.visibility = View.VISIBLE
//        binding.root.startAnimation(closeAnimation)
    }

    companion object {
        private const val TAG = "DestinationInfoWindow"
    }
}