package com.hb.coop.ui.vehicle

interface VehicleContract {
    interface View {
        fun setTotal(total: Int)
    }

    interface Presenter {
        fun loadNextPage()

        fun loadTransaction()
    }
}