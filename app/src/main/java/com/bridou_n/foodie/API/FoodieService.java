package com.bridou_n.foodie.API;

import com.bridou_n.foodie.models.SearchFoodResponse;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by bridou_n on 09/12/2016.
 */

public interface FoodieService {

    // TODO: 09/12/2016 change language based on phone locale ?

    @GET("icebox/v1/foods/fr/se/{search}")
    Observable<SearchFoodResponse> getFoodList(@Path("search") String s);
}
