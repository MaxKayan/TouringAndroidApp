package net.inqer.touringapp.ui.map

import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import net.inqer.touringapp.data.models.Destination
import net.inqer.touringapp.databinding.DestinationInfoWindowBinding
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow

class DestinationInfoWindow(
        private val binding: DestinationInfoWindowBinding,
        mapView: MapView,
        private val destination: Destination
) : InfoWindow(binding.root, mapView) {
//    constructor(layoutResId: Int, mapView: MapView?) : super(layoutResId, mapView)
//    constructor(v: View?, mapView: MapView?) : super(v, mapView)
//    constructor(binding: DestinationInfoWindowBinding, mapView: MapView?) : super()

    private val context = binding.root.context

    private val openAnimation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
    private val closeAnimation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)

    override fun onOpen(item: Any?) {
        binding.root.visibility = View.INVISIBLE
        binding.title.text = destination.title
        binding.description.text = destination.description
        Log.d(TAG, "onOpen: $this ; $item")

        binding.root.startAnimation(openAnimation)
        binding.root.visibility = View.VISIBLE

        binding.root.setOnClickListener {
            Log.d(TAG, "onOpen: click: $it")
        }

        binding.btnDetails.setOnClickListener {
            Toast.makeText(context, "$destination ; ${destination.type}", Toast.LENGTH_SHORT).show()
        }
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