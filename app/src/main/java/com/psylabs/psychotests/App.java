package com.psylabs.psychotests;

import android.app.Application;

import com.psylabs.psychotests.di.AppComponent;
import com.psylabs.psychotests.di.module.AppModule;
import com.psylabs.psychotests.di.DaggerAppComponent;

/**
 * Created by Andrew on 30.03.2017.
 */

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
