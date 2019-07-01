package com.hb.coop.data.entity

import com.google.gson.annotations.SerializedName

class Transaction {

    @SerializedName("product")
    var product: Product? = null

    @SerializedName("product_packing")
    var packing: Packing? = null

    @SerializedName("basket")
    var basket: Basket? = null


    @SerializedName("transaction_id")
    var id: Int = 0
    @SerializedName("transaction_quantity")
    var quantity: Double = 0.0
    @SerializedName("transaction_quality_test_flag")
    var qualityTestFlag: Boolean = true
    @SerializedName("transaction_created_at")
    var createAt: String = ""
    @SerializedName("vehicle_transport_id")
    var vehicleId: Int = 0


    @SerializedName("container")
    var container: Any? = null
    @SerializedName("passed_flag")
    var passedFlag: Any? = null
}