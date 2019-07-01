package com.hb.coop.data.store.supplier

import com.hb.coop.data.DataManager
import com.hb.coop.data.entity.Passport

class SupplierLocalStorage(
    private val dm: DataManager
) : SupplierStore.LocalStorage {

    override fun getPassport(): Passport {
        return dm.getPassport()!!
    }
}