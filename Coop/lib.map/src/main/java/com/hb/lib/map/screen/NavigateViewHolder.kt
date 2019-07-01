package com.hb.lib.map.screen

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageSwitcher
import android.widget.TextSwitcher
import butterknife.BindView
import butterknife.OnClick
import com.hb.lib.map.BuildConfig
import com.hb.lib.map.R
import com.hb.lib.map.R2
import com.hb.lib.map.model.FindShortestPath
import com.hb.lib.map.navigation.camera.Camera
import com.hb.lib.map.navigation.camera.RouteInformation
import com.hb.lib.map.navigation.ui.camera.DynamicCamera
import com.hb.lib.map.route.NavigateManager
import com.hb.lib.map.route.SphericalUtil
import com.hb.lib.utils.Utils
import com.hb.lib.utils.ui.ThemeUtils
import com.hb.uiwidget.recyclerview.BaseViewHolder
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.mapboxsdk.annotations.Polyline
import com.mapbox.mapboxsdk.annotations.PolylineOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdate
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode

abstract class NavigateViewHolder(
    itemView: View,
    private val mapFragment: HBMapFragment<*>
) : BaseViewHolder<FindShortestPath>(itemView), LocationEngineListener {

    private lateinit var mMap: MapboxMap
    private var mLocationLayerPlugin: LocationLayerPlugin? = null
    private lateinit var mCameraEngine: Camera


    private val animH = intArrayOf(R.anim.slide_in_right, R.anim.slide_out_left)
    private val animV = intArrayOf(R.anim.slide_in_top, R.anim.slide_out_bottom)

    @BindView(R2.id.text_switcher_navigate_script)
    lateinit var scriptSwitcher: TextSwitcher
    @BindView(R2.id.text_switcher_navigate_length)
    lateinit var lengthSwitcher: TextSwitcher
    @BindView(R2.id.image_switcher_navigate_arrow)
    lateinit var arrowSwitcher: ImageSwitcher

    private var isInitSwitcher = false

    private lateinit var mResult: FindShortestPath
    private var steps: Array<FindShortestPath.Step>? = null

    private lateinit var mManager: NavigateManager

    private var mCurrent = 0

    private fun initSwitcher() {
        if (isInitSwitcher)
            return
        isInitSwitcher = true

        val context = itemView.context!!
        val inflater = LayoutInflater.from(context)


        scriptSwitcher.setFactory {
            val view = inflater.inflate(R.layout.view_naviagte_script, null, false)
            view
        }
        lengthSwitcher.setFactory {
            val view = inflater.inflate(R.layout.view_naviagte_length, null, false)
            view
        }
        arrowSwitcher.setFactory {
            val view = inflater.inflate(R.layout.view_naviagte_arrow, null, false)
            view
        }

        scriptSwitcher.setInAnimation(context, animH[0])
        scriptSwitcher.setOutAnimation(context, animH[1])

        lengthSwitcher.setInAnimation(context, animV[0])
        lengthSwitcher.setOutAnimation(context, animV[1])

        arrowSwitcher.setInAnimation(context, animH[0])
        arrowSwitcher.setOutAnimation(context, animH[1])


        mMap = mapFragment.mapView()!!
        mCameraEngine = DynamicCamera(mMap)
        mLocationLayerPlugin = mapFragment.getLocationLayerPlugin()

        val radius = getRadius()

        mManager = NavigateManager(
            locationLayerPlugin = mLocationLayerPlugin,
            radius = radius
        )

        if (BuildConfig.DEBUG) {
            mMap.addOnMapClickListener {
                val location = Location("Mock - Test")
                location.apply {
                    longitude = it.longitude
                    latitude = it.latitude
                }

                onLocationChanged(location)
            }
        }
    }

    open fun getRadius(): Double {
        return 50.0
    }

    @SuppressLint("MissingPermission")
    override fun bindData(data: FindShortestPath) {
        initSwitcher()

        mResult = data
        if (!mResult.hasValue())
            return

        mManager.parseWayPoints(data)

        val rs = data.resultScript!!
        steps = rs.legs!![0].steps

        if (steps != null) {
            bindScript(steps!![mCurrent])
            bindArrow(steps!![mCurrent])
            bindLength(steps!![mCurrent])
        }

        if (isTracking) {
            mapFragment.hideStartMarker()
            if (mLocationLayerPlugin != null) {
                onLocationChanged(mLocationLayerPlugin!!.lastKnownLocation)
            }
        }

        if (BuildConfig.DEBUG) {
            val wp = mManager.getCurrentWayPoint()
            if (wp != null) {
                if (polylineTest != null) {
                    mMap.removePolyline(polylineTest!!)
                }
                val test = mMap.addPolyline(
                    PolylineOptions().width(8.0f)
                        .color(Color.GRAY)
                        .addAll(wp.path)
                )
                polylineTest = test
            }
        }
    }

    private var polylineTest: Polyline? = null

    private fun bindScript(step: FindShortestPath.Step) {
        val context = itemView.context
        val nameDir = String.format("route_guide_%02d", step.dir!!.toInt())
        val guideResId = Utils.getResId(context = context, name = nameDir, type = "string")
        val script = context.getString(guideResId) + " " + step.name
        scriptSwitcher.setText(script)
    }

    private fun bindArrow(step: FindShortestPath.Step) {
        val context = itemView.context
        val nameArrow = String.format("da_turn_%02d", step.dir!!.toInt())
        val arrowResId = Utils.getResId(
            context = context,
            name = nameArrow,
            type = "drawable"
        )
        arrowSwitcher.setImageResource(arrowResId)
    }

    private fun bindLength(step: FindShortestPath.Step) {
        lengthSwitcher.setText("${step.len} m")
    }

    private fun bindLength(len: Double) {
        val text = Utils.formatDistance(len)
        lengthSwitcher.setText(text)
    }

    @OnClick(R2.id.button_navigate_prev)
    fun onPrev() {
        if (!mResult.hasValue() && steps != null)
            return

        if (mCurrent == 0)
            return
        mCurrent--

        bindScript(steps!![mCurrent])
        bindArrow(steps!![mCurrent])
        bindLength(steps!![mCurrent])

    }

    @OnClick(R2.id.button_navigate_next)
    fun onNext() {
        if (!mResult.hasValue() && steps != null)
            return

        if (mCurrent == steps!!.size - 1)
            return
        mCurrent++

        bindScript(steps!![mCurrent])
        bindArrow(steps!![mCurrent])
        bindLength(steps!![mCurrent])

    }

    private var mOldStep: FindShortestPath.Step? = null

    override fun onConnected() {
    }

    private var mOldLocation: Location? = null

    abstract fun findRoute()

    @SuppressLint("CheckResult")
    override fun onLocationChanged(location: Location?) {
        if (location == null || !isTracking)
            return
        var wp = mManager.getWayPoint(location = location, updateMyLocation = false)
        if (wp != null) {
            mOldLocation = location
        } else {
            if (mOldLocation != null) {
                val distance = SphericalUtil.computeDistanceBetween(
                    LatLng(
                        mOldLocation!!.latitude,
                        mOldLocation!!.longitude
                    ), LatLng(
                        location.latitude,
                        location.longitude
                    )
                )
                if (distance <= mManager.getRadius()) {
                    wp = mManager.getWayPoint(location = mOldLocation, updateMyLocation = false)
                } else {
                    mOldLocation = null
                    if (mLocationLayerPlugin != null) {
                        mLocationLayerPlugin!!.forceLocationUpdate(location)
                    }
                    findRoute()

//                    val rxBus = App.getInstance<App>().appComponent.bus()
//                    rxBus.send(
//                        ReFindShortestPathEvent(
//                            startPoint = GeoPoint(location.latitude, location.longitude),
//                            endPoint = GeoPoint.fromLatLng(mResult.endPoint!!)
//                        )
//                    )
                    return
                }
            }
        }

        if (wp != null) {
            val step = wp.step
            if (mOldStep != step) {
                bindScript(step)
                bindArrow(step)
                mOldStep = step
            }
            bindLength(wp.distances[0])

            if (mLocationLayerPlugin != null) {
                mLocationLayerPlugin!!.forceLocationUpdate(location)
            }
            currentRouteInformation = buildRouteInformationFromLocation(location, mManager)
            animateCameraFromLocation(currentRouteInformation!!)
        } else {
            if (BuildConfig.DEBUG && mLocationLayerPlugin != null) {
                mLocationLayerPlugin!!.forceLocationUpdate(location)
            }


        }
    }

    private var isTracking = false

    @SuppressLint("MissingPermission", "WakelockTimeout", "InvalidWakeLockTag")
    fun enableTracking(enabled: Boolean) {

        val llp = mLocationLayerPlugin ?: return

        isTracking = enabled
        if (enabled) {
            llp.renderMode = RenderMode.GPS
            llp.cameraMode = CameraMode.TRACKING_GPS

            llp.locationEngine!!.addLocationEngineListener(this)

            val context = mapFragment.context
            val paddingTop = ThemeUtils.dpToPx(context, 200)
            val padding = ThemeUtils.dpToPx(context, 56)

            mMap.setPadding(padding, paddingTop, padding, padding)

            onLocationChanged(llp.lastKnownLocation)

            start()

            val uiSettings = mMap.uiSettings
            uiSettings.isRotateGesturesEnabled = true
            uiSettings.isTiltGesturesEnabled = true

            mapFragment.hideStartMarker()

            itemView.visibility = View.VISIBLE

            mapFragment.changeNavigateStyleByTime()
        } else {
            llp.renderMode = RenderMode.COMPASS
            llp.cameraMode = CameraMode.NONE

            llp.locationEngine!!.removeLocationEngineListener(this)

            animationRestCamera()

            val padding = ThemeUtils.dpToPx(itemView.context, 56)
            mMap.setPadding(padding, padding, padding, padding)

            if (BuildConfig.DEBUG) {
                if (polylineTest != null) {
                    mMap.removePolyline(polylineTest!!)
                }
            }

            val uiSettings = mMap.uiSettings
            uiSettings.isRotateGesturesEnabled = false
            uiSettings.isTiltGesturesEnabled = false

            mOldLocation = null
            mapFragment.showStartMarker()

            itemView.visibility = View.GONE

            mapFragment.changeMapStyleDefault()
        }
    }


    @SuppressLint("MissingPermission")
    fun start() {
        if (mLocationLayerPlugin == null)
            return

        val location = mLocationLayerPlugin!!.lastKnownLocation
        currentRouteInformation = buildRouteInformationFromLocation(location, mManager)
        animateCameraFromLocation(currentRouteInformation!!, true)
    }

    private fun buildRouteInformationFromLocation(location: Location?, progress: NavigateManager): RouteInformation {
        return RouteInformation(location = location, routeProgress = progress)
    }

    private var currentRouteInformation: RouteInformation? = null

    private fun getCameraEngine(): Camera {
        return mCameraEngine
    }


    private fun animateCameraFromLocation(routeInformation: RouteInformation, isStart: Boolean = false) {

        val cameraEngine = getCameraEngine()

        val targetPoint = cameraEngine.target(routeInformation)
        val target = LatLng(targetPoint.latitude(), targetPoint.longitude())
        val bearing = cameraEngine.bearing(routeInformation)
        val tilt = cameraEngine.tilt(routeInformation)
        val zoom = if (isStart) 18.0 else cameraEngine.zoom(routeInformation)

        val position = CameraPosition.Builder()
            .target(target)
            .bearing(bearing)
            .tilt(tilt)
            .zoom(zoom)
            .build()

        easeMapCameraPosition(position)
    }

    private var locationUpdateTimestamp: Long = 0

    private fun animationRestCamera() {
        val resetUpdate = buildResetCameraUpdate()
        mMap.animateCamera(resetUpdate, 150)
    }

    private fun buildResetCameraUpdate(): CameraUpdate {
        val resetPosition = CameraPosition.Builder().tilt(0.0).bearing(0.0).build()
        return CameraUpdateFactory.newCameraPosition(resetPosition)
    }

    private fun updateMapCameraPosition(position: CameraPosition, callback: MapboxMap.CancelableCallback?) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000, callback)
    }

    private fun easeMapCameraPosition(position: CameraPosition) {
        mMap.easeCamera(
            CameraUpdateFactory.newCameraPosition(position),
            obtainLocationUpdateDuration(), false, null
        )
    }

    private val MAX_ANIMATION_DURATION_MS: Long = 1500

    private fun obtainLocationUpdateDuration(): Int {
        val previousUpdateTimeStamp = locationUpdateTimestamp
        locationUpdateTimestamp = SystemClock.elapsedRealtime()
        val duration = locationUpdateTimestamp - previousUpdateTimeStamp
        return (if (duration < MAX_ANIMATION_DURATION_MS) duration else MAX_ANIMATION_DURATION_MS).toInt()
    }

}