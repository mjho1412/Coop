package com.hb.coop.ui.test.product

import com.hb.lib.mvp.impl.lce.HBMvpLcePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ProductPresenter
@Inject constructor(

) : HBMvpLcePresenter<ProductActivity>(), ProductContract.Presenter {

    override fun loadData(pullToRefresh: Boolean) {
        if (isViewAttached()) {
            getView().showLoading(pullToRefresh)
        }

        disposable.clear()
        disposable.add(
            Observable.just(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (isViewAttached()) {
                        getView().setData(ArrayList<Any>())
                        getView().showContent()
                    }
                }, {

                })
        )
    }
}