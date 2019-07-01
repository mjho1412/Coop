package com.hb.coop.data.entity

import com.google.gson.annotations.SerializedName
import com.hb.coop.data.api.response.ObjectResponse


data class AppVersion(
    @SerializedName("version") val version: String,
    @SerializedName("force_flag") val forceFlag: Boolean,
    @SerializedName("google_play") val googlePlay: String,
    @SerializedName("version_app") val versionApp: String
) : ObjectResponse()