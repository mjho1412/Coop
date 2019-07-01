package com.hb.coop.data.api.request.vbd

import com.google.gson.annotations.SerializedName

data class SearchParams(
    @SerializedName("Keyword")
        var keyword: String = "",
    @SerializedName("Page")
        var page: Int = 1,
    @SerializedName("PageSize")
        var pageSize: Int = 10,

    @SerializedName("Point")
        var point: Point? = null,
    @SerializedName("Radius")
        var radius: Int = 1000 * 1000,

    @SerializedName("IsOrder")
        var isOrder: Boolean? = null,
    @SerializedName("Lx")
        var lx: Double? = null,
    @SerializedName("Ly")
        var ly: Double? = null,
    @SerializedName("Rx")
        var rx: Double? = null,
    @SerializedName("Ry")
        var ry: Double? = null
) {
    class Point(
            @SerializedName("Latitude")
            var latitude: Double = 0.0,
            @SerializedName("Longitude")
            var longitude: Double = 0.0
    )

    companion object {
        fun newPoint(lat: Double, lon: Double): Point {
            return Point(lat, lon)
        }
    }
}