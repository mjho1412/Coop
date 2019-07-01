package com.hb.travinh.data.pref

interface PreferenceHelper {
    companion object {
        const val TOKEN_FIREBASE_TAG = "TOKEN_FIREBASE_TAG"
        const val TOKEN_TAG = "TOKEN_TAG"
        const val CATEGORY_TYPE_TAG = "CATEGORY_TYPE_TAG"
        const val USERNAME_TAG = "USERNAME_TAG"
        const val PASSWORD_TAG = "PASSWORD_TAG"

    }

    fun setFirebaseToken(token: String)

    fun getFirebaseToken(): String

    fun setToken(token: String)

    fun getToken(): String

    fun isLogin(): Boolean

    fun setUsername(user: String)

    fun getUsername(): String

    fun setPassword(pass: String)

    fun getPassword(): String

}