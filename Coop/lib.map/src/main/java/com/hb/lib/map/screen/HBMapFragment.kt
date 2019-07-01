package com.hb.lib.map.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Matrix
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.OnClick
import com.google.android.gms.location.*
import com.hb.lib.map.R
import com.hb.lib.map.R2
import com.hb.lib.map.animtor.RoutePathLayer
import com.hb.lib.map.model.GeoPoint
import com.hb.lib.map.model.POI
import com.hb.lib.mvp.base.MvpContract
import com.hb.lib.mvp.impl.HBMvpFragment
import com.hb.lib.utils.ui.ScreenUtils
import com.hb.lib.utils.ui.ThemeUtils
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import timber.log.Timber
import java.util.*

abstract class HBMapFragment<P : MvpContract.Presenter> : HBMvpFragment<P>(), OnMapReadyCallback, LocationListener {

    override fun getResLayoutId(): Int = R.layout.fragment_map

    @BindView(R2.id.button_my_location)
    lateinit var mLocationButton: View

    @BindView(R2.id.mapview)
    lateinit var mMapView: MapView

    private lateinit var mMap: MapboxMap
    private var mLocationLayerPlugin: LocationLayerPlugin? = null
    private var mLocationEngine: LocationEngine? = null

    private var mStartMarker: Marker? = null
    private var mEndMarker: Marker? = null
    private var mStartIcon: Icon? = null
    private var mEndIcon: Icon? = null

    private var mHandler = Handler()

    private var mMoveMarker: Marker? = null
    private var mMoveIcon: Icon? = null


     abstract fun getToken(): String

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        mMapView = view.findViewById(R.id.mapview)
//        mLocationButton = view.findViewById(R.id.button_my_location)

        mMapView.onCreate(savedInstanceState)

        initAllMarkers()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        val styleUrl = getMapStyleUrlFormat().replace("{{STYLE}}", "vt_vbddefault")
        Timber.d("MapStyle: $styleUrl")
        mMapView.setStyleUrl(styleUrl)
        mMapView.getMapAsync(this)

