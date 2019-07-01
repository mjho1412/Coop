package com.hb.coop.common

import com.hb.coop.data.entity.Category
import com.hb.coop.data.entity.DataWrapper
import com.hb.coop.ui.supplier.ProductWrapper
import com.hb.coop.ui.test.order.model.StringDataWrapper

object DataProvider {
    fun getData(): List<DataWrapper<String>> {
        val data = ArrayList<DataWrapper<String>>(10)
        for (i in 0..9) {
            data.add(StringDataWrapper("Position $i"))
        }
        return data
    }

    fun getAllProductsByProvider(): List<ProductWrapper> {
        val data = ArrayList<ProductWrapper>()
//        PRODUCT_BY_PROVIDER.forEach {
//            val pw = ProductWrapper(it)
//            data.add(pw)
//        }
        return data
    }

    fun getAllCategory(): List<DataWrapper<Category>> {
        val data = ArrayList<DataWrapper<Category>>()
        ALL_CATEGORIES.forEach {
            val item = object: DataWrapper<Category>(it) {
                override fun getTitle(): String {
                    return getData().name
                }

                override fun getSubtitle(): String {
                    return getData().name
                }

                override fun getDescription(): String {
                    return getData().icon
                }

                override fun getIcon(): String {
                    return getData().icon
                }
            }
            data.add(item)
        }
        return data
    }


    private val ALL_CATEGORIES = arrayOf(
        Category(1, "Rau - Củ - Quả", ""),
        Category(2, "Thủy Hải Sản", ""),
        Category(3, "Trái Cây", ""),
        Category(4, "Thịt - Trứng", "")
    )

}