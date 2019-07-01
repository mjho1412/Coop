package com.hb.coop.data.api.response.coop

import com.google.gson.annotations.SerializedName
import com.hb.coop.data.api.response.ObjectResponse
import com.hb.coop.data.entity.Transaction

data class TransactionResponse(
    @SerializedName("transaction_list")
    var listData: List<Transaction>
) : ObjectResponse()