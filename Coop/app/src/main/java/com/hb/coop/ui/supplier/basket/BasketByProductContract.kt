package com.hb.coop.ui.supplier.basket

import com.hb.coop.data.entity.DataWrapper
import com.hb.coop.data.entity.SaleOrderProduct

interface BasketByProductContract {

    interface View {
        fun setTotal(total: Int)

        fun deleteCompleted(position: Int)

        fun deleteFailed(position: Int, data: DataWrapper<*>)

        fun showBottomSheetDialog(updated: Boolean = false, position: Int = -1, data: DataWrapper<*>? = null)

        fun updateBasketCompleted(position: Int, data: DataWrapper<*>)
    }

    interface Presenter {
        fun getProduct(): SaleOrderProduct

        fun loadNextPage()

        fun insertBasketList(number: Int)

        fun updateBasket(number: Int, position: Int, data: DataWrapper<*>)

        fun deleteBasket(position: Int, data: DataWrapper<*>)

    }

    companion object {
        const val EXTRA_POSITION = "EXTRA_POSITION"

        const val REQUEST_CODE_SCANNER = 0
    }

}