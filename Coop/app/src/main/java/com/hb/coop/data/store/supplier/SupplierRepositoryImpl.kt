package com.hb.coop.data.store.supplier

import com.hb.coop.data.api.request.PageParams
import com.hb.coop.data.api.request.coop.*
import com.hb.coop.data.api.response.ObjectResponse
import com.hb.coop.data.api.response.coop.BasketScannerResponse
import com.hb.coop.data.api.response.coop.SaleOrderProductResponse
import com.hb.coop.data.api.response.coop.TransactionResponse
import com.hb.coop.data.api.response.coop.VehicleResponse
import com.hb.coop.data.repository.SupplierRepository
import io.reactivex.Observable

class SupplierRepositoryImpl(
    private val storage: SupplierStore.LocalStorage,
    private val service: SupplierStore.RequestService
) : SupplierRepository {

    override fun getAllProducts(params: PageParams): Observable<SaleOrderProductResponse> {

        val passport = storage.getPassport()
        val p = if (params !is OrderBySupplierParams) {
            OrderBySupplierParams(passport.ruleId).apply {
                page = params.page
            }
        } else {
            params
        }
        return service.getAllProducts(params = p)
    }

    override fun getTransaction(params: TransactionParams): Observable<TransactionResponse> {
        return service.getTransaction(params)
    }

    override fun insertBasket(params: BasketByProductParams): Observable<BasketScannerResponse> {
        return service.insertBasket(params)
            .map {
                if (it.success) {
                    it.basketParams = params
                }
                it
            }
    }

    override fun deleteBasket(params: BasketByProductParams): Observable<ObjectResponse> {
        return service.deleteBasket(params)
    }

    override fun updateBasket(params: BasketByProductParams): Observable<ObjectResponse> {
        return service.updateBasket(params)
    }

    override fun getVehicle(params: VehicleParams): Observable<VehicleResponse> {
        return service.getVehicle(params)
    }

    override fun getTransactionByTransport(params: TransactionByTransportParams): Observable<TransactionResponse> {
        return service.getTransactionByTransport(params)
    }
}