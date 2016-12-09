package com.bridou_n.foodie.features.foodList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bridou_n.foodie.R;
import com.bridou_n.foodie.models.Food;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by bridou_n on 09/12/2016.
 */

public class FoodRecyclerViewAdapter extends RealmRecyclerViewAdapter<Food, FoodRecyclerViewAdapter.FoodHolder> {

    private static final String TAG = "SAVED_FOOD_RV_ADAPTER";

    private Context ctx;
    private OnFoodClick listener;

    public FoodRecyclerViewAdapter(@NonNull Context context,
                                   @Nullable OrderedRealmCollection<Food> data,
                                   boolean autoUpdate,
                                   OnFoodClick listener) {
        super(context, data, autoUpdate);
        ctx = context;
        this.listener = listener;
    }

    public class FoodHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title) TextView title;
        @BindView(R.id.category) TextView category;
        @BindView(R.id.action) ImageView action;

        FoodHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindTo(Food food) {
            title.setText(food.getTitle());
            category.setText(food.getCategory());
            action.setOnClickListener(v -> {
                listener.onFoodClicked(food);
            });
        }

    }

    @Override
    public FoodHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FoodHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false));
    }

    @Override
    public void onBindViewHolder(FoodHolder holder, int position) {
        holder.bindTo(getItem(position));
    }

    interface OnFoodClick {
        void onFoodClicked(Food f);
    }
}
