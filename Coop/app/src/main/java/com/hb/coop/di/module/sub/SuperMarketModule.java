package com.hb.coop.di.module.sub;


import com.hb.coop.data.DataManager;
import com.hb.coop.data.repository.SuperMarketRepository;
import com.hb.coop.data.store.supermarket.SuperMarketLocalStorage;
import com.hb.coop.data.store.supermarket.SuperMarketRepositoryImpl;
import com.hb.coop.data.store.supermarket.SuperMarketStore;
import com.hb.coop.di.scope.CustomScope;
import com.hb.lib.data.IDataManager;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by buihai on 8/8/17.
 */
@Module
public class SuperMarketModule {


    @Provides
    @CustomScope
    SuperMarketStore.LocalStorage provideLocalStorage(IDataManager dm) {
        return new SuperMarketLocalStorage((DataManager) dm);
    }

    @Provides
    @CustomScope
    SuperMarketStore.RequestService provideRequestService(Retrofit retrofit) {
        return retrofit.create(SuperMarketStore.RequestService.class);
    }

    @Provides
    @CustomScope
    SuperMarketRepository provideRepository(SuperMarketStore.LocalStorage storage,
                                            SuperMarketStore.RequestService service) {
        return new SuperMarketRepositoryImpl(storage, service);
    }
}
