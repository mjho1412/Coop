package com.hb.coop.data.entity

import com.google.gson.annotations.SerializedName


open class Packing {
    @SerializedName("id")
    var packingId: Int = 0
    @SerializedName("unit")
    var packingUnit: String = ""
    @SerializedName("value")
    var packingValue: Double = 0.0
}
