package com.hb.coop.ui.test.order.model

import com.hb.coop.data.entity.DataWrapper

class StringDataWrapper(name: String) : DataWrapper<String>(name) {
    override fun getTitle(): String {
        return "Title: ${getData()}"
    }

    override fun getSubtitle(): String {
        return "Subtitle: ${getData()}"
    }

    override fun getDescription(): String {
        return "Description: ${getData()}"
    }

    override fun getIcon(): String {
        return "Icon ${getData()}"
    }
}