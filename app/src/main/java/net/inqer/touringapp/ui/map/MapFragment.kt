package net.inqer.touringapp.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import net.inqer.touringapp.R
import net.inqer.touringapp.data.models.Waypoint
import net.inqer.touringapp.databinding.FragmentMapBinding
import net.inqer.touringapp.ui.map.overlays.LocationOverlay
import net.inqer.touringapp.util.DrawableHelpers
import net.inqer.touringapp.util.GeoHelpers.calculatePointBetween
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment : Fragment() {
    private val viewModel: MapViewModel by viewModels()

    @Inject
    @ApplicationContext
    lateinit var appContext: Context

    private lateinit var binding: FragmentMapBinding

    private lateinit var destinationsAdapter: DestinationsMapAdapter

    private val waypointsPolyline: Polyline = Polyline()
    private val targetPolyline: Polyline = Polyline()

    private lateinit var closestPointMarker: Marker
    private lateinit var targetPointMarker: Marker

    private var popupMenu: PopupMenu? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        onViewBindingReady()
        return binding.root
    }

    /**
     *  Called as soon as the view binding has been inflated.
     */
    private fun onViewBindingReady() {
        destinationsAdapter = DestinationsMapAdapter(
            binding.map, layoutInflater, parentFragmentManager
        )

        setupMarkers()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Configuration.getInstance()
            .load(appContext, PreferenceManager.getDefaultSharedPreferences(appContext))

        initMap()

        setupMapEvents()

        setupWaypointsPolyline()

        setDefaultView()

        setupMarkers()

        managePermissions()

        setupLocationOverlay(viewModel.gpsLocationProvider)

        setupButtonClickListeners()

        setupPopupMenu()

        subscribeObservers(viewLifecycleOwner)
    }

    private fun setupPopupMenu() {
        popupMenu = PopupMenu(context, binding.buttonExtrasMenu).apply {
            this.inflate(R.menu.map_extras_menu)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                this.setForceShowIcon(true)
            }

            this.menu.findItem(R.id.menu_shorten_paths)?.isChecked =
                viewModel.appConfig.alwaysShortenPaths

            this.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_shorten_paths -> {
                        Log.d(TAG, "setupPopupMenu: $item")
                        Log.d(TAG, "setupPopupMenu: ${item.isChecked}")
                        item.isChecked = !item.isChecked
                        viewModel.appConfig.alwaysShortenPaths = item.isChecked
                        true
                    }

                    R.id.menu_cancel_route -> {
                        viewModel.cancelTourRoute()
                        true
                    }

                    else -> false
                }
            }
        }

        binding.buttonExtrasMenu.setOnClickListener {
            popupMenu?.show()
        }
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


    private fun setupMapEvents() {
        val eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                destinationsAdapter.closeAllInfoWindows()
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        })
        binding.map.overlayManager.add(eventsOverlay)
    }


    private fun setupButtonClickListeners() {
        val context: Context = binding.root.context
        binding.fabMyLocation.setOnClickListener {
            viewModel.currentLocation.value?.let {
                binding.map.controller.animateTo(
                    GeoPoint(it),
                    LOCATION_ZOOM,
                    LOCATION_ZOOM_SPEED
                )
            }
        }

        binding.fabForward.setOnClickListener {
            viewModel.nextWaypoint(context)
        }
        binding.fabBackward.setOnClickListener {
            viewModel.prevWaypoint(context)
        }
    }


    private fun subscribeObservers(owner: LifecycleOwner) {
        viewModel.activeTourRoute.observe(owner) { route ->
            if (route == null) {
                destinationsAdapter.removeAllMarkers()
                clearWaypointsPolyline()
                binding.activeRouteTitle.text = null
                popupMenu?.let { it.menu.findItem(R.id.menu_cancel_route)?.isVisible = false }
                binding.map.postInvalidate()
                return@observe
            }

            popupMenu?.let { it.menu.findItem(R.id.menu_cancel_route)?.isVisible = true }

            route.waypoints?.let { waypoints ->
                setTourWaypointsLine(waypoints)
            }

            route.destinations?.let { destinations ->
                destinationsAdapter.submitList(destinations.toList())
            }

            binding.activeRouteTitle.text = route.title
        }

        viewModel.routeDataBus.activeDestination.observe(owner) {
            destinationsAdapter.setActiveDestination(it)
        }

        viewModel.currentLocation.observe(owner) {
            updateTargetLine()
            binding.speedValue.text = it?.speed.toString()
        }

        viewModel.routeDataBus.targetWaypoint.observe(owner) { waypoint ->
            waypoint?.let {
                updateTargetLine(it)
                updateTargetPointMarker(it)
            }
        }

        viewModel.routeDataBus.closestWaypointCalculatedPoint.observe(owner) { point ->
            point?.let { updateClosestPointMarker(point.waypoint) }
        }
    }


    private fun setupWaypointsPolyline() {
        waypointsPolyline.apply {
            this.outlinePaint.apply {
                color = Color.RED
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND

            }

            binding.map.overlayManager.add(this)
        }
    }


    private fun updateClosestPointMarker(waypoint: Waypoint) {
        if (!this::closestPointMarker.isInitialized) return

        Log.d(TAG, "updateClosestPoint: settings closest waypoint position - $waypoint")
        closestPointMarker.position = waypoint.asGeoPoint()
    }

    private fun updateTargetPointMarker(waypoint: Waypoint) {
        Log.d(TAG, "updateTargetPoint: setting target waypoint marker position - $waypoint")
        targetPointMarker.position = waypoint.asGeoPoint()
    }


    private fun clearWaypointsPolyline() = waypointsPolyline.setPoints(listOf())


    private fun setTourWaypointsLine(waypoints: Array<Waypoint>) =
        setTourWaypointsLine(waypoints.map { GeoPoint(it.latitude, it.longitude) }.toList())

    private fun setTourWaypointsLine(geoPoints: List<GeoPoint>) {
        waypointsPolyline.setPoints(geoPoints)

        if (geoPoints.isEmpty()) {
            return
        }

        binding.map.controller.animateTo(
            if (geoPoints.size > 1) calculatePointBetween(geoPoints[0], geoPoints[1])
            else geoPoints[0]
        )

        if (binding.map.width > 0) {
            binding.map.zoomToBoundingBox(BoundingBox.fromGeoPointsSafe(geoPoints), true, 100)
        } else {
            Log.w(TAG, "setTourWaypointsLine: mapView is not initialized, failed to zoom.")
        }
    }


    private fun updateTargetLine() {
        viewModel.routeDataBus.targetWaypoint.value?.let {
            updateTargetLine(it)
        }
    }

    private fun updateTargetLine(waypoint: Waypoint) {
        updateTargetLine(waypoint.asGeoPoint())
    }

    private fun updateTargetLine(target: GeoPoint) {
        viewModel.currentLocation.value?.let { location ->
            targetPolyline.setPoints(listOf(GeoPoint(location), target))
            binding.map.postInvalidate() // Force map to redraw
        }
    }


    private fun Waypoint.asGeoPoint(): GeoPoint {
        return GeoPoint(this.latitude, this.longitude)
    }


    private fun setupLocationOverlay(locationProvider: GpsMyLocationProvider?) {
//        val context: Context = binding.root.context

        targetPolyline.apply {
            this.outlinePaint.apply {
                color = ContextCompat.getColor(binding.root.context, R.color.target_line)
                pathEffect = DashPathEffect(floatArrayOf(20f, 15f), 0f)
//                strokeJoin = Paint.Join.ROUND
//                strokeCap = Paint.Cap.ROUND
            }

            binding.map.overlayManager.add(this)
        }

        //My Location
        //note you have handle the permissions yourself, the overlay did not do it for you
        locationProvider?.let {
            LocationOverlay(it, binding.map).apply {
                setOnLocationChangedListener { location, _ ->
                    viewModel.updateLocation(location)
                }

                enableMyLocation()
                binding.map.overlayManager.add(this)
            }
        }
    }


    private fun requestPermissionsIfNecessary(permissions: Array<String>) {
        val permissionsToRequest = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(appContext, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is not granted
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.size > 0) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    permissionsToRequest.toTypedArray(),
                    REQUEST_PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }


    private fun managePermissions() {
        requestPermissionsIfNecessary(
            arrayOf( // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE  // WRITE_EXTERNAL_STORAGE is required in order to show the map
            )
        )

//        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }
//        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//            Log.d(TAG, "onSuccess: location - $location")
//            viewModel.lastLocation = location
//        }
    }


    private fun initMap() {
//        val dm = ctx.resources.displayMetrics
        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        //needed for pinch zooms
        binding.map.setMultiTouchControls(true)

        //scales tiles to the current screen's DPI, helps with readability of labels
//        binding.map.isTilesScaledToDpi = true

        // Zoom buttons visibility
        binding.map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

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


    private fun setupMarkers() {
        val context: Context = binding.root.context

        closestPointMarker = Marker(binding.map).apply {
//                position = destination.geoPoint()
            icon = DrawableHelpers.getThemePaintedDrawable(
                context,
                R.drawable.circle,
                R.attr.colorPrimaryVariant
            )
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            title = "Ближайшая точка"

            setOnMarkerClickListener { _, _ ->
                Log.d(TAG, "onViewBindingReady: closest point marker clicked")
                true
            }

            binding.map.overlayManager.add(this)
        }

        targetPointMarker = Marker(binding.map).apply {
//                position = destination.geoPoint()

            icon = DrawableHelpers.getResPaintedDrawable(
                context,
                R.drawable.circle,
                R.color.target_line
            )
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            title = "Целевая точка"

            setOnMarkerClickListener { _, _ ->
                Log.d(TAG, "onViewBindingReady: closest point marker clicked")
                true
            }

            binding.map.overlayManager.add(this)
        }
    }


    companion object {
        private const val TAG = "MapFragment"
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1

        private const val LOCATION_ZOOM = 16.0
        private const val LOCATION_ZOOM_SPEED = 3000L

        private val POINT_RGUTIS = LabelledGeoPoint(55.4331145, 37.5562910, "RGUTIS")
    }
}