package com.psylabs.psychotests.di.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.psylabs.psychotests.service.ResourceManager;
import com.psylabs.psychotests.service.ResourceManagerImpl;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Andrew on 30.03.2017.
 */
@Module
public class QuizModule {

    @Provides
    @NonNull
    @Singleton
    public ResourceManager provideResourceManager(Context context, SharedPreferences prefs) {
        return new ResourceManagerImpl(context, prefs);
    }

}
