package com.hb.coop.data.store.passport

import com.hb.coop.data.entity.Passport
import com.hb.coop.data.entity.Profile
import io.reactivex.Observable
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Created by buihai on 9/9/17.
 */

interface PassportStore {

    companion object {
        const val VERSION = "v1"
    }

    interface LocalStorage {
        fun savePassport(passport: Passport)

        fun getToken(): String

        fun saveToken(token: String)

        fun getTokenWithBearer(): String

        fun saveProfile(profile: Profile)

        fun loadProfile(): Profile?

        fun getPassport(): Passport

        fun saveUserPassword(user: String, pass: String)

        fun getUserPassword(): Pair<String, String>
    }

    interface RequestService {

        @POST("login")
        fun login(@Header("Authorization") auth: String): Observable<Passport>


    }
}