package com.hb.coop.ui.system.basket

interface BasketContract {
    interface View {
        fun setTotal(total: Int)

        fun addBasketCompleted()

        fun addBasketFailed()
    }

    interface Presenter {
        fun loadNextPage()

        fun addBasket()
    }
}