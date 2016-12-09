package com.bridou_n.foodie.features.foodList;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bridou_n.foodie.R;
import com.bridou_n.foodie.models.Food;
import com.jakewharton.rxbinding.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by bridou_n on 09/12/2016.
 */

public class FoodSearchedRecyclerViewAdapter extends RecyclerView.Adapter<FoodSearchedRecyclerViewAdapter.FoodHolder> {

    private static final String     TAG = "FoodSearched_RV";
    private List<Food> data;
    private Context ctx;
    private Realm realm;

    public class FoodHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.container) ConstraintLayout container;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.category) TextView category;
        @BindView(R.id.action) ImageView action;

        public FoodHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindTo(Food food) {
            title.setText(food.getTitle());
            category.setText(food.getCategory());
            action.setColorFilter(ContextCompat.getColor(ctx, R.color.colorAccent));

            updateSavedState(realm.where(Food.class).equalTo("id", food.getId()).findFirst());

            RxView.clicks(container)
                    .throttleFirst(100, TimeUnit.MILLISECONDS) // In case the user clicks very fast..
                    .flatMap(c -> Observable.fromEmitter(emitter -> {
                        int id = food.getId();

                        realm.executeTransactionAsync(tRealm -> {
                            Food tFood = tRealm.where(Food.class).equalTo("id", id).findFirst();

                            if (tFood == null) { // Add the food to the saved food
                                tRealm.copyToRealm(food);
                            } else { // delete it from the saved food
                                tFood.deleteFromRealm();
                            }
                            emitter.onNext(tRealm.where(Food.class).equalTo("id", id).findFirst());
                            emitter.onCompleted();
                        });
                    }, Emitter.BackpressureMode.BUFFER))
                    .map(e -> (Food)e)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::updateSavedState);
        }

        private void updateSavedState(Food f) {
            action.setImageResource(f == null ? R.drawable.ic_star_border_black_24dp : R.drawable.ic_star_black_24dp);
        }
    }

    public FoodSearchedRecyclerViewAdapter(Context context, List<Food> d, Realm realm) {
        data = d;
        ctx = context;
        this.realm = realm;
    }

    @Override
    public FoodHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FoodHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false));
    }

    @Override
    public void onBindViewHolder(FoodHolder holder, int position) {
        holder.bindTo(data.get(position));
    }

    public void setData(List<Food> data) {
        this.data = data; // carefull, this is not a deep copy
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
