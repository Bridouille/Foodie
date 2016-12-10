package com.bridou_n.foodie.features.foodList;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bridou_n.foodie.API.FoodieService;
import com.bridou_n.foodie.AppSingleton;
import com.bridou_n.foodie.R;
import com.bridou_n.foodie.models.Food;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.jakewharton.rxbinding.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static android.view.View.GONE;

public class FoodListActivity extends AppCompatActivity {
    private static final String TAG = "FOODLIST_ACTIVITY";
    private static final String PREF_TUTO_KEY = "tutoPref";

    private FoodSavedRecyclerViewAdapter foodSavedAdapter;
    private FoodSearchedRecyclerViewAdapter foodSearchedAdapter;

    private FoodListPresenter presenter;

    @Inject Realm realm;
    @Inject FoodieService api;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rv) RecyclerView rv;
    @BindView(R.id.loading) ConstraintLayout loading;
    @BindView(R.id.empty) ConstraintLayout empty;
    @BindView(R.id.empty_content) TextView emptyContent;
    @BindView(R.id.error) ConstraintLayout error;
    @BindView(R.id.error_content) TextView errorContent;
    @BindView(R.id.retry) Button retry;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        ButterKnife.bind(this);
        AppSingleton.appComponent().inject(this);

        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.food_list_menu);

        RealmResults<Food> foodSaved = realm.where(Food.class).findAllSortedAsync(new String[]{"title"}, new Sort[]{Sort.ASCENDING});

        foodSaved.asObservable()
                .filter(r -> rv.getAdapter() == foodSavedAdapter) // We only care when we're displaying the saved food
                .subscribe(r -> {
                    if (r.size() == 0) { // Show the empty state
                        showEmptyState(R.string.you_dont_have_any_saved_food);
                    } else { // Show the list of food
                        showSavedFood();
                    }
                });

        foodSavedAdapter = new FoodSavedRecyclerViewAdapter(this, foodSaved, true, realm);

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        rv.setAdapter(foodSavedAdapter);

        presenter = new FoodListPresenter(this, api);

        if (!getPreferences(Context.MODE_PRIVATE).getBoolean(PREF_TUTO_KEY, false)) {
            showTutorial();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.takeView(this);
    }

    public void showTutorial() {
        TapTargetView.showFor(this,
                TapTarget.forToolbarMenuItem(toolbar,
                R.id.action_search_food, getString(R.string.search_food),
                getString(R.string.click_here_to_explore_an_endless_list_of_food))
                .cancelable(false)
                .dimColor(R.color.primaryText)
                .drawShadow(true));
        getPreferences(Context.MODE_PRIVATE).edit().putBoolean(PREF_TUTO_KEY, true).apply();
    }

    public void hideAll() {
        loading.setVisibility(GONE);
        rv.setVisibility(GONE);
        empty.setVisibility(GONE);
        error.setVisibility(GONE);
    }

    public void showLoading() {
        hideAll();
        loading.setVisibility(View.VISIBLE);
    }

    public void showSavedFood() {
        hideAll();

        if (foodSavedAdapter.isEmpty()) {
            showEmptyState(R.string.you_dont_have_any_saved_food);
            return ;
        }

        rv.setAdapter(foodSavedAdapter);
        rv.setVisibility(View.VISIBLE);
    }

    public void showEmptyState(int what) {
        hideAll();
        empty.setVisibility(View.VISIBLE);
        emptyContent.setText(getString(what));
    }

    public Observable<Integer> showError(String err) {
        hideAll();
        error.setVisibility(View.VISIBLE);
        errorContent.setText(err);
        return RxView.clicks(retry).map(n -> 1);
    }

    public void showSearchedFood(List<Food> list) {
        if (foodSearchedAdapter == null) {
            foodSearchedAdapter = new FoodSearchedRecyclerViewAdapter(this, list, realm);
        } else {
            foodSearchedAdapter.setData(list);
        }
        hideAll();
        rv.setAdapter(foodSearchedAdapter);
        rv.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.food_list_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_food).getActionView();

        searchView.setQueryHint(getString(R.string.search_food));

        RxSearchView.queryTextChanges(searchView) // Watch for the user's typing
                .skip(1)
                .debounce(400, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    presenter.onFoodSearched(s);
                });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.dropView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
