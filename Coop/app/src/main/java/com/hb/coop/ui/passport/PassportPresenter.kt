package com.hb.coop.ui.passport

import com.hb.coop.data.DataManager
import com.hb.coop.data.repository.PassportRepository
import com.hb.coop.data.repository.SystemRepository
import com.hb.lib.mvp.impl.HBMvpPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class PassportPresenter
@Inject constructor(
    private val passportRepository: PassportRepository
) : HBMvpPresenter<PassportActivity>(), PassportContract.Presenter {


    override fun login(user: String, pass: String) {
        disposable.clear()
        disposable.addAll(
            passportRepository.login(user, pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.d("$it")
                    if (it.success) {
                        dataManager<DataManager>().setUsername(user)
                        if (isViewAttached()) {
                            getView().loginCompleted()
                        }
                    } else {
                        if (isViewAttached()) {
                            getView().loginFailed(it.detail)
                        }
                    }
                }, {
                    if (isViewAttached()) {
                        getView().loginFailed(it.message!!)
                    }
                })
        )
    }

    override fun forgetPassword(user: String) {

    }
}