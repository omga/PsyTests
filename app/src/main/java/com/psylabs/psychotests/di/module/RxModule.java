package com.psylabs.psychotests.di.module;

import android.support.annotation.NonNull;

import com.psylabs.psychotests.service.RxBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Andrew on 08.04.2017.
 */
@Module
public class RxModule {

    @Provides
    @NonNull
    @Singleton
    public RxBus provideRxBus() {
        return new RxBus();
    }
}
