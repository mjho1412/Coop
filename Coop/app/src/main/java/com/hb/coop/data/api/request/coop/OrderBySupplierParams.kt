package com.hb.coop.data.api.request.coop

import com.google.gson.annotations.SerializedName
import com.hb.coop.data.api.request.PageParams

data class OrderBySupplierParams(
    @SerializedName("supplier_id")
    val supplierId: Int
) : PageParams()