package com.hb.coop.ui.map

import com.hb.lib.mvp.impl.HBMvpPresenter
import javax.inject.Inject

class MapPresenter
@Inject constructor(

) : HBMvpPresenter<MapFragment>(), MapContract.Presenter {

    override fun getToken(): String {
        return "bbb9c9ad-00b9-4066-bc88-06b401b8eddd"
//        return dataManager<DataManager>().getToken()
    }
}