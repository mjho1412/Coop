package com.hb.coop.di.module.sub;


import com.hb.coop.data.DataManager;
import com.hb.coop.data.repository.PassportRepository;
import com.hb.coop.data.store.passport.PassportLocalStorage;
import com.hb.coop.data.store.passport.PassportRepositoryImpl;
import com.hb.coop.data.store.passport.PassportStore;
import com.hb.coop.di.scope.CustomScope;
import com.hb.lib.data.IDataManager;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by buihai on 8/8/17.
 */
@Module
public class PassportModule {


    @Provides
    @CustomScope
    PassportStore.LocalStorage provideLocalStorage(IDataManager dm) {
        return new PassportLocalStorage((DataManager) dm);
    }

    @Provides
    @CustomScope
    PassportStore.RequestService provideRequestService(Retrofit retrofit) {
        return retrofit.create(PassportStore.RequestService.class);
    }

    @Provides
    @CustomScope
    PassportRepository provideRepository(PassportStore.LocalStorage storage,
                                         PassportStore.RequestService service) {
        return new PassportRepositoryImpl(storage, service);
    }
}
