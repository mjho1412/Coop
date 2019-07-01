package com.hb.lib.map.route

import android.annotation.SuppressLint
import android.location.Location
import com.hb.lib.map.model.FindShortestPath
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin


class NavigateManager(
        private val locationLayerPlugin: LocationLayerPlugin?,
        private val radius: Double = 50.0
) {

    companion object {
        private const val DISTANCE_TO_PASS_WAY_POINT = 10.0
    }

    private var mInitial = true
    var index: Int = 0
    var wayPoints = ArrayList<WayPoint>()
    var mCurrentWayPoint: WayPoint? = null
    var mSnappedLocation: Location? = null
    var mSegmentIndex: Int = 0
    var mPathLength: Double = 0.0

    fun getRadius(): Double = radius


    fun getWayPoint(i: Int): WayPoint? {
        if (wayPoints.isEmpty()) {
            return null
        }
        if (i >= wayPoints.size) {
            return null
        }
        return wayPoints[i]
    }


    fun getCurrentWayPoint(): WayPoint? = getWayPoint(index)

    @SuppressLint("MissingPermission")
    fun getWayPoint(location: Location?, updateMyLocation: Boolean = true): WayPoint? {
        if (location == null)
            return null

        if (wayPoints.isEmpty())
            return null

        var tolorence: Double = location.accuracy + 10.0
        if (tolorence > radius) {
            tolorence = radius
        }
        val size = wayPoints.size
        val latLng = LatLng(location.latitude, location.longitude)
        if (index < size) {
            val wp = wayPoints[index]
            val idx = PolyUtil.snapIndex(latLng, wp.path, tolorence)
            if (idx >= 0) {
                snapLocation(location, wp, idx, updateMyLocation)
                val latLng = LatLng(location.latitude, location.longitude)
                val distance = SphericalUtil.computeLength(wp.path,
                        latLng,
                        idx)
                if (distance < DISTANCE_TO_PASS_WAY_POINT && index != size - 1 && !mInitial) {
                    mInitial = false
                } else {
                    wp.putDistance(distance)
                    mInitial = false
                    return wp
                }
            }
        } else {
            index = 0
            mInitial = false
        }

        var loop = size - index
        if (loop < index) {
            loop = index
        }

        var i = 1
        while (i < loop) {
//        for (i in 1..(loop - 1)) {
            val f = index + i
            val b = index - i
            if (f < size) {
                val wp = wayPoints[f]
                val idx = PolyUtil.snapIndex(latLng, wp.path, tolorence)
                if (idx >= 0) {
                    snapLocation(location, wp, idx, updateMyLocation)
                    val distance = SphericalUtil.computeLength(wp.path, latLng, idx)
                    wp.putDistance(distance)
                    index = f
                    return wp
                }
            }
            if (b >= 0) {
                val wp = wayPoints[b]
                val idx = PolyUtil.snapIndex(latLng, wp.path, tolorence)
                if (idx >= 0) {
                    snapLocation(location, wp, idx, updateMyLocation)
                    val distance = SphericalUtil.computeLength(wp.path, latLng, idx)
                    wp.putDistance(distance)
                    index = b
                    return wp
                }
            }
            i++
        }

        mCurrentWayPoint = null
        return null
    }

    fun getRemainDistance(wp: WayPoint): Double {
        if (wayPoints.isEmpty())
            return 0.0

        val idx = wayPoints.indexOf(wp)
        var len = wp.distances[0]
        val n = wayPoints.size
        for (i in (idx + 1)..(n - 1)) {
            len += wayPoints[i].len
        }
        return len
    }

    private fun snapLocation(location: Location, wp: WayPoint, idx: Int, updateMyLocation: Boolean) {
        mCurrentWayPoint = wp
        mSegmentIndex = idx

        val ll = SphericalUtil.closestPoint(
                LatLng(location.latitude, location.longitude),
                wp.path[idx],
                wp.path[idx + 1]
        )
        location.latitude = ll.latitude
        location.longitude = ll.longitude

        wp.bearing = SphericalUtil.getBearing(wp.path[idx], wp.path[idx + 1])
        location.bearing = wp.bearing.toFloat()

        mSnappedLocation = location
        if (updateMyLocation && locationLayerPlugin != null) {
            locationLayerPlugin.forceLocationUpdate(location)
//            locationLayerPlugin.location = location
        }
    }

    private fun snapFloc(location: LatLng, wp: WayPoint, idx: Int): LatLng {
        mCurrentWayPoint = wp
        mSegmentIndex = idx

        return SphericalUtil.closestPoint(
                LatLng(location.latitude, location.longitude),
                wp.path[idx],
                wp.path[index + 1]
        )
    }

    fun getNextSnappedPoint(dist: Double, distCheck: Double, bearingCheck: Double): LatLng? {
        if (mSnappedLocation == null) {
            return null
        }
        val snappedLatLng = LatLng(mSnappedLocation!!.latitude, mSnappedLocation!!.longitude)
        val snappedIndex = mSegmentIndex
        val remainDistance = mCurrentWayPoint!!.distances[0] - (distCheck * 1000)
        if (remainDistance < 0) {
            mCurrentWayPoint!!.putDistance(0.0)
        }
        val bearing = SphericalUtil.getBearing(snappedLatLng, mCurrentWayPoint!!.path[snappedIndex + 1])
        val db = Math.abs(bearingCheck - bearing)
        if (!(db < 90 || (270 < db && db < 360))) {
            return null
        }

        val ll = getPointWithDistanceAndBearing(snappedLatLng, dist, bearing)
        val index = PolyUtil.snapIndex(ll, mCurrentWayPoint!!.path, mSnappedLocation!!.accuracy + 10.0)
        val flocSnapped = snapFloc(ll!!, mCurrentWayPoint!!, index)
        mCurrentWayPoint!!.putDistance(SphericalUtil.computeLength(mCurrentWayPoint!!.path, flocSnapped, index))
        return ll
    }

    fun getPointWithDistanceAndBearing(snappedLatLng: LatLng, dist: Double, brng: Double): LatLng? {
        var dist = dist
        var brng = brng
        dist /= 6371.0
        brng = Math.toRadians(brng)
        val lat1 = Math.toRadians(snappedLatLng.latitude)
        val lon1 = Math.toRadians(snappedLatLng.longitude)
        val lat2 = Math.asin(Math.sin(lat1) * Math.cos(dist) + Math.cos(lat1) * Math.sin(dist) * Math.cos(brng))
        val lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(dist) * Math.cos(lat1), Math.cos(dist) - Math.sin(lat1) * Math.sin(lat2))
        return if (!java.lang.Double.isNaN(lat2) && !java.lang.Double.isNaN(lon2)) LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2)) else null
    }

    fun parseWayPoints(data: FindShortestPath) {
        wayPoints.clear()
        index = 0
        if (!data.hasValue())
            return

        mPathLength = data.pathLength
        var lastSeg = 0
        val resultScript = data.resultScript!!
        val legs = resultScript.legs!!
        val legSize = legs.size

        legs.forEachIndexed { iLeg, leg ->
            leg.steps?.forEachIndexed { iStep, step ->
                if (iStep == 0) {
                    if (iLeg > 0) {
                        val wp = WayPoint()
                        val beforeStep = legs[iLeg - 1].steps!!
                        val size = beforeStep.size
                        wp.latLng = LatLng(step.y, step.x)
                        wp.step = step
                        wp.len = beforeStep[size - 1].len
                        wp.dur = beforeStep[size - 1].duration
                        wp.street = beforeStep[size - 1].name!!.replaceFirst("Đường ", "")
                        val path = data.fullPath!![0][iLeg - 1]
                        val n = path.size / 2
                        for (j in lastSeg..n) {
                            val lon = path[j * 2]
                            val lat = path[j * 2 + 1]
                            wp.path.add(LatLng(lat, lon))
                        }
                        lastSeg = 0
                        wp.putDistance(wp.len)
                        wayPoints.add(wp)
                    }
                } else {
                    val beforeStep = leg.steps!![iStep - 1]
                    val wp = WayPoint()
                    wp.latLng = LatLng(step.y, step.x)
                    wp.step = step
                    wp.len = beforeStep.len
                    wp.dur = beforeStep.duration
                    wp.street = beforeStep.name!!.replaceFirst("Đường ", "")
                    val path = data.fullPath!![0][iLeg]
                    for (j in lastSeg..step.start) {
                        val lon = path[j * 2]
                        val lat = path[j * 2 + 1]
                        wp.path.add(LatLng(lat, lon))
                    }
                    lastSeg = step.start
                    wp.putDistance(wp.len)
                    wayPoints.add(wp)
                }
            }
        }

        if (data.whatHereEndPoint != null) {
            val wp = WayPoint()
            wp.step = FindShortestPath.Step()
            wp.step.name = data.whatHereEndPoint!!.name
            wp.step.dir = "15"
            wp.isLast = true
            val steps = legs[legSize - 1].steps!!
            val step = steps[steps.size - 1]
            wp.len = step.len
            wp.dur = step.duration
            wp.latLng = LatLng(data.endPoint)
            val path = data.fullPath!![0][legSize - 1]
            var i = lastSeg
            val end = path.size / 2
            while (i < end) {
                val lon = path[i * 2]
                val lat = path[i * 2 + 1]
                wp.path.add(LatLng(lat, lon))
                i++
            }
            wayPoints.add(wp)

            mCurrentWayPoint = wayPoints[0]
            mSegmentIndex = 0
            mSnappedLocation = null
            mInitial = true
        }
    }

    fun getRemainCost(): Double {
        var cost = 0.0
        val end = wayPoints.size - 1
        val start = index + 1
        for (i in start..end) {
            cost += wayPoints[i].dur
        }
        cost += (mCurrentWayPoint!!.distances[0] / mCurrentWayPoint!!.len) * mCurrentWayPoint!!.dur
        return cost
    }

    fun compareCost(paths: List<FindShortestPath>): Int {
        return 0
    }
}