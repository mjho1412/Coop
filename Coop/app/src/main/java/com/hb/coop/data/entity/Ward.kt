package com.hb.coop.data.entity

import com.google.gson.annotations.SerializedName


data class Ward(
    @SerializedName("district_id") val districtId: Int,
    @SerializedName("district_name") val districtName: String,
    @SerializedName("ward_id") val wardId: Int,
    @SerializedName("ward_name") val wardName: String,
    @SerializedName("geometry") val geometry: Any,
    @SerializedName("level") val level: String,
    @SerializedName("gasoline_store_count") val gasolineStoreCount: Int,
    @SerializedName("medical_count") val medicalCount: Int
): Region {

    var district: Region? = null

    override fun getId(): Int {
        return wardId
    }

    override fun getName(): String {
        return wardName
    }

    override fun getParent(): Region? {
        return district
    }

    override fun getGasoline(): Int {
        return gasolineStoreCount
    }

    override fun getMedical(): Int {
        return medicalCount
    }
}