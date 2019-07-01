package com.hb.coop.data.entity

import com.google.gson.annotations.SerializedName


//data class Product(
//    @SerializedName("id") val id:Int,
//    @SerializedName("name") val name: String,
//    @SerializedName("description") val description: String,
//    @SerializedName("categoryId") val categoryId: Int,
//    @SerializedName("quantity") val quantity: Float = 10.0f
//)

open class Product {
    @SerializedName("category")
    var category: String = ""
    @SerializedName("id")
    var id: Int = 0
    @SerializedName("name")
    var name: String = ""
    @SerializedName("sku")
    var sku: Int = 0
    @SerializedName("avatar")
    val avatar: String = ""

}