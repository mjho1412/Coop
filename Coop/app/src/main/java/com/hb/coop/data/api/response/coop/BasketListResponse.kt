package com.hb.coop.data.api.response.coop

import com.google.gson.annotations.SerializedName
import com.hb.coop.data.api.response.ObjectResponse
import com.hb.coop.data.entity.Basket

data class BasketListResponse(
    @SerializedName("basket_list")
    var listData: List<Basket>
) : ObjectResponse()