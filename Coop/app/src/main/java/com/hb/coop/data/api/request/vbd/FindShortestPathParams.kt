package com.hb.coop.data.api.request.vbd

import com.google.gson.annotations.SerializedName
import com.hb.coop.common.AppConstants
import com.hb.lib.map.model.GeoPoint

class FindShortestPathParams {
    @SerializedName("AlleyAvoidance")
    var alleyAvoidance: Boolean = false
    @SerializedName("TransportType")
    var transportType = AppConstants.TRANSPORT_CAR
    @SerializedName("Points")
    var points: List<GeoPoint>? = null
}