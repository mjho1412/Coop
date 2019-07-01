package com.hb.coop.ui.home

import com.hb.coop.R
import com.hb.coop.data.DataManager
import com.hb.coop.navigation.Navigator
import com.hb.lib.mvp.impl.lce.HBMvpLcePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HomePresenter
@Inject constructor(

) : HBMvpLcePresenter<HomeFragment>(), HomeContract.Presenter {

    override fun loadData(pullToRefresh: Boolean) {
        if (isViewAttached()) {
            getView().showLoading(pullToRefresh)
        }

        val passport = dataManager<DataManager>().getPassport()!!
        val rule = passport.rule.toLowerCase()

        disposable.clear()
        disposable.addAll(
            Observable.create<List<ActionObject>> {
                it.onNext(listOf(
                    ActionObject(
                        icon = R.drawable.ic_newspaper_black,
                        title = "Tin Tức",
                        listener = object : ActionObject.OnClickListener {
                            override fun onClick() {
                                if (isViewAttached()) {
                                }
                            }
                        }
                    ),
                    ActionObject(
                        icon = R.drawable.ic_order,
                        title = "Đơn Hàng",
                        listener = object : ActionObject.OnClickListener {
                            override fun onClick() {
                                if (isViewAttached()) {
                                    Navigator.startProduct(getView().activity!!)
                                }
                            }
                        }
                    ),
                    ActionObject(
                        icon = R.drawable.ic_transport,
                        title = "Vận Chuyển",
                        listener = object : ActionObject.OnClickListener {
                            override fun onClick() {
                                if (isViewAttached()) {
                                    Navigator.startVehicle(getView().activity!!)
                                }
                            }
                        }
                    ),
                    ActionObject(
                        icon = R.drawable.ic_supermarket,
                        title = "Siêu Thị",
                        listener = object : ActionObject.OnClickListener {
                            override fun onClick() {

                            }
                        }
                    ),
                    ActionObject(
                        icon = R.drawable.ic_house,
                        title = "Nhà Cung Cấp",
                        listener = object : ActionObject.OnClickListener {
                            override fun onClick() {
                                if (isViewAttached()) {
                                    Navigator.startProvider(getView().activity!!)
                                }
                            }
                        }
                    ),
                    ActionObject(
                        icon = R.drawable.ic_stock,
                        title = "Nhà Kho",
                        listener = object : ActionObject.OnClickListener {
                            override fun onClick() {

                            }
                        }
                    ),
                    ActionObject(
                        isShowed = false
                    ),
                    ActionObject(
                        icon = R.drawable.ic_basket,
                        title = "Sọt Hàng",
                        listener = object : ActionObject.OnClickListener {
                            override fun onClick() {
                                Navigator.startBasket(getView().activity!!)
                            }
                        }
                    )
                ))
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (isViewAttached()) {
                        getView().apply {
                            setData(it)
                            showContent()
                        }
                    }
                }, {
                    if (isViewAttached()) {
                        getView().showError(it, pullToRefresh)
                    }
                })
        )
    }
}