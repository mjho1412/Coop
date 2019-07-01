package com.hb.coop.data.store.vietbando

import com.hb.coop.data.DataManager
import com.hb.lib.map.model.FindShortestPath

class VietbandoLocalStorage(
    private val dm: DataManager
) : VietbandoStore.LocalStorage {

    override fun clearRoute() {
    }

    override fun setRoute(route: FindShortestPath) {
    }
}