package com.psylabs.psychotests;

import android.app.Application;

import com.psylabs.psychotests.di.AppComponent;
import com.psylabs.psychotests.di.DaggerAppComponent;
import com.psylabs.psychotests.di.module.AppModule;

/**
 * Created by Andrew on 30.03.2017.
 */
//play store APP ID: ca-app-pub-7140137638332507~2688118657
public class App extends Application {
    private static AppComponent component;

    public static AppComponent getComponent() {
        return component;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        component = buildComponent();
    }

    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }
}
