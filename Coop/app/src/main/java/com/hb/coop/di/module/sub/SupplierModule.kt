package com.hb.coop.di.module.sub

import com.hb.coop.data.DataManager
import com.hb.coop.data.repository.SupplierRepository
import com.hb.coop.data.store.supplier.SupplierLocalStorage
import com.hb.coop.data.store.supplier.SupplierRepositoryImpl
import com.hb.coop.data.store.supplier.SupplierStore
import com.hb.coop.di.scope.CustomScope
import com.hb.lib.data.IDataManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class SupplierModule {

    @Provides
    @CustomScope
    fun provideLocalStorage(dm: IDataManager): SupplierStore.LocalStorage {
        return SupplierLocalStorage(dm as DataManager)
    }

    @Provides
    @CustomScope
    fun provideRequestService(retrofitSystem: Retrofit): SupplierStore.RequestService {
        return retrofitSystem.create(SupplierStore.RequestService::class.java)
    }

    @Provides
    @CustomScope
    fun provideRepository(
        storage: SupplierStore.LocalStorage,
        service: SupplierStore.RequestService
    ): SupplierRepository {
        return SupplierRepositoryImpl(storage, service)
    }
}
