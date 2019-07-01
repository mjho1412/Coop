package com.hb.coop.ui.supplier

import com.hb.coop.data.entity.DataWrapper
import com.hb.coop.data.entity.Packing
import com.hb.coop.data.entity.Product
import com.hb.coop.data.entity.SaleOrderProduct

class ProductWrapper(product: SaleOrderProduct) : DataWrapper<SaleOrderProduct>(product) {

    override fun getTitle(): String {
        return product.name.trim()
    }

    override fun getSubtitle(): String {
        return product.category.trim()

    }

    override fun getDescription(): String {
        return "- <b>Quy cách:</b> ${packing.packingValue} ${packing.packingUnit}<br>" +
                "- <b>SKU:</b> ${product.sku}"
    }

    override fun getIcon(): String {
        return "${getData().quantity}"
    }

    val product: Product
        get() = getData().product

    val packing: Packing
        get() = getData().packing
}