package com.bridou_n.foodie.DI.components;

import android.content.Context;

import com.bridou_n.foodie.API.FoodieService;
import com.bridou_n.foodie.DI.modules.ApiModule;
import com.bridou_n.foodie.DI.modules.ContextModule;
import com.bridou_n.foodie.DI.modules.DatabaseModule;
import com.bridou_n.foodie.features.foodList.FoodListActivity;

import javax.inject.Singleton;

import dagger.Component;
import io.realm.Realm;

/**
 * Created by bridou_n on 09/12/2016.
 */

@Singleton
@Component(modules = {
        ContextModule.class,
        ApiModule.class,
        DatabaseModule.class
})
public interface AppComponent {
    Context context();
    Realm realm();
    FoodieService api();
    void inject(FoodListActivity activity);
}
