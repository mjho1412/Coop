package com.hb.coop.data.store.passport


import com.hb.coop.data.DataManager
import com.hb.coop.data.entity.Passport
import com.hb.coop.data.entity.Profile

/**
 * Created by buihai on 9/9/17.
 */

class PassportLocalStorage(
        private val dm: DataManager
) : PassportStore.LocalStorage {

    override fun savePassport(passport: Passport) {
        dm.setToken(passport.key)
        dm.setPassport(passport)
    }

    override fun saveToken(token: String) {
        dm.setToken(token)
    }

    override fun getToken(): String = dm.getToken()

    override fun getTokenWithBearer(): String = "Bearer ${dm.getToken()}"

    override fun saveProfile(profile: Profile) {
        dm.setProfile(profile)
    }

    override fun loadProfile(): Profile? {
        return dm.getProfile()
    }

    override fun getPassport(): Passport {
        return dm.getPassport()!!
    }

    override fun saveUserPassword(user: String, pass: String) {
        dm.apply {
            setUsername(user)
            setPassword(pass)
        }
    }

    override fun getUserPassword(): Pair<String, String> {
        return Pair(
            dm.getUsername(),
            dm.getPassword()
        )
    }
}
