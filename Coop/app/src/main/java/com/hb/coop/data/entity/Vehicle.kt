package com.hb.coop.data.entity

import com.google.gson.annotations.SerializedName

data class Vehicle(
    @SerializedName("id")
    val id: Int,
    @SerializedName("box_dimension")
    val boxDimension: Any,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("driver_name")
    val driverName: String,
    @SerializedName("max_weight")
    val maxWeight: Any,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("owner")
    val owner: Any,
    @SerializedName("phone")
    val phone: Any,
    @SerializedName("plate_number")
    val plateNumber: String,
    @SerializedName("active_flag")
    val activeFlag: Boolean,
    @SerializedName("supplier")
    val supplier: Supplier?
)