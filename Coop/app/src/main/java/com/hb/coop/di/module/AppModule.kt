package com.hb.coop.di.module

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.hb.coop.data.AppDataManager
import com.hb.coop.data.cache.AppCache
import com.hb.coop.data.cache.ICache
import com.hb.coop.data.pref.AppPreferenceHelper
import com.hb.coop.utils.image.ImageUpload
import com.hb.coop.utils.image.ImageUploadHelper
import com.hb.lib.data.IDataManager
import com.hb.lib.utils.RxBus
import com.hb.travinh.data.pref.PreferenceHelper
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Retrofit
import java.io.File
import javax.inject.Singleton


@Module
class AppModule(val app: Application) {

    @Singleton
    @Provides
    fun providesContext(): Context = app

    @Singleton
    @Provides
    fun providesCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Singleton
    @Provides
    fun providesCacheDir(): File = app.cacheDir

    @Singleton
    @Provides
    fun providesRxBus(): RxBus = RxBus()

    @Singleton
    @Provides
    fun providesCache(context: Context): ICache = AppCache(context)

    @Singleton
    @Provides
    fun providesPreferences(context: Context, gson: Gson): PreferenceHelper = AppPreferenceHelper(context, gson)

    @Singleton
    @Provides
    fun providesDataManager(context: Context, preferenceHelper: PreferenceHelper, cache: ICache): IDataManager =
        AppDataManager(context, preferenceHelper, cache)

    @Singleton
    @Provides
    fun providesImageUpload(retrofit: Retrofit): ImageUpload = ImageUploadHelper(retrofit)

}