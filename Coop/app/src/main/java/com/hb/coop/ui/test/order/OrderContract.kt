package com.hb.coop.ui.test.order

import com.hb.coop.data.entity.DataWrapper

interface OrderContract {
    interface View {
        fun showLoading()

        fun showError(error: Throwable)

        fun setData(data: List<DataWrapper<String>>)

        fun showContent()
    }

    interface Presenter {
        fun loadData()
    }

}