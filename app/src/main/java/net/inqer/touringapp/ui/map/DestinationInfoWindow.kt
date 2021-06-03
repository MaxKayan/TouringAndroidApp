package net.inqer.touringapp.ui.map

import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import net.inqer.touringapp.data.models.Destination
import net.inqer.touringapp.databinding.DestinationInfoWindowBinding
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow

class DestinationInfoWindow(
    private val binding: DestinationInfoWindowBinding,
    mapView: MapView,
    private val destination: Destination,
    private val onDetailsClick: View.OnClickListener,
    private val onOpenCallback: () -> Unit,
    private val onCloseCallback: () -> Unit
) : InfoWindow(binding.root, mapView) {
//    constructor(layoutResId: Int, mapView: MapView?) : super(layoutResId, mapView)
//    constructor(v: View?, mapView: MapView?) : super(v, mapView)
//    constructor(binding: DestinationInfoWindowBinding, mapView: MapView?) : super()

    private val context = binding.root.context

    private val openAnimation =
        AnimationUtils.loadAnimation(context, android.R.anim.fade_in).apply {
            this.duration = 200L
        }
    private val closeAnimation =
        AnimationUtils.loadAnimation(context, android.R.anim.fade_out).apply {
            this.duration = 200L
        }

    override fun onOpen(item: Any?) {
        binding.root.visibility = View.INVISIBLE
        binding.title.text = destination.title
        binding.description.text = destination.shortDescription

        binding.root.startAnimation(openAnimation)
        binding.root.visibility = View.VISIBLE

        binding.root.setOnClickListener {
        }

        binding.btnDetails.setOnClickListener(onDetailsClick)

        onOpenCallback()
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
        onCloseCallback()
//        binding.root.visibility = View.VISIBLE
//        binding.root.startAnimation(closeAnimation)
    }

    companion object {
        private const val TAG = "DestinationInfoWindow"
    }
}