package com.hb.coop.data.pref

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.hb.coop.R
import com.hb.travinh.data.pref.PreferenceHelper

class AppPreferenceHelper(val context: Context, val gson: Gson) : PreferenceHelper {

    private var pref: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE)

    override fun setFirebaseToken(token: String) {
        pref.edit().putString(PreferenceHelper.TOKEN_FIREBASE_TAG, token).apply()
    }

    override fun getFirebaseToken(): String {
        return pref.getString(PreferenceHelper.TOKEN_FIREBASE_TAG, "")!!
    }

    override fun setToken(token: String) {
        pref.edit().putString(PreferenceHelper.TOKEN_TAG, token).apply()
    }

    override fun getToken(): String = pref.getString(PreferenceHelper.TOKEN_TAG, "")!!

    override fun isLogin(): Boolean {
        return !pref.getString(PreferenceHelper.TOKEN_TAG, "")!!.isEmpty()
    }

    override fun setUsername(user: String) {
        pref.edit().putString(PreferenceHelper.USERNAME_TAG, user).apply()
    }

    override fun getUsername(): String {
        return pref.getString(PreferenceHelper.USERNAME_TAG, "")!!
    }

    override fun setPassword(pass: String) {
        pref.edit().putString(PreferenceHelper.PASSWORD_TAG, pass).apply()
    }

    override fun getPassword(): String {
        return pref.getString(PreferenceHelper.PASSWORD_TAG, "")!!
    }


}