package com.bridou_n.foodie.features.foodList;

import com.bridou_n.foodie.API.FoodieService;
import com.bridou_n.foodie.R;
import com.bridou_n.foodie.models.Food;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by bridou_n on 09/12/2016.
 */

public class FoodListPresenter {

    private FoodListActivity view;
    private FoodieService api;

    private Subscription sub;

    FoodListPresenter(FoodListActivity view, FoodieService api) {
        this.view = view;
        this.api = api;
    }

    public void onFoodSearched(String search) {
        if (search != null && search.length() > 0) {
            searchFood(search);
        } else {
            view.showSavedFood();
        }
    }

    public void searchFood(String s) {
        if (sub != null && !sub.isUnsubscribed()) { // Cancel the previous search, if any
            sub.unsubscribe();
        }

        view.showLoading();
        sub = api.getFoodList(s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(errors -> errors.flatMap(error -> view.showError(error.getMessage()).doOnNext(e -> view.showLoading())))
                .subscribe(resp -> {
                    if (resp != null) {
                        List<Food> food = resp.getFood();

                        if (food.size() > 0) {
                            view.showSearchedFood(food);
                            return;
                        }
                    }
                    view.showEmptyState(R.string.no_food_matches_your_search);
                });
    }

    public void takeView(FoodListActivity view) {
        this.view = view;
    }

    public void dropView() {
        this.view = null;
        if (sub != null && !sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
    }
}
