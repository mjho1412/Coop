package com.hb.coop.ui.supplier

import com.hb.coop.data.DataManager
import com.hb.coop.data.api.request.coop.OrderBySupplierParams
import com.hb.coop.data.api.response.ObjectResponse
import com.hb.coop.data.entity.SaleOrderProduct
import com.hb.coop.data.repository.SupplierRepository
import com.hb.lib.mvp.impl.lce.HBMvpLcePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SupplierPresenter
@Inject constructor(
    private val supplierRepository: SupplierRepository
) : HBMvpLcePresenter<SupplierActivity>(), SupplierContract.Presenter {

    private val mData = ArrayList<ProductWrapper>()
    private val mDataTemp = ArrayList<ProductWrapper>()

    private var mPage = 1
    private var mTotalPage = 0
    private var mTotal = 0

    override fun setProduct(product: SaleOrderProduct) {
        dataManager<DataManager>().setSaleOrderProduct(product)
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

        val passport = dataManager<DataManager>().getPassport()!!
        val params = OrderBySupplierParams(passport.ruleId).apply {
            page = mPage
        }

        disposable.clear()
        disposable.addAll(
            supplierRepository.getAllProducts(params)
                .map {
                    if (it.success) {
                        mTotalPage = it.totalPage
                        mTotal = it.total
                        return@map it.listData.map { sop ->
                            ProductWrapper(sop)
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
                        mData.addAll(it as List<ProductWrapper>)
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

}