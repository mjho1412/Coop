package com.hb.coop.ui.map

import com.hb.lib.map.screen.HBMapFragment

class MapFragment : HBMapFragment<MapPresenter>(), MapContract.View {

    override fun getToken(): String {
        return mPresenter.getToken()
    }
}