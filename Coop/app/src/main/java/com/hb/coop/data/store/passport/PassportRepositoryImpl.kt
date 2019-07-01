package com.hb.coop.data.store.passport


import com.hb.coop.data.api.response.ObjectResponse
import com.hb.coop.data.entity.Passport
import com.hb.coop.data.repository.PassportRepository
import io.reactivex.Observable
import okhttp3.Credentials
import timber.log.Timber

/**
 * Created by buihai on 9/9/17.
 */

class PassportRepositoryImpl(
    private val storage: PassportStore.LocalStorage,
    private val service: PassportStore.RequestService
) : PassportRepository {

    companion object {
        const val IS_TEST = false
    }

    override fun login(user: String, pass: String): Observable<Passport> {
        val authInBase64 = Credentials.basic(user, pass)

        val loginFunc = if (IS_TEST) {
            Observable.just(Passport(
                userId = 1,
                key = "Key",
                fullName = "Full Name",
                email = "Email",
                avatarUrl = "Avatar Url",
                mobile = "Mobile",
                config = Passport.Config(
                    keyMap = "",
                    backgroundUrl = ""
                ),
                userTypeId = 9,
                userTypeName = "Admin",
                userTypeCode = "ADMIN",
                rule = "Admin",
                ruleId = 1,
                ruleName = "Admin"
            ).apply {
                success = true
            })
        } else {
            service.login(auth = authInBase64)
        }

        return loginFunc
            .doOnNext {
                Timber.d("$it")
                if (it.success) {
                    storage.savePassport(it)
                    storage.saveUserPassword(user, pass)
                }
            }
    }

    override fun reLogin(): Observable<ObjectResponse> {
        val pair = storage.getUserPassword()
        Timber.d("${pair.first} -- ${pair.second} ")
        return login(pair.first, pair.second)
            .map {
                ObjectResponse().apply {
                    success = it.success
                }
            }
//        return Observable.just(ObjectResponse().apply {
//            success = true
//        })
//        return Observable.create<Pair<String, String>> {
//            try {
//                val passport = storage.getPassport()
//                Timber.d("$passport")
//                it.onNext(Pair(passport.userName, passport.password))
//                it.onComplete()
//            } catch (e: Exception) {
//                it.onError(e)
//            }
//        }.flatMap {
//            login(it.first, it.second)
//        }

    }
}
