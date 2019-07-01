package com.hb.coop.data.api.response.coop

import com.google.gson.annotations.SerializedName
import com.hb.coop.data.api.response.ObjectResponse
import com.hb.coop.data.entity.SaleOrderProduct

class SaleOrderProductResponse(
    @SerializedName("sale_order_list")
    val listData: List<SaleOrderProduct>
) : ObjectResponse()