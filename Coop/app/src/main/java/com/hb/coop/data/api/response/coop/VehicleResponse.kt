package com.hb.coop.data.api.response.coop

import com.google.gson.annotations.SerializedName
import com.hb.coop.data.api.response.ObjectResponse
import com.hb.coop.data.entity.Vehicle


data class VehicleResponse(
    @SerializedName("vehicle_list")
    var listData: List<Vehicle>
) : ObjectResponse()