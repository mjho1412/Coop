package com.hb.coop.di.component

import android.content.Context
import com.hb.coop.app.App
import com.hb.coop.di.module.AppModule
import com.hb.coop.di.module.BuildersModule
import com.hb.coop.di.module.NetModule
import com.hb.coop.utils.image.ImageUpload
import com.hb.lib.data.IDataManager
import com.hb.lib.utils.RxBus
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import retrofit2.Retrofit
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        BuildersModule::class,
        AppModule::class,
        NetModule::class
    ]
)
interface AppComponent {
    fun inject(app: App)

    fun bus(): RxBus

    fun getImageUpload(): ImageUpload

    fun dataManager(): IDataManager

    fun context(): Context

    fun retrofit(): Retrofit

}