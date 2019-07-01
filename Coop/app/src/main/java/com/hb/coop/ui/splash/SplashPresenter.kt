package com.hb.coop.ui.splash

import android.annotation.SuppressLint
import android.text.TextUtils
import com.google.firebase.iid.FirebaseInstanceId
import com.hb.coop.data.DataManager
import com.hb.coop.data.api.response.ObjectResponse
import com.hb.coop.data.repository.PassportRepository
import com.hb.coop.data.repository.SystemRepository
import com.hb.lib.mvp.impl.HBMvpPresenter
import com.hb.lib.utils.Utils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by buihai on 7/13/17.
 */
@SuppressLint("CheckResult")
class SplashPresenter
@Inject constructor(
    private val systemRepository: SystemRepository,
    private val passportRepository: PassportRepository
) : HBMvpPresenter<SplashActivity>(), SplashContract.Presenter {

    override fun loadAppVersion() {
        systemRepository.getAppVersion()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (isViewAttached()) {
                    if (it.success) {
                        val view = getView()
                        val versionCurrent = Utils.getVersionCode(view)
                        val newVersion = it.version.toInt()
                        if (versionCurrent < newVersion) {
                            view.showUpdateDialog(it.forceFlag)
                        } else {
                            loadData()
                        }
                    } else {
                        if (isViewAttached()) {
                            if (!TextUtils.isEmpty(it.detail)) {
                                getView().showErrorDialog(it.detail)
                            }
                        }
                        loadData()
                    }
                }
            }, {
                if (isViewAttached()) {
                    getView().showErrorDialog("${it.message}")
                }
            })
    }

    override fun loadData() {
//        dataManager.enabledFindCabNearby(enabled = true)
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result!!.token
//                dataManager.setFirebaseToken(token)
                Timber.d("Token: $token")
            }
            disposable.add(
                Observable.just(isLogin())
                    .flatMap { isLogin ->
                        if (isLogin) {
                            passportRepository.reLogin()
                        } else {
                            Observable.just(ObjectResponse().apply {
                                success = isLogin
                            }).delay(900, TimeUnit.MILLISECONDS)
                        }
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (isViewAttached()) {
//                            if (TestActivity.IS_TEST) {
//                                getView().openTestActivity()
//                            } else {
                            if (!isIntroduction()) {
                                getView().openIntroductionActivity()
                            } else if (!it.success) {
                                getView().openPassportActivity()
                            } else {
                                getView().openMainActivity()
                            }
//                            }
                        }
                    }, {
                        Timber.e(it)
                        if (isViewAttached()) {
                            getView().showErrorDialog("Thông báo: ${it.message}")
                            getView().openPassportActivity()
                        }
                    })
            )
        }

    }

    override fun isIntroduction(): Boolean {
        return true
    }

    override fun isLogin(): Boolean {
        Timber.d("IsLogin: ${dataManager<DataManager>().isLogin()}")
        return dataManager<DataManager>().isLogin()
    }

}
