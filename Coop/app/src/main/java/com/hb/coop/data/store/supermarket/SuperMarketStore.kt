package com.hb.coop.data.store.supermarket

import com.hb.coop.data.api.request.PageParams
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface SuperMarketStore {

    interface LocalStorage {
    }

    interface RequestService {
        @POST("query/sale_order/branch")
        fun getMarket(@Body params: PageParams): Observable<Any>
    }
}