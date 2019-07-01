package com.hb.lib.map.model

import com.google.gson.annotations.SerializedName
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import java.util.*

class FindShortestPath {

    @SerializedName("StartPoint")
    var startPoint: LatLng? = null
    var whatHereStartPoint: POI? = null

    @SerializedName("EndPoint")
    var endPoint: LatLng? = null
    var whatHereEndPoint: POI? = null

    @SerializedName("FullPath")
    var fullPath: Array<Array<DoubleArray>>? = null

    @SerializedName("PathLength")
    var pathLength: Double = 0.toDouble()

    @SerializedName("RealPlaces")
    var realPlaces: DoubleArray? = null

    @SerializedName("Segments")
    var segments: IntArray? = null

    @SerializedName("ResultScript")
    var resultScript: ResultScript? = null

    inner class ResultScript {
        @SerializedName("Leg")
        var legs: Array<Leg>? = null

        @SerializedName("len")
        var len: Double = 0.0
    }

    inner class Leg {

        @SerializedName("Step")
        var steps: Array<Step>? = null

        @SerializedName("endX")
        var endX: Double = 0.0

        @SerializedName("endY")
        var endY: Double = 0.0

        @SerializedName("found")
        var found: Boolean = false

        @SerializedName("len")
        var len: Double = 0.0

        @SerializedName("startX")
        var startX: Double = 0.0

        @SerializedName("startY")
        var startY: Double = 0.0
    }

    class Step {
        @SerializedName("X")
        var x: Double = 0.0

        @SerializedName("Y")
        var y: Double = 0.0

        @SerializedName("angle")
        var angle: Int = 0

        @SerializedName("dir")
        var dir: String? = null

        @SerializedName("len")
        var len: Double = 0.0

        @SerializedName("name")
        var name: String? = null

        @SerializedName("start")
        var start: Int = 0

        @SerializedName("duration")
        var duration: Double = 0.0

        @Transient
        var position: Int = 0
    }

    fun hasValue(): Boolean {
        return fullPath != null && resultScript != null && resultScript!!.legs != null && resultScript!!.legs!!.isNotEmpty()
    }

    companion object {
        fun map(route: FindShortestPath): List<LatLng> {
            val path = ArrayList<LatLng>()
            val size = route.fullPath!![0][0].size
            val data = route.fullPath!![0][0]
            var i = 0
            while (i < size) {
                val lon = data[i]
                val lat = data[i + 1]
                path.add(LatLng(lat, lon))
                i += 2
            }
            return path
        }

        fun map2Points(route: FindShortestPath): List<Point> {
            val path = ArrayList<Point>()
            val size = route.fullPath!![0][0].size
            val data = route.fullPath!![0][0]
            var i = 0
            while (i < size) {
                val lon = data[i]
                val lat = data[i + 1]
                path.add(Point.fromLngLat(lon, lat))
                i += 2
            }
            return path
        }
    }

}
