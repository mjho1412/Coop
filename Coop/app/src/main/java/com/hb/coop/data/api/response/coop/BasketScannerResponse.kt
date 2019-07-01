package com.hb.coop.data.api.response.coop

import com.google.gson.annotations.SerializedName
import com.hb.coop.data.api.request.coop.BasketByProductParams
import com.hb.coop.data.api.response.ObjectResponse

data class BasketScannerResponse(
    @SerializedName("transaction_id")
    val transactionId: Int
): ObjectResponse() {

    var basketParams: BasketByProductParams? = null
}