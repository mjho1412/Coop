package com.hb.coop.data.entity
import com.google.gson.annotations.SerializedName


data class Basket(
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("location")
    val location: Int,
    @SerializedName("rfid")
    val rfid: Any,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("used_flag")
    val usedFlag: Boolean
)