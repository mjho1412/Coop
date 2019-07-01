package com.hb.coop.data.entity

import com.google.gson.annotations.SerializedName



data class SaleOrderProduct(
    @SerializedName("id")
    val id: Int,
    @SerializedName("quantity")
    val quantity: Double,
    @SerializedName("product")
    val product: Product,
    @SerializedName("product_packing")
    val packing: Packing,
    @SerializedName("sale_order_sum")
    val saleOrderSum: Any,
    @SerializedName("created_at")
    val createAt: String
)