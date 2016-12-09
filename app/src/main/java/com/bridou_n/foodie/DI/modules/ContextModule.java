package com.bridou_n.foodie.DI.modules;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by bridou_n on 09/12/2016.
 */

@Module
public class ContextModule {

    private final Application context;

    public ContextModule(Application app) {
        context = app;
    }

    @Provides @Singleton
    public Context providesContext() {
        return context;
    }
}
