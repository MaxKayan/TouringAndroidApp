package net.inqer.touringapp.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
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
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import net.inqer.touringapp.databinding.FragmentMapBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.MinimapOverlay
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

    private lateinit var binding: FragmentMapBinding

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1

    private val POINT_RGUTIS = LabelledGeoPoint(55.4331145, 37.5562910, "RGUTIS")
    var locationProvider: GpsMyLocationProvider? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
//        val ctx: Context = getApplicationContext()
        Configuration.getInstance().load(appContext, PreferenceManager.getDefaultSharedPreferences(appContext))
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        //inflate and create the map
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        //inflate and create the map

        locationProvider = GpsMyLocationProvider(appContext)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)

        initMap(locationProvider!!)

        setDefaultView()

//        setPolylines();
        setMarkers()

        requestPermissionsIfNecessary(arrayOf( // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,  // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ))

        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient!!.lastLocation.addOnSuccessListener { location ->
            Log.d(TAG, "onSuccess: location - $location")
            lastLocation = location
            setPolylines()
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

    private fun initMap(locationProvider: GpsMyLocationProvider) {
        val ctx: Context = appContext
        val dm = ctx.resources.displayMetrics
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
//        // Zoom buttons visibility
//        binding.map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

        //Copyright overlay
        val copyrightOverlay = CopyrightOverlay(ctx)
        //i hate this very much, but it seems as if certain versions of android and/or
        //device types handle screen offsets differently
        binding.map.overlays.add(copyrightOverlay)


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


    private fun setPolylines() {
        val geoPoints: MutableList<GeoPoint> = ArrayList()
        val startPoint: GeoPoint = POINT_RGUTIS
        val increment = 0.01
        geoPoints.add(startPoint)
        geoPoints.add(GeoPoint(startPoint.latitude + increment, startPoint.longitude))
        geoPoints.add(GeoPoint(startPoint.latitude + increment, startPoint.longitude + increment))
        geoPoints.add(GeoPoint(startPoint.latitude, startPoint.longitude + increment))
        geoPoints.add(startPoint)
        if (lastLocation != null) {
            geoPoints.add(GeoPoint(lastLocation))
        }
        val line = Polyline() //see note below!
        line.setPoints(geoPoints)
        line.setOnClickListener { polyline, mapView, eventPos ->
            Toast.makeText(context, "polyline with " + polyline.actualPoints.size + "pts was tapped", Toast.LENGTH_LONG).show()
            false
        }
        binding.map.overlayManager.add(line)
    }


    private fun setMarkers() {
        val rgutMarker = Marker(binding.map)
        rgutMarker.position = POINT_RGUTIS
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


//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        val permissionsToRequest: MutableList<String> = ArrayList()
//        permissionsToRequest.addAll(listOf(*permissions).subList(0, grantResults.size))
//        if (permissionsToRequest.size > 0) {
//            activity?.let {
//                ActivityCompat.requestPermissions(
//                        it,
//                        permissionsToRequest.toTypedArray(),
//                        REQUEST_PERMISSIONS_REQUEST_CODE)
//            }
//        }
//    }


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
    }
}