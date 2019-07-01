package com.hb.coop.data.api.request.coop

import com.google.gson.annotations.SerializedName

data class BasketByProductParams(
    @SerializedName("sale_order_id") val saleOrderId: Int? = null,
    @SerializedName("basket_id") val basketId: Int? = null,
    @SerializedName("quantity") var quantity: Int? = null
) {
    @SerializedName("transaction_id")
    var transactionId: Int? = null
}