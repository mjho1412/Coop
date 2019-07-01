package com.hb.coop.ui.vehicle

import com.hb.coop.data.DataManager
import com.hb.coop.data.api.request.coop.VehicleParams
import com.hb.coop.data.api.response.ObjectResponse
import com.hb.coop.data.entity.DataWrapper
import com.hb.coop.data.entity.Vehicle
import com.hb.coop.data.repository.SupplierRepository
import com.hb.lib.mvp.impl.lce.HBMvpLcePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class VehiclePresenter
@Inject constructor(
    private val supplierRepository: SupplierRepository
) : HBMvpLcePresenter<VehicleActivity>(), VehicleContract.Presenter {

    private val mData = ArrayList<DataWrapper<*>>()
    private val mDataTemp = ArrayList<DataWrapper<*>>()

    private var mPage = 1
    private var mTotalPage = 0
    private var mTotal = 0

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
        val params = VehicleParams(passport.ruleId).apply {
            page = mPage
        }

        disposable.clear()
        disposable.addAll(
            supplierRepository.getVehicle(params)
                .map {
                    if (it.success) {
                        mTotalPage = it.totalPage
                        mTotal = it.total
                        return@map it.listData.map { vehicle ->
                            object: DataWrapper<Vehicle>(vehicle) {
                                override fun getTitle(): String {
                                    return getData().plateNumber
                                }

                                override fun getSubtitle(): String {
                                    return getData().driverName
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

    override fun loadTransaction() {

    }
}