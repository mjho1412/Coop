package com.hb.coop.di.module

import com.hb.coop.di.module.sub.PassportModule
import com.hb.coop.di.module.sub.SuperMarketModule
import com.hb.coop.di.module.sub.SupplierModule
import com.hb.coop.di.module.sub.SystemModule
import com.hb.coop.di.scope.CustomScope
import com.hb.coop.ui.home.HomeFragment
import com.hb.coop.ui.main.MainActivity
import com.hb.coop.ui.map.MapFragment
import com.hb.coop.ui.passport.PassportActivity
import com.hb.coop.ui.scanner.ScannerActivity
import com.hb.coop.ui.splash.SplashActivity
import com.hb.coop.ui.supplier.SupplierActivity
import com.hb.coop.ui.supplier.basket.BasketByProductActivity
import com.hb.coop.ui.system.basket.BasketActivity
import com.hb.coop.ui.test.ja.PassportXActivity
import com.hb.coop.ui.test.order.OrderFragment
import com.hb.coop.ui.test.order.detail.OrderDetailsFragment
import com.hb.coop.ui.test.product.ProductActivity
import com.hb.coop.ui.vehicle.VehicleActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class BuildersModule {

    @CustomScope
    @ContributesAndroidInjector(modules = [PassportModule::class, SystemModule::class])
    abstract fun contributeSplashActivity(): SplashActivity

    @CustomScope
    @ContributesAndroidInjector(modules = [PassportModule::class, SystemModule::class])
    abstract fun contributePassportActivity(): PassportActivity

    @CustomScope
    @ContributesAndroidInjector(modules = [])
    abstract fun contributeMainActivity(): MainActivity

    @CustomScope
    @ContributesAndroidInjector(modules = [PassportModule::class, SystemModule::class])
    abstract fun contributeMapFragment(): MapFragment

    @CustomScope
    @ContributesAndroidInjector(modules = [PassportModule::class, SystemModule::class])
    abstract fun contributeHomeFragment(): HomeFragment

    @CustomScope
    @ContributesAndroidInjector(modules = [])
    abstract fun contributeProductActivity(): ProductActivity

    @CustomScope
    @ContributesAndroidInjector(modules = [])
    abstract fun contributeScannerActivity(): ScannerActivity

    @CustomScope
    @ContributesAndroidInjector(modules = [])
    abstract fun contributeOrderFragment(): OrderFragment

    @CustomScope
    @ContributesAndroidInjector(modules = [])
    abstract fun contributeOrderDetailsFragment(): OrderDetailsFragment

    @CustomScope
    @ContributesAndroidInjector(modules = [SupplierModule::class])
    abstract fun contributeSupplierActivity(): SupplierActivity

    @CustomScope
    @ContributesAndroidInjector(modules = [SupplierModule::class])
    abstract fun contributeBoxByProductActivity(): BasketByProductActivity

    @CustomScope
    @ContributesAndroidInjector(modules = [SupplierModule::class])
    abstract fun contributeVehicleActivity(): VehicleActivity

    @CustomScope
    @ContributesAndroidInjector(modules = [SystemModule::class])
    abstract fun contributeBasketActivity(): BasketActivity

//    @CustomScope
//    @ContributesAndroidInjector(modules =[SuperMarketModule::class])
//    abstract fun contribute<NAME_ACIVITY>(): <NAME_ACTIVITY>

    @CustomScope
    @ContributesAndroidInjector(modules = [SuperMarketModule::class])
    abstract fun contributePassportXActivity(): PassportXActivity
}