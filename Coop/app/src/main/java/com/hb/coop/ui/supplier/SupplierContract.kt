package com.hb.coop.ui.supplier

import com.hb.coop.data.entity.SaleOrderProduct

interface SupplierContract {
    interface View {
        fun setTotal(total: Int)
    }

    interface Presenter {
        fun setProduct(product: SaleOrderProduct)

        fun loadNextPage()
    }
}