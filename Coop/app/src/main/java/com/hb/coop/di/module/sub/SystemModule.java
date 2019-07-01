package com.hb.coop.di.module.sub;


import com.hb.coop.data.DataManager;
import com.hb.coop.data.repository.SystemRepository;
import com.hb.coop.data.store.system.SystemLocalStorage;
import com.hb.coop.data.store.system.SystemRepositoryImpl;
import com.hb.coop.data.store.system.SystemStore;
import com.hb.coop.di.scope.CustomScope;
import com.hb.lib.data.IDataManager;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by buihai on 8/8/17.
 */
@Module
public class SystemModule {


    @Provides
    @CustomScope
    SystemStore.LocalStorage provideLocalStorage(IDataManager dm) {
        return new SystemLocalStorage((DataManager) dm);
    }

    @Provides
    @CustomScope
    SystemStore.RequestService provideRequestService(Retrofit retrofit) {
        return retrofit.create(SystemStore.RequestService.class);
    }

    @Provides
    @CustomScope
    SystemRepository provideRepository(SystemStore.LocalStorage storage,
                                       SystemStore.RequestService service) {
        return new SystemRepositoryImpl(storage, service);
    }
}
