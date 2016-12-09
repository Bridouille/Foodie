package com.bridou_n.foodie;

import android.app.Application;

import com.bridou_n.foodie.DI.components.AppComponent;
import com.bridou_n.foodie.DI.components.DaggerAppComponent;
import com.bridou_n.foodie.DI.modules.ApiModule;
import com.bridou_n.foodie.DI.modules.ContextModule;
import com.bridou_n.foodie.DI.modules.DatabaseModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by bridou_n on 09/12/2016.
 */

public class AppSingleton extends Application {

    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);

        appComponent = DaggerAppComponent.builder()
                .contextModule(new ContextModule(this))
                .databaseModule(new DatabaseModule())
                .apiModule(new ApiModule())
                .build();
    }

    public static AppComponent appComponent() {
        return appComponent;
    }
}
