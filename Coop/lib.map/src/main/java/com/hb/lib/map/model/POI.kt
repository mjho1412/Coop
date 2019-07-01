package com.hb.lib.map.model

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import com.mapbox.mapboxsdk.geometry.LatLng




data class POI(

        /**
         * Building : String content
         * CategoryCode : 2147483647
         * District : String content
         * Fax : String content
         * Floor : String content
         * GroupCode : 2147483647
         * Latitude : 1.26743233E15
         * Longitude : 1.26743233E15
         * Name : String content
         * Number : String content
         * Phone : String content
         * Province : String content
         * Room : String content
         * Street : String content
         * VietbandoId : String content
         * Ward : String content
         */

        @SerializedName("VietbandoId")
        var vietbandoId: String = "",

        @SerializedName("Name")
        var name: String = "",

        // Group, Category
        @SerializedName("GroupCode")
        var groupCode: Int = 0,
        @SerializedName("CategoryCode")
        var categoryCode: Int = 0,

        // GeoPoint
        @SerializedName("Latitude")
        var latitude: Double = 0.0,
        @SerializedName("Longitude")
        var longitude: Double = 0.0,

        //  Contact
        @SerializedName("Phone")
        var phone: String? = null,
        @SerializedName("Fax")
        var fax: String? = null,

        // Building
        @SerializedName("Building")
        var building: String? = "",
        @SerializedName("Room")
        var room: String? = "",
        @SerializedName("Floor")
        var floor: String? = "",

        //  Address
        @SerializedName("Number")
        var number: String? = "",
        @SerializedName("Street")
        var street: String? = "",
        @SerializedName("Ward")
        var ward: String? = "",
        @SerializedName("District")
        var district: String? = "",
        @SerializedName("Province")
        var province: String? = "",

        @SerializedName("Additions")
        var additions: List<Addition>? = null,

        var isSearch: Boolean = true
) : Parcelable {
    var distance = 0.0

    var address = ""

    fun getAddress(hasName: Boolean = true): String {

        if (!TextUtils.isEmpty(address))
            return address

        if (hasName) {
            if (!TextUtils.isEmpty(name)) address += "$name, "
        }

        if (!TextUtils.isEmpty(number)) address += "$number, "
        if (!TextUtils.isEmpty(building)) address += "$building, "
        if (!TextUtils.isEmpty(floor)) address += "$floor, "
        if (!TextUtils.isEmpty(room)) address += "$room, "
        if (!TextUtils.isEmpty(street)) address += "$street, "
        if (!TextUtils.isEmpty(ward)) address += "$ward, "
        if (!TextUtils.isEmpty(district)) address += "$district, "
        if (!TextUtils.isEmpty(province)) address += "$province"

        address = address.trim()
        if (TextUtils.isEmpty(address)) {
            address += "$latitude, $longitude"
        }

        if (address.last() == ',') {
            address = address.substring(0, address.lastIndex)
        }
        return address
    }

    fun getShortAddress(): String {
        return getAddress()
//        if (!TextUtils.isEmpty(name) && name.length > 20) {
//            return name
//        }
//
//        var address = ""
//
//        if (!TextUtils.isEmpty(name)) address += "$name, "
//        if (!TextUtils.isEmpty(number)) address += "$number, "
//        if (!TextUtils.isEmpty(street)) address += "$street"
//
//        address = address.trim()
//        if (TextUtils.isEmpty(address)) {
//            address += "$latitude, $longitude"
//        }
//
//        return address
    }

    fun getLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }

    fun getGeoPoint(): GeoPoint {
        return GeoPoint(
                latitude = latitude,
                longitude = longitude,
                address = getAddress())
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readInt(),
            source.readInt(),
            source.readDouble(),
            source.readDouble(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            ArrayList<Addition>().apply { source.readList(this, Addition::class.java.classLoader) },
            1 == source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(vietbandoId)
        writeString(name)
        writeInt(groupCode)
        writeInt(categoryCode)
        writeDouble(latitude)
        writeDouble(longitude)
        writeString(phone)
        writeString(fax)
        writeString(building)
        writeString(room)
        writeString(floor)
        writeString(number)
        writeString(street)
        writeString(ward)
        writeString(district)
        writeString(province)
        writeList(additions)
        writeInt((if (isSearch) 1 else 0))
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<POI> = object : Parcelable.Creator<POI> {
            override fun createFromParcel(source: Parcel): POI = POI(source)
            override fun newArray(size: Int): Array<POI?> = arrayOfNulls(size)
        }
    }

    data class Addition(
        @SerializedName("Latitude") val latitude: Double,
        @SerializedName("Longitude") val longitude: Double,
        @SerializedName("Name") val name: String,
        @SerializedName("Desc") val desc: String
    ) {

        fun toPOI(): POI {
            val poi = POI(
                    latitude = latitude,
                    longitude = longitude,
                    name =  name
            )
            poi.address = this.desc
            return poi
        }
    }
}