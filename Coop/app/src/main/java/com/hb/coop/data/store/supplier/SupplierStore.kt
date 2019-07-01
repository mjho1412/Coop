package com.hb.coop.data.store.supplier

import com.hb.coop.data.api.request.coop.*
import com.hb.coop.data.api.response.ObjectResponse
import com.hb.coop.data.api.response.coop.BasketScannerResponse
import com.hb.coop.data.api.response.coop.SaleOrderProductResponse
import com.hb.coop.data.api.response.coop.TransactionResponse
import com.hb.coop.data.api.response.coop.VehicleResponse
import com.hb.coop.data.entity.Passport
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface SupplierStore {

    companion object {
        const val VERSION = "v1"
    }

    interface LocalStorage {
        fun getPassport(): Passport
    }

    interface RequestService {

        @POST("query/supplier/list")
        fun getAllProducts(@Body params: OrderBySupplierParams): Observable<SaleOrderProductResponse>

        @POST("query/supplier/transaction/list")
        fun getTransaction(@Body params: TransactionParams): Observable<TransactionResponse>

        @POST("query/supplier/transaction/insert")
        fun insertBasket(@Body params: BasketByProductParams): Observable<BasketScannerResponse>

        @POST("query/supplier/transaction/delete")
        fun deleteBasket(@Body params: BasketByProductParams): Observable<ObjectResponse>

        @POST("query/supplier/transaction/edit")
        fun updateBasket(@Body params: BasketByProductParams): Observable<ObjectResponse>

        @POST("query/supplier/vehicle/list")
        fun getVehicle(@Body params: VehicleParams): Observable<VehicleResponse>

        @POST("query/supplier/vehicle/transport/transaction")
        fun getTransactionByTransport(@Body params: TransactionByTransportParams): Observable<TransactionResponse>

    }
}