package com.hb.coop.data.entity

import com.mapbox.mapboxsdk.geometry.LatLng

interface GeoObject {

    fun getId(): Int

    fun getLongitude(): Double

    fun getLatitude(): Double

    fun getLatLng(): LatLng

    fun getName(): String

    fun getAddress(): String

    fun getThumbnail(): String

    fun getPhoneNumber(): String

    fun getType(): String

    fun getOwner(): String

    fun getImages(): List<String>

    fun getAccreditationDate(): String
}