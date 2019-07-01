package com.hb.coop.data.api.request.coop

import com.google.gson.annotations.SerializedName
import com.hb.coop.data.api.request.PageParams

data class TransactionParams(
    @SerializedName("sale_order_id")
    val saleOrderId: Int
) : PageParams()