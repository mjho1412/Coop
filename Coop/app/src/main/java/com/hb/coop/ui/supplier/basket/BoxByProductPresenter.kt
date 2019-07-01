package com.hb.coop.ui.supplier.basket

import android.view.View
import com.hb.coop.data.DataManager
import com.hb.coop.data.api.request.coop.BasketByProductParams
import com.hb.coop.data.api.request.coop.TransactionParams
import com.hb.coop.data.api.response.ObjectResponse
import com.hb.coop.data.api.response.coop.BasketScannerResponse
import com.hb.coop.data.entity.DataWrapper
import com.hb.coop.data.entity.SaleOrderProduct
import com.hb.coop.data.repository.SupplierRepository
import com.hb.lib.mvp.impl.lce.HBMvpLcePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class BoxByProductPresenter
@Inject constructor(
    private val supplierRepository: SupplierRepository
) : HBMvpLcePresenter<BasketByProductActivity>(), BasketByProductContract.Presenter {

    private val mData = ArrayList<DataWrapper<*>>()
    private val mDataTemp = ArrayList<DataWrapper<*>>()

    private var mPage = 1
    private var mTotalPage = 0
    private var mTotal = 0

    override fun getProduct(): SaleOrderProduct {
        return dataManager<DataManager>().getSaleOrderProduct()!!
    }

    override fun loadData(pullToRefresh: Boolean) {
        if (pullToRefresh) {
            mDataTemp.clear()
            mDataTemp.addAll(mData)
            mData.clear()
            if (isViewAttached()) {
                getView().setData(mDataTemp)
            }
            mPage = 1
        }

        if (isViewAttached()) {
            if (mPage == 1) {
                getView().showLoading(pullToRefresh)
            }
        }

        val product = getProduct()
        val params = TransactionParams(product.id).apply {
            page = mPage
        }

        disposable.clear()
        disposable.addAll(
            supplierRepository.getTransaction(params = params)
                .map {
                    if (it.success) {
                        mTotalPage = it.totalPage
                        mTotal = it.total
                        return@map it.listData.map { tr ->
                            object : DataWrapper<Any>(tr) {
                                override fun getTitle(): String {
                                    return ""
                                }

                                override fun getSubtitle(): String {
                                    return ""
                                }

                                override fun getDescription(): String {
                                    return ""
                                }

                                override fun getIcon(): String {
                                    return ""
                                }
                            }
                        }

                    }
                    it
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it is ObjectResponse) {
                        if (isViewAttached()) {
                            getView().showError(Throwable(it.detail), pullToRefresh)
                        }
                    } else if (it is List<*>) {
                        mData.addAll(it as List<DataWrapper<*>>)
                        if (isViewAttached()) {
                            getView().apply {
                                setData(mData)
                                showContent()
                                setTotal(mTotal)
                            }
                        }
                    }
                }, { error ->
                    if (isViewAttached()) {
                        getView().showError(error, pullToRefresh)
                    }
                })
        )
    }

    override fun loadNextPage() {
        if (mPage >= mTotalPage)
            return
        mPage++
        loadData(pullToRefresh = false)
    }

    override fun insertBasketList(number: Int) {

        val product = getProduct()
        val data = dataManager<DataManager>().getBarcodeList()
        disposable.add(Observable.fromIterable(data.toList())
            .flatMap {
                val params = BasketByProductParams(
                    saleOrderId = product.id,
                    basketId = it.toInt(),
                    quantity = number
                )
                supplierRepository.insertBasket(params)
            }
            .map {
                if (it.success) {
                    val dataMap = object : DataWrapper<BasketScannerResponse>(it) {
                        override fun getTitle(): String {
                            return "${getData().basketParams!!.basketId}"
                        }

                        override fun getSubtitle(): String {
                            return ""
                        }

                        override fun getDescription(): String {

                            return "${getData().basketParams!!.quantity}"
                        }

                        override fun getIcon(): String {
                            return ""
                        }
                    }
                    return@map dataMap
                }
                it
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it is DataWrapper<*>) {
                    mData.add(it)
                    if (isViewAttached()) {
                        getView().setData(mData)
                        getView().showContent()
                    }
                }
            }, {
                if (isViewAttached()) {
                    getView().showError(it, false)
                }
            })
        )
    }

    override fun deleteBasket(position: Int, data: DataWrapper<*>) {
        val basket = data.getData() as BasketScannerResponse
        val params = BasketByProductParams().apply {
            transactionId = basket.transactionId
        }
        disposable.add(
            supplierRepository.deleteBasket(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (isViewAttached()) {
                        getView().deleteCompleted(position)
                    }
                }, {
                    if (isViewAttached()) {
                        getView().deleteFailed(position, data)
                        getView().showErrorDialog(it.message!!, View.OnClickListener {

                        })
                    }
                })
        )
    }

    override fun updateBasket(number: Int, position: Int, data: DataWrapper<*>) {
        val basket = data.getData() as BasketScannerResponse
        val params = BasketByProductParams(quantity = number).apply {
            transactionId = basket.transactionId
        }
        disposable.add(
            supplierRepository.updateBasket(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (isViewAttached()) {
                        basket.basketParams?.quantity = number
                        getView().updateBasketCompleted(position, data)
                    }
                }, {
                    if (isViewAttached()) {
                        getView().showErrorDialog(it.message!!, View.OnClickListener {

                        })
                    }
                })
        )
    }
}