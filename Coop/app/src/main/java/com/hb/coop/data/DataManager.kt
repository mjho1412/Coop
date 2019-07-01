package com.hb.coop.data

import com.hb.coop.data.entity.*
import com.hb.lib.data.IDataManager
import com.hb.travinh.data.pref.PreferenceHelper

interface DataManager : IDataManager, PreferenceHelper {

    fun logout()

    fun setPassport(passport: Passport?): DataManager

    fun getPassport(): Passport?

    fun setProfile(profile: Profile?): DataManager

    fun getProfile(): Profile?

    fun setGeoObject(poi: GeoObject): DataManager

    fun getGeoObject(): GeoObject?

    fun setDistrict(region: Region?): DataManager

    fun getDistrict(): Region?

    fun setWard(region: Region?): DataManager

    fun getWard(): Region?

    fun setSaleOrderProduct(product: SaleOrderProduct) : DataManager

    fun getSaleOrderProduct() : SaleOrderProduct?

    fun setBarcodeList(data: Array<String>)

    fun getBarcodeList(): Array<String>

}