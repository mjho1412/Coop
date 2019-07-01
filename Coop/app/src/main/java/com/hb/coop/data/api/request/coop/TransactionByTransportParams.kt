package com.hb.coop.data.api.request.coop

import com.google.gson.annotations.SerializedName

data class TransactionByTransportParams(
    @SerializedName("transport_id")
    val transportId: Int,
    @SerializedName("basket_id")
    val basketId: Int? = null
)