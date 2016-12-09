package com.bridou_n.foodie.DI.modules;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

/**
 * Created by bridou_n on 09/12/2016.
 */

@Module
public class DatabaseModule {
    @Provides
    public Realm providesRealm() {
        return Realm.getDefaultInstance();
    }
}
