package com.hb.coop.data.repository

import com.hb.coop.data.api.response.ObjectResponse
import com.hb.coop.data.entity.Passport
import io.reactivex.Observable

/**
 * Created by buihai on 9/9/17.
 */

interface PassportRepository {

    fun login(user: String, pass: String): Observable<Passport>

    fun reLogin(): Observable<ObjectResponse>

}
