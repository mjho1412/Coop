package com.hb.coop.data.api.response

import com.google.gson.annotations.SerializedName

class ImageUploadResponse : ObjectResponse() {

    @SerializedName("image_type")
    var imageType: String = ""

    @SerializedName("image_url")
    var imageUrl: String = ""

    @SerializedName("url")
    var uploadUrl: String = ""

    @SerializedName("avatar_url")
    var avatarUrl: String = ""
}