package com.hb.coop.ui.scanner

import com.hb.coop.data.DataManager
import com.hb.lib.mvp.impl.HBMvpPresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ScannerPresenter
@Inject constructor(

) : HBMvpPresenter<ScannerActivity>(), ScannerContract.Presenter {

    private var mData = ArrayList<String>()

    override fun addContentScanner(content: String) {
        mData.add(content)
    }

    override fun clearAllContentScanner() {
        mData.clear()
    }

    override fun getAllContentScanner(): ArrayList<String> {
        return mData
    }

    override fun saveBarcodeList() {
        disposable.add(
            Observable.create<Boolean> {
                try {
                    val dataManager = dataManager<DataManager>()
                    val arr = arrayOf<String>()
                    dataManager.setBarcodeList(mData.toArray(arr))
                    it.onNext(true)
                    it.onComplete()
                } catch (e: Exception) {
                    it.onError(e)
                }
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (isViewAttached()) {
                        getView().completed()
                    }
                }, {
                    if (isViewAttached()) {
                        getView().showError(it)
                    }
                })
        )

    }
}