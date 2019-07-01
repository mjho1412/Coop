package com.hb.coop.data.repository

import com.hb.coop.data.api.request.PageParams
import com.hb.coop.data.api.response.coop.BasketListResponse
import com.hb.coop.data.entity.AppVersion
import io.reactivex.Observable

/**
 * Created by buihai on 9/9/17.
 */

interface SystemRepository {

    fun getAppVersion(): Observable<AppVersion>

    fun getBasket(params: PageParams): Observable<BasketListResponse>

    fun addBasket(params: Any): Observable<Any>

}
