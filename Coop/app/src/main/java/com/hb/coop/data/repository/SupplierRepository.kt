package com.hb.coop.data.repository

import com.hb.coop.data.api.request.PageParams
import com.hb.coop.data.api.request.coop.BasketByProductParams
import com.hb.coop.data.api.request.coop.TransactionByTransportParams
import com.hb.coop.data.api.request.coop.TransactionParams
import com.hb.coop.data.api.request.coop.VehicleParams
import com.hb.coop.data.api.response.ObjectResponse
import com.hb.coop.data.api.response.coop.BasketScannerResponse
import com.hb.coop.data.api.response.coop.SaleOrderProductResponse
import com.hb.coop.data.api.response.coop.TransactionResponse
import com.hb.coop.data.api.response.coop.VehicleResponse
import io.reactivex.Observable

interface SupplierRepository {

    fun getAllProducts(params: PageParams) : Observable<SaleOrderProductResponse>

    fun getTransaction(params: TransactionParams) : Observable<TransactionResponse>

    fun insertBasket(params: BasketByProductParams): Observable<BasketScannerResponse>

    fun deleteBasket(params: BasketByProductParams): Observable<ObjectResponse>

    fun updateBasket(params: BasketByProductParams): Observable<ObjectResponse>

    fun getVehicle(params: VehicleParams): Observable<VehicleResponse>

    fun getTransactionByTransport(params: TransactionByTransportParams): Observable<TransactionResponse>
}