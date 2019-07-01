package com.hb.coop.data

import android.content.Context
import com.hb.coop.data.cache.ICache
import com.hb.coop.data.entity.*
import com.hb.travinh.data.pref.PreferenceHelper

class AppDataManager
constructor(
    private val context: Context,
    private val pref: PreferenceHelper,
    private val cache: ICache
) : DataManager {

    companion object {
        const val PROFILE_TAG = "PROFILE"
        const val PASSPORT_TAG = "PASSPORT"
    }

    private var poi: GeoObject? = null
    private var district: Region? = null
    private var ward: Region? = null

    private var product: SaleOrderProduct? = null

    private var mBarcodeList: Array<String> = arrayOf()

    override fun logout() {
        setPassport(null).setToken("")
    }

    override fun setFirebaseToken(token: String) {
        pref.setFirebaseToken(token)
    }

    override fun getFirebaseToken(): String {
        return pref.getFirebaseToken()
    }

    override fun setToken(token: String) {
        pref.setToken(token)
    }

    override fun getToken(): String = pref.getToken()

    override fun isLogin(): Boolean = pref.isLogin()

    override fun setPassport(passport: Passport?): DataManager {
        if (cache.contains(PASSPORT_TAG)) {
            cache.delete(PASSPORT_TAG)
        }
        cache.setObject(PASSPORT_TAG, passport)
        return this
    }

    override fun getPassport(): Passport? = cache.getObject(PASSPORT_TAG, Passport::class.java)

    override fun setProfile(profile: Profile?): DataManager {
        if (cache.contains(PROFILE_TAG)) {
            cache.delete(PROFILE_TAG)
        }
        cache.setObject(PROFILE_TAG, profile)
        return this
    }

    override fun getProfile(): Profile? = cache.getObject(PROFILE_TAG, Profile::class.java)

    override fun setUsername(user: String) {
        pref.setUsername(user)
    }

    override fun getUsername(): String {
        return pref.getUsername()
    }

    override fun setPassword(pass: String) {
        pref.setPassword(pass)
    }

    override fun getPassword(): String {
        return pref.getPassword()
    }

    override fun setGeoObject(poi: GeoObject): DataManager {
        this.poi = poi
        return this
    }

    override fun getGeoObject(): GeoObject? {
        return poi
    }

    override fun setDistrict(region: Region?): DataManager {
        district = region
        return this
    }

    override fun getDistrict(): Region? {
        return district
    }

    override fun setWard(region: Region?): DataManager {
        ward = region
        return this
    }

    override fun getWard(): Region? {
        return ward
    }

    override fun setSaleOrderProduct(product: SaleOrderProduct): DataManager {
        this.product = product
        return this
    }

    override fun getSaleOrderProduct(): SaleOrderProduct? {
        return product
    }

    override fun setBarcodeList(data: Array<String>) {
        mBarcodeList = data
    }

    override fun getBarcodeList(): Array<String> {
        return mBarcodeList
    }
}