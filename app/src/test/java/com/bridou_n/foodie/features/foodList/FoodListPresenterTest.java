package com.bridou_n.foodie.features.foodList;

import com.bridou_n.foodie.API.FoodieService;
import com.bridou_n.foodie.R;
import com.bridou_n.foodie.models.Food;
import com.bridou_n.foodie.models.SearchFoodResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by bridou_n on 10/12/2016.
 */

@RunWith(PowerMockRunner.class)
public class FoodListPresenterTest {

    private FoodListPresenter presenter;

    @Mock private FoodListActivity view;
    @Mock private FoodieService api;
    @Mock private SearchFoodResponse foodResponse;
    @Mock private Food foodItem;

    private String mySearch = "Apple";
    private List<Food> foodList = Arrays.asList(foodItem, foodItem);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // Mock the android main scheduler
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });

        // Mock the io() scheduler
        RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
            @Override
            public Scheduler getIOScheduler() {
                return Schedulers.immediate();
            }
        });

        presenter = new FoodListPresenter(view, api);
    }

    @After
    public void tearDown() {
        // Reset the schedulers hooks
        RxAndroidPlugins.getInstance().reset();
        RxJavaPlugins.getInstance().reset();
    }

    @Test
    public void dropViewWithoutViewTest() {
        presenter.takeView(null);
        presenter.dropView(); // shouldn't crash
    }

    @Test
    public void onFoodSearchedWithoutViewTest() {
        presenter.takeView(null);
        presenter.onFoodSearched(anyString()); // shouldn't crash
    }

    @Test
    public void searchFoodWithoutViewTest() {
        presenter.takeView(null);
        presenter.onFoodSearched(anyString());  // shouldn't crash
    }

    @Test
    public void onFoodSearchedWithEmptyOrNullTest() {
        // If the search is null, we should display the saved results

        presenter.onFoodSearched(null);
        presenter.onFoodSearched("");

        verify(view, times(2)).showSavedFood();
    }

    @Test
    public void onFoodSearchedWithRegularSearchTest() {
        when(api.getFoodList(mySearch)).thenReturn(Observable.just(foodResponse));
        when(foodResponse.getFood()).thenReturn(foodList);

        presenter.onFoodSearched(mySearch);
        verify(view).showLoading();
        verify(api).getFoodList(mySearch);
        verify(view).showSearchedFood(foodList);
    }

    @Test
    public void searchFoodWithNoResults() {
        when(api.getFoodList(mySearch)).thenReturn(Observable.just(foodResponse));
        when(foodResponse.getFood()).thenReturn(null); // The food property is not present

        presenter.onFoodSearched(mySearch);

        when(api.getFoodList(mySearch)).thenReturn(Observable.just(foodResponse));
        when(foodResponse.getFood()).thenReturn(new ArrayList<>()); // The food property is an empty list

        presenter.onFoodSearched(mySearch);

        when(api.getFoodList(mySearch)).thenReturn(Observable.just(null)); // We don't have any foodResponse from the api (204)

        presenter.onFoodSearched(mySearch);

        verify(view, times(3)).showLoading();
        verify(api, times(3)).getFoodList(mySearch);
        verify(view, times(3)).showEmptyState(R.string.no_food_matches_your_search);
    }

    @Test
    public void searchFoodWithError() {
        when(api.getFoodList(mySearch)).thenReturn(Observable.error(new IOException("I'm in the metro"))); // There is an error while searching food
        when(view.showError(anyString())).thenReturn(Observable.empty());

        presenter.onFoodSearched(mySearch);

        verify(view).showLoading();
        verify(api).getFoodList(mySearch);
        verify(view).showError("I'm in the metro");
    }
}
