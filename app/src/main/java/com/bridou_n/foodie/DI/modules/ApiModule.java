package com.bridou_n.foodie.DI.modules;

import android.util.Log;

import com.bridou_n.foodie.API.FoodieService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Locale;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by bridou_n on 09/12/2016.
 */

@Module
public class ApiModule {

    @Provides @Singleton @Named("injectToken")
    public Interceptor providesInjectTokenInterceptor() {
        return chain -> {
            Request original = chain.request();

            return chain.proceed(original.newBuilder()
                    .header("Authorization", "5055538:e52b2981d949fea96d3a103643f377e1ab85c08e347e310adf5ed927831e1018")
                    .method(original.method(), original.body())
                    .build());
        };
    }

    @Provides @Singleton @Named("logging")
    public Interceptor providesLoggingInterceptor() {
        return chain -> {
            Request request = chain.request();

            // Logging what's happenning
            long t1 = System.nanoTime();
            Log.i("OKHTTP3", String.format("Sending request %s on %s%n%s%s",
                    request.url(), chain.connection(), request.headers(), request.body()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            String bodyString = response.body().string();
            Log.i("OKHTTP3", String.format(Locale.getDefault(), "Received response for %s in %.1fms%n(%s)%s", response.request().url(), (t2 - t1) / 1e6d, response.code(), bodyString));

            return response.newBuilder()
                    .body(ResponseBody.create(response.body().contentType(), bodyString))
                    .build();
        };
    }

    @Provides @Singleton
    public OkHttpClient providesFoursquareOkhttpClient(@Named("injectToken") Interceptor injectTokenInterceptor,
                                                       @Named("logging") Interceptor loggingInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(injectTokenInterceptor)
                .addInterceptor(loggingInterceptor)
                .build();
    }

    @Provides @Singleton
    public Gson providesGson() {
        return new GsonBuilder().create();
    }

    @Provides @Singleton
    public Retrofit providesRetrofit(OkHttpClient client, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl("https://api.lifesum.com/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }

    @Provides @Singleton
    public FoodieService providesFoursquareService(Retrofit retrofit) {
        return retrofit.create(FoodieService.class);
    }
}