        mMapView.addOnMapChangedListener { change ->
            if (change == MapView.DID_FINISH_LOADING_STYLE) {
                showStartMarker()
                showEndMarker()
                updateRouteOnMap(mPath)
            }
        }
    }


    private fun getMapStyleUrlFormat(): String = "https://images.vietbando.com/Style/{{STYLE}}/${getToken()}"

    private fun initAllMarkers() {
        if (mStartIcon == null) {
            mStartIcon = IconFactory.getInstance(context!!).fromResource(R.drawable.marker_start)
        }
        if (mEndIcon == null) {
            mEndIcon = IconFactory.getInstance(context!!).fromResource(R.drawable.marker_end)
        }

    }

    @SuppressLint("MissingPermission", "WrongConstant")
    override fun onMapReady(mapboxMap: MapboxMap) {
        mMap = mapboxMap
        val uiSettings = mMap.uiSettings

        uiSettings.isLogoEnabled = false
        uiSettings.isAttributionEnabled = false

        uiSettings.isRotateGesturesEnabled = false
        uiSettings.isTiltGesturesEnabled = false

        mLocationLayerPlugin = LocationLayerPlugin(mMapView, mMap)
        mLocationLayerPlugin!!.apply {
            renderMode = RenderMode.COMPASS
        }

        lifecycle.addObserver(mLocationLayerPlugin!!)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location == null)
                return@addOnSuccessListener

            val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(location.latitude, location.longitude))
                    .zoom(17.0)
                    .build()
            mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            mLocationLayerPlugin!!.forceLocationUpdate(location)

            if (onMapReadyListener != null) {
                onMapReadyListener!!.onMapReady()
            }
        }


    }

    override fun onLocationChanged(location: Location) {
        mLocationLayerPlugin!!.forceLocationUpdate(location)
    }

    private var onMapReadyListener: OnMapReadyListener? = null

    fun setOnMapReadyListener(listener: OnMapReadyListener?) {
        onMapReadyListener = listener
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMapView.onSaveInstanceState(outState)
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        Timber.d("onStart")
        mMapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
        mMapView.onResume()

    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
//        stopLocationUpdates()
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop")
        mMapView.onStop()

    }

    override fun onLowMemory() {
        super.onLowMemory()
        Timber.d("onLowMemory")
        mMapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mMapView.onDestroy()
    }

    fun getLocationLayerPlugin(): LocationLayerPlugin {
        return mLocationLayerPlugin!!
    }

    @SuppressLint("MissingPermission")
    fun location(): Location? {
        if (mLocationLayerPlugin == null)
            return null
        return mLocationLayerPlugin!!.lastKnownLocation
    }

    fun locationGeoPoint(): GeoPoint? {
        val location = location() ?: return null
        return GeoPoint(longitude = location.longitude, latitude = location.latitude)
    }


    fun mapView(): MapboxMap? = mMap

    @SuppressLint("MissingPermission")
    @OnClick(R2.id.button_my_location)
    open fun onLocation() {
        if (PermissionsManager.areRuntimePermissionsRequired() && !PermissionsManager.areLocationPermissionsGranted(context)) {
            val manager = PermissionsManager(object : PermissionsListener {
                override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
                }

                override fun onPermissionResult(granted: Boolean) {
                }
            })
            manager.requestLocationPermissions(activity!!)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location == null)
                return@addOnSuccessListener

            val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(location.latitude, location.longitude))
                    .zoom(17.0)
                    .build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            mLocationLayerPlugin!!.forceLocationUpdate(location)
        }
    }

    fun hideStartMarker() {
        val map = mapView() ?: return

        if (mStartMarker != null) {
            map.removeMarker(mStartMarker!!)
            mStartMarker = null
        }
    }

    fun hideEndMaker() {
        val map = mapView() ?: return

        if (mEndMarker != null) {
            map.removeMarker(mEndMarker!!)
            mEndMarker = null
        }
    }

    fun showStartMarker() {
        if (mStartPoint == null)
            return
        val map = mapView() ?: return

        if (mStartMarker != null) {
            map.removeMarker(mStartMarker!!)
        }
        mStartMarker = map.addMarker(
            MarkerOptions()
                .icon(mStartIcon)
                .position(mStartPoint)
        )
    }

    fun showEndMarker() {
        if (mEndPoint == null)
            return
        val map = mapView() ?: return

        if (mEndMarker != null) {
            map.removeMarker(mEndMarker!!)
        }
        mEndMarker = map.addMarker(MarkerOptions()
                .icon(mEndIcon)
                .position(mEndPoint)
        )
    }

    fun clearStartEndMarker() {
        val map = mapView() ?: return

        if (mStartMarker != null) {
            map.removeMarker(mStartMarker!!)
            mStartMarker = null
        }

        if (mEndMarker != null) {
            map.removeMarker(mEndMarker!!)
            mEndMarker = null
        }

        mStartPoint = null
        mEndPoint = null
    }

    fun clearRouteOverlay() {
        clearMoveMarker()
        clearStartEndMarker()

        RoutePathLayer.getInstance().clear(mapView())
        mPath = null
    }

    private var mStartPoint: LatLng? = null
    private var mEndPoint: LatLng? = null
    private var mPath: List<LatLng>? = null

    fun updateStartEndMarker(startPoint: LatLng?, endPoint: LatLng?) {
        if (startPoint == null || endPoint == null)
            return

        mStartPoint = startPoint
        mEndPoint = endPoint

        val mbr = LatLngBounds.Builder()
                .include(startPoint)
                .include(endPoint)
                .build()

        val map = mapView()!!

        if (mStartMarker != null) {
            map.removeMarker(mStartMarker!!)
        }
        mStartMarker = map.addMarker(MarkerOptions()
                .icon(mStartIcon)
                .position(startPoint)
        )
        if (mEndMarker != null) {
            map.removeMarker(mEndMarker!!)
        }
        mEndMarker = map.addMarker(MarkerOptions()
                .icon(mEndIcon)
                .position(endPoint)
        )

        val padding = ThemeUtils.dpToPx(activity!!, 64)
        map.animateCamera(
                CameraUpdateFactory.newLatLngBounds(mbr,
                        padding,
                        padding,
                        padding,
                        ScreenUtils.getScreenHeight(activity!!) / 2
                ),
                500
        )
    }


    fun updateStartEndMarker(startPoint: POI?, endPoint: POI?) {
        updateStartEndMarker(startPoint?.getLatLng(), endPoint?.getLatLng())
    }

    fun updateRouteOnMap(path: List<LatLng>?) {
        if (activity == null || path == null)
            return

        mPath = path

        Timber.d("updateRouteOnMap")
        val mbr = LatLngBounds.Builder()
                .includes(path)
                .build()
        val padding = ThemeUtils.dpToPx(activity!!, 64)
        mapView()!!.animateCamera(
                CameraUpdateFactory.newLatLngBounds(mbr,
                        padding,
                        padding,
                        padding,
                        ScreenUtils.getScreenHeight(activity!!) / 2
                ),
                500
        )

        RoutePathLayer.getInstance()
                .width(4)
                .colorBackground(ContextCompat.getColor(activity!!, R.color.path_color))
                .colorForeground(ContextCompat.getColor(activity!!, R.color.path_color))
                .draw(mapView(), path)
    }


    fun enableButtonLocation(enabled: Boolean) {
        mLocationButton.visibility = if (enabled) View.VISIBLE else View.GONE
    }

    fun clearMoveMarker() {
        val map = mapView() ?: return

        if (mMoveMarker != null) {
            map.removeMarker(mMoveMarker!!)
            mMoveMarker = null
        }

        if (mMoveIcon != null) {
            mMoveIcon!!.bitmap.recycle()
            mMoveIcon = null
        }
    }

    fun changeMapStyleDefault() {
        val styleUrl = getMapStyleUrlFormat().replace("{{STYLE}}", "vt_vbddefault")
        mapView()!!.setStyleUrl(styleUrl)

//        if (mPath != null) {
//            RoutePathLayer.getInstance()
//                    .width(4)
//                    .colorBackground(ContextCompat.getColor(activity!!, R.color.path_color))
//                    .colorForeground(ContextCompat.getColor(activity!!, R.color.path_color))
//                    .draw(mapView(), mPath!!)
//        }
    }

    fun changeNavigateStyleByTime() {
        val c = Calendar.getInstance()
        val timeOfDay = c.get(Calendar.HOUR_OF_DAY)
        val style = if (timeOfDay in 6..18) {
            "vt_vbdlight"
        } else {
            "vt_vbddark"
        }

        val styleUrl = getMapStyleUrlFormat().replace("{{STYLE}}", style)
        mapView()!!.setStyleUrl(styleUrl)

    }

    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

}