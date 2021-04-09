package net.inqer.touringapp.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import net.inqer.touringapp.R
import net.inqer.touringapp.databinding.FragmentMapBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment : Fragment() {
    private val viewModel: MapViewModel by viewModels()

    @Inject
    @ApplicationContext
    lateinit var appContext: Context

    private var tourPolyLine: Polyline? = null

    private lateinit var binding: FragmentMapBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Configuration.getInstance().load(appContext, PreferenceManager.getDefaultSharedPreferences(appContext))

        initMap(viewModel.gpsLocationProvider)

        setDefaultView()

        setMarkers()

        managePermissions(viewModel.fusedLocationProviderClient)

        subscribeObservers()
    }

    override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        binding.map.onResume() //needed for compass, my location overlays, v6.0.0 and up
    }


    override fun onPause() {
        super.onPause()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        binding.map.onPause() //needed for compass, my location overlays, v6.0.0 and up
    }

    private fun subscribeObservers() {
        viewModel.activeTourRoute.observe(viewLifecycleOwner) { route ->
            if (route == null) {
                clearTourWaypoints()
                return@observe;
            }

            route.waypoints?.let { waypoints ->
                setTourWaypointsLine(
                        waypoints.map { GeoPoint(it.latitude, it.longitude) }.toList()
                )
            }
        }
    }

    private fun managePermissions(fusedLocationClient: FusedLocationProviderClient) {
        requestPermissionsIfNecessary(arrayOf( // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,  // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ))

        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            Log.d(TAG, "onSuccess: location - $location")
            viewModel.lastLocation = location
        }
    }

    private fun initMap(locationProvider: GpsMyLocationProvider) {
        val ctx: Context = appContext
//        val dm = ctx.resources.displayMetrics
        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        //needed for pinch zooms
        binding.map.setMultiTouchControls(true)

        //scales tiles to the current screen's DPI, helps with readability of labels
//        binding.map.isTilesScaledToDpi = true

        //My Location
        //note you have handle the permissions yourself, the overlay did not do it for you
        val locationOverlay = MyLocationNewOverlay(locationProvider, binding.map)
        locationOverlay.enableMyLocation()
        binding.map.overlays.add(locationOverlay)


//        //Mini map
//        val minimapOverlay = MinimapOverlay(ctx, binding.map.tileRequestCompleteHandler)
//        minimapOverlay.width = dm.widthPixels / 5
//        minimapOverlay.height = dm.heightPixels / 5
//        binding.map.overlays.add(minimapOverlay)
//
        // Zoom buttons visibility
        binding.map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

//        //Copyright overlay
//        val copyrightOverlay = CopyrightOverlay(ctx)
//        //i hate this very much, but it seems as if certain versions of android and/or
//        //device types handle screen offsets differently
//        binding.map.overlays.add(copyrightOverlay)


        //On screen compass
        val compassOverlay = CompassOverlay(ctx, InternalCompassOrientationProvider(ctx),
                binding.map)
        compassOverlay.enableCompass()
        binding.map.overlays.add(compassOverlay)


        //support for map rotation
        val rotationGestureOverlay = RotationGestureOverlay(binding.map)
        rotationGestureOverlay.isEnabled = true
        binding.map.overlays.add(rotationGestureOverlay)
    }


    private fun setDefaultView() {
        val mapController = binding.map.controller
        mapController.setZoom(14.5)
        mapController.setCenter(POINT_RGUTIS)
    }


    private fun setTourWaypointsLine(geoPoints: List<GeoPoint>) {
        clearTourWaypoints()

        tourPolyLine = Polyline().apply {
            this.setPoints(geoPoints)

            this.outlinePaint.apply {
                color = Color.RED
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
            }

            this.setOnClickListener { polyline, mapView, eventPos ->
                Toast.makeText(context, "polyline with " + polyline.actualPoints.size + "pts was tapped", Toast.LENGTH_LONG).show()
                return@setOnClickListener false
            }

            binding.map.overlayManager.add(this)
        }
    }


    private fun clearTourWaypoints() {
        tourPolyLine?.let {
            binding.map.overlayManager.remove(it)
            tourPolyLine = null
        }
    }


    private fun setMarkers() {
        val rgutMarker = Marker(binding.map)
        rgutMarker.position = POINT_RGUTIS
        rgutMarker.icon = ContextCompat.getDrawable(appContext, R.drawable.marker_icon)
        rgutMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        rgutMarker.title = POINT_RGUTIS.label
        rgutMarker.setOnMarkerClickListener { marker, mapView ->
            Toast.makeText(context, String.format(Locale.US, "Marker %s was tapped! %f %f",
                    marker.title, marker.position.longitude, marker.position.longitude),
                    Toast.LENGTH_SHORT).show()
            false
        }
        binding.map.overlays.add(rgutMarker)
    }


    private fun requestPermissionsIfNecessary(permissions: Array<String>) {
        val permissionsToRequest = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(appContext, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.size > 0) {
            activity?.let {
                ActivityCompat.requestPermissions(
                        it,
                        permissionsToRequest.toTypedArray(),
                        REQUEST_PERMISSIONS_REQUEST_CODE)
            }
        }
    }

    companion object {
        private const val TAG = "MapFragment"
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1
        private val POINT_RGUTIS = LabelledGeoPoint(55.4331145, 37.5562910, "RGUTIS")
    }
}