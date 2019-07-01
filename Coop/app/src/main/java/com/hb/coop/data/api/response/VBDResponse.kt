package com.hb.coop.data.api.response

import com.google.gson.annotations.SerializedName


class VBDResponse<T> {

    open class Error {

        @SerializedName("ExceptionType")
        var exceptionType: String? = null

        @SerializedName("Message")
        var message: String? = null

    }

    @SerializedName("IsSuccess")
    var isSuccess: Boolean = false

    @SerializedName("ResponseTime")
    var responseTime: String? = null

    @SerializedName("Value")
    var value: T? = null

    @SerializedName("List")
    var list: List<T>? = null
}