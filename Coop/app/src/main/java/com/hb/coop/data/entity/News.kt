package com.hb.coop.data.entity

import com.google.gson.annotations.SerializedName

data class News(
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("news_id") val newsId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("desc") val desc: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("detail_url") val detailUrl: String
)