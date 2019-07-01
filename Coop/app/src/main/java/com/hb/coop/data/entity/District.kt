package com.hb.coop.data.entity

import com.google.gson.annotations.SerializedName

data class District(
    @SerializedName("district_id") val districtId: Int,
    @SerializedName("district_name") val districtName: String,
    @SerializedName("geometry") val polygon: String,
    @SerializedName("level") val level: String,
    @SerializedName("ward_count") val wardCount: Int,
    @SerializedName("gasoline_store_count") val gasolineCount: Int,
    @SerializedName("medical_count") val medicalCount: Int
) : Region {
    override fun getId(): Int {
        return districtId
    }

    override fun getName(): String {
        return districtName
    }

    override fun getParent(): Region? {
        return object : Region {

            override fun getId(): Int = 1

            override fun getName(): String {
                return "Tỉnh Trà Vinh"
            }

            override fun getParent(): Region? {
                return null
            }

            override fun getGasoline(): Int = 0

            override fun getMedical(): Int = 0
        }
    }

    override fun getGasoline(): Int {
        return gasolineCount
    }

    override fun getMedical(): Int {
        return medicalCount
    }
}
