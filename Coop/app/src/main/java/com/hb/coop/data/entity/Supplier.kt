package com.hb.coop.data.entity

import com.google.gson.annotations.SerializedName

data class Supplier(
    @SerializedName("id") val id: Int,
    @SerializedName("Name") val name: String,
    @SerializedName("owner") val owner: String?,
    @SerializedName("mobile") val mobile: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("ward") val ward: String?,
    @SerializedName("district") val district: String?,
    @SerializedName("province") val province: String?,
    @SerializedName("type") val type: Type?
) {

    data class Type(
        @SerializedName("description")
        val description: Any,
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String
    )
}

