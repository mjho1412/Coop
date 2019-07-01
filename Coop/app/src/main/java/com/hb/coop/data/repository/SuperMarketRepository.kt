package com.hb.coop.data.repository

import io.reactivex.Observable


interface SuperMarketRepository {

    fun getSuperMarket(page: Int): Observable<Any>
}
