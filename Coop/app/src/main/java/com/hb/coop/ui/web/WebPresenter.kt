package com.hb.coop.ui.web

import android.annotation.SuppressLint
import com.hb.coop.data.DataManager
import com.hb.lib.mvp.impl.lce.HBMvpLcePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class WebPresenter
@Inject constructor() : HBMvpLcePresenter<WebActivity>(), WebContract.Presenter {

    @Inject
    lateinit var dm: DataManager

    @SuppressLint("CheckResult")
    override fun loadData(pullToRefresh: Boolean) {
        if (isViewAttached()) {
            getView().showLoading(pullToRefresh)
        }

        val testUrl = getView().intent?.extras?.getString(WebContract.WEB_URL_TAG)!!
        Observable.just(testUrl)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (isViewAttached()) {
                    getView().setData(it)
                    getView().showContent()
                }
            }, {
                Timber.e(it)
                if (isViewAttached()) {
                    getView().showError(it, pullToRefresh)
                }
            })
    }
}