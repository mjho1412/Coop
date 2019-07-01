package com.hb.coop.data.store.system


import com.hb.coop.data.api.request.PageParams
import com.hb.coop.data.api.response.coop.BasketListResponse
import com.hb.coop.data.entity.AppVersion
import com.hb.coop.data.repository.SystemRepository
import io.reactivex.Observable

/**
 * Created by buihai on 9/9/17.
 */

class SystemRepositoryImpl(
    private val storage: SystemStore.LocalStorage,
    private val service: SystemStore.RequestService
) : SystemRepository {

    override fun getAppVersion(): Observable<AppVersion> {
        return Observable.just(
            AppVersion(
                version = "",
                forceFlag = false,
                googlePlay = "",
                versionApp = ""

            ).apply {
                success = false
            }
        )
    }

    override fun getBasket(params: PageParams): Observable<BasketListResponse> {
        return service.getBasketList(params)
    }

    override fun addBasket(params: Any): Observable<Any> {
        return service.insertBasket(params)
    }
}
