package com.hb.coop.di.module.sub

import com.hb.coop.data.DataManager
import com.hb.coop.data.repository.VietbandoRepository
import com.hb.coop.data.store.vietbando.VietbandoLocalStorage
import com.hb.coop.data.store.vietbando.VietbandoRepositoryImpl
import com.hb.coop.data.store.vietbando.VietbandoStore
import com.hb.coop.di.scope.CustomScope
import com.hb.lib.data.IDataManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit


@Module
class VietbandoModule {
    @Provides
    @CustomScope
    fun provideLocalStorage(dm: IDataManager): VietbandoStore.LocalStorage {
        return VietbandoLocalStorage(dm as DataManager)
    }

    @Provides
    @CustomScope
    fun provideRequestService(retrofitSystem: Retrofit): VietbandoStore.RequestService {

//        val retrofit = retrofitSystem.newBuilder()
//                .baseUrl(BuildConfig.HOST_VBD)
//                .show()
        return retrofitSystem.create(VietbandoStore.RequestService::class.java)
    }

    @Provides
    @CustomScope
    fun provideRepository(
        storage: VietbandoStore.LocalStorage,
        service: VietbandoStore.RequestService
    ): VietbandoRepository {
        return VietbandoRepositoryImpl(storage, service)
    }
}
