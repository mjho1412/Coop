package com.hb.coop.data.store.system

import com.hb.coop.data.api.request.PageParams
import com.hb.coop.data.api.response.coop.BasketListResponse
import com.hb.coop.data.entity.AppVersion
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by buihai on 9/9/17.
 */

interface SystemStore {

    companion object {
        const val VERSION = "v1"
    }

    interface LocalStorage {
    }

    interface RequestService {

        @POST("$VERSION/static/version_android")
        fun getAppVersion(): Observable<AppVersion>

        @POST("query/basket/list")
        fun getBasketList(@Body params: PageParams): Observable<BasketListResponse>

        @POST("query/basket/insert")
        fun insertBasket(@Body params: Any): Observable<Any>

    }
}
