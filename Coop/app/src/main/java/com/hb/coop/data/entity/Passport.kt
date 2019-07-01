package com.hb.coop.data.entity

import com.google.gson.annotations.SerializedName
import com.hb.coop.data.api.response.ObjectResponse


//data class Passport(
//    @SerializedName("user_id") val userId: Int,
//    @SerializedName("key") val key: String,
//    @SerializedName("full_name") val fullName: String,
//    @SerializedName("email") val email: String,
//    @SerializedName("avatar") val avatar: String,
//    @SerializedName("config") val config: Config
//): ObjectResponse() {
//

//
//    data class Config(
//        @SerializedName("KEY_MAP") val keyMap: String,
//        @SerializedName("BACKGROUND_URL") val backgroundUrl: String
//    )
//}


data class Passport(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("key") val key: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("config") val config: Config,
    @SerializedName("user_type_id") val userTypeId: Int,
    @SerializedName("user_type_code") val userTypeCode: String,
    @SerializedName("user_type_name") val userTypeName: String,
    @SerializedName("rule") val rule: String,
    @SerializedName(value = "branch_id", alternate = ["supplier_id", "warehouse_id", "supermarket_id"])
    val ruleId: Int,
    @SerializedName(value = "branch_name", alternate = ["supplier_name", "warehouse_name", "supermarket_name"])
    val ruleName: String
) : ObjectResponse() {

    var username = ""
    var password = ""

    data class Config(
        @SerializedName("KEY_MAP") val keyMap: String,
        @SerializedName("BACKGROUND_URL") val backgroundUrl: String
    )


}


