package com.hb.coop.app;

import androidx.annotation.Keep;
import com.crashlytics.android.Crashlytics;
import com.hb.coop.BuildConfig;
import com.hb.coop.di.component.AppComponent;
import com.hb.coop.di.component.DaggerAppComponent;
import com.hb.coop.di.module.AppModule;
import com.hb.coop.utils.image.GlideImageHelper;
import com.hb.coop.utils.image.ImageHelper;
import com.hb.lib.app.HBMvpApp;
import com.mapbox.mapboxsdk.Mapbox;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class App extends HBMvpApp {

    static {
        System.loadLibrary("native-lib");
    }

    @Keep
    native String getMapboxToken();

    @Keep
    native void setKey(String key);

    public static ImageHelper imageHelper;


    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    protected void init() {
        setKey("HAIBUI!@123456");


        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        initAllComponent();

        imageHelper = new GlideImageHelper(getBaseContext());

        String mapboxToken = getMapboxToken();
        Mapbox.getInstance(getApplicationContext(), mapboxToken);

        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)  // Enables Crashlytics debugger
                .build();
        Fabric.with(fabric);

    }

    public void initAllComponent() {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        mAppComponent.inject(this);
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
