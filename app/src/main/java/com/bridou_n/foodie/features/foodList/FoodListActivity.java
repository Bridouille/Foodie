package com.bridou_n.foodie.features.foodList;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bridou_n.foodie.API.FoodieService;
import com.bridou_n.foodie.AppSingleton;
import com.bridou_n.foodie.R;
import com.bridou_n.foodie.models.Food;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class FoodListActivity extends AppCompatActivity {
    private static final String TAG = "FOODLIST_ACTIVITY";

    @Inject Realm realm;
    @Inject FoodieService api;

    @BindView(R.id.rv) RecyclerView rv;
    @BindView(R.id.loading) ConstraintLayout loading;
    @BindView(R.id.empty) ConstraintLayout empty;
    @BindView(R.id.empty_content) TextView emptyContent;
    @BindView(R.id.error) ConstraintLayout error;
    @BindView(R.id.error_content) TextView errorContent;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        ButterKnife.bind(this);
        AppSingleton.appComponent().inject(this);

        RealmResults<Food> foodSaved = realm.where(Food.class).findAllAsync();

        foodSaved.addChangeListener(results -> { // TODO: 09/12/2016 verify this with both empty state 
            if (results.size() == 0 && empty.getVisibility() != View.VISIBLE) { // If there is no record, display an empty view
                rv.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
            } else if (results.size() > 0 && rv.getVisibility() != View.VISIBLE) { // Hide the empty view to show the data
                rv.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
            }
        });

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        rv.setAdapter(new FoodRecyclerViewAdapter(this, foodSaved, true, deleted -> {
            Log.d(TAG, "onCreate: " + deleted.getTitle() + " clicked");
        }));

        // TODO: 09/12/2016 remove this
        // adding fake data
        realm.executeTransactionAsync(tRealm -> {
            Food food = new Food();

            food.setId(12);
            food.setTitle("mon test");
            food.setCategory("ma catégorie");

            tRealm.copyToRealmOrUpdate(food);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // subs.unsubscribe();
        realm.close();
    }
}
