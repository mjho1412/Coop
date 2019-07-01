package com.hb.coop.data.api.response

import com.google.gson.annotations.SerializedName
import com.hb.coop.data.entity.GeoObject
import com.hb.coop.data.entity.Region

open class ObjectResponse {

    @SerializedName("success")
    var success: Boolean = false

    @SerializedName("detail")
    var detail: String = ""

    @SerializedName("code")
    var code: Int = 0

    @SerializedName("total_page")
    var totalPage: Int = 0

    @SerializedName("total_record")
    val total: Int = 0

    open fun getGeoData(): List<GeoObject> {
        return ArrayList()
    }
    open fun getRegionData(): List<Region> {
        return ArrayList()
    }


}
