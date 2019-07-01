package com.hb.coop.data.api.request.vbd

import com.google.gson.annotations.SerializedName
import com.hb.coop.common.AppConstants
import com.hb.lib.map.model.GeoPoint

class DistanceParams {
    @SerializedName("AlleyAvoidance")
    var alleyAvoidance: Boolean = false
    @SerializedName("TransportType")
    var transportType = AppConstants.TRANSPORT_CAR

    @SerializedName("Sourses")
    var sources: List<GeoPoint>? = null
    @SerializedName("Destinations")
    var destinations: List<GeoPoint>? = null
}