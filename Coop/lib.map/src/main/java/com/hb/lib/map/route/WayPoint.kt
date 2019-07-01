package com.hb.lib.map.route

import com.hb.lib.map.model.FindShortestPath
import com.mapbox.mapboxsdk.geometry.LatLng

class WayPoint {
    lateinit var latLng: LatLng
    lateinit var step: FindShortestPath.Step
    var path = ArrayList<LatLng>()
    var len: Double = 0.0
    var dur: Double = 0.0
    var street: String = ""
    var bearing: Double = 0.0
    var distances = DoubleArray(3)
    var isLast = false

    fun putDistance(distance: Double) {
        distances[2] = distances[1]
        distances[1] = distances[0]
        distances[0] = distance
    }
}