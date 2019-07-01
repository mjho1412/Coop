package com.hb.coop.ui.test.order

import com.hb.coop.common.DataProvider
import com.hb.lib.mvp.impl.HBMvpPresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class OrderPresenter
@Inject constructor(

) : HBMvpPresenter<OrderFragment>(), OrderContract.Presenter {

    override fun loadData() {

        if (isViewAttached()) {
            getView().showLoading()
        }

        disposable.clear()
        disposable.add(Observable.just(DataProvider.getData())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (isViewAttached()) {
                    getView().setData(it)
                    getView().showContent()
                }
            }, {

            }))
    }
}