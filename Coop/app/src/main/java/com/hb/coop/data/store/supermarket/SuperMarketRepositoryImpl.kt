package com.hb.coop.data.store.supermarket

import com.hb.coop.data.api.request.PageParams
import com.hb.coop.data.repository.SuperMarketRepository
import io.reactivex.Observable

class SuperMarketRepositoryImpl(
    private val storage: SuperMarketStore.LocalStorage,
    private val service: SuperMarketStore.RequestService
) : SuperMarketRepository {


    override fun getSuperMarket(page: Int): Observable<Any> {
        val params = PageParams().apply {
            this.page = page
        }
        return service.getMarket(params)
    }
}