package com.hb.lib.map.model

import com.google.gson.annotations.SerializedName
import com.mapbox.mapboxsdk.geometry.LatLng

data class GeoPoint(@SerializedName("Latitude") val latitude: Double,
                    @SerializedName("Longitude") val longitude: Double,
                    @SerializedName("Address") val address: String = "") {

    companion object {
        fun fromLatLng(latLng: LatLng): GeoPoint {
            return GeoPoint(
                    latitude = latLng.latitude,
                    longitude = latLng.longitude)
        }
    }
}