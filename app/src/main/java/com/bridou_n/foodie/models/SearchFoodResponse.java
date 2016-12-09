package com.bridou_n.foodie.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchFoodResponse {

    @SerializedName("serving_categories")
    @Expose
    private List<ServingCategory> servingCategories = null;
    @SerializedName("title_requested")
    @Expose
    private String titleRequested;
    @SerializedName("title_completed")
    @Expose
    private String titleCompleted;
    @SerializedName("serving_sizes")
    @Expose
    private List<ServingSize> servingSizes = null;
    @SerializedName("language_requested")
    @Expose
    private String languageRequested;
    @SerializedName("food")
    @Expose
    private List<Food> food = null;

    /**
     * 
     * @return
     *     The servingCategories
     */
    public List<ServingCategory> getServingCategories() {
        return servingCategories;
    }

    /**
     * 
     * @param servingCategories
     *     The serving_categories
     */
    public void setServingCategories(List<ServingCategory> servingCategories) {
        this.servingCategories = servingCategories;
    }

    /**
     * 
     * @return
     *     The titleRequested
     */
    public String getTitleRequested() {
        return titleRequested;
    }

    /**
     * 
     * @param titleRequested
     *     The title_requested
     */
    public void setTitleRequested(String titleRequested) {
        this.titleRequested = titleRequested;
    }

    /**
     * 
     * @return
     *     The titleCompleted
     */
    public String getTitleCompleted() {
        return titleCompleted;
    }

    /**
     * 
     * @param titleCompleted
     *     The title_completed
     */
    public void setTitleCompleted(String titleCompleted) {
        this.titleCompleted = titleCompleted;
    }

    /**
     * 
     * @return
     *     The servingSizes
     */
    public List<ServingSize> getServingSizes() {
        return servingSizes;
    }

    /**
     * 
     * @param servingSizes
     *     The serving_sizes
     */
    public void setServingSizes(List<ServingSize> servingSizes) {
        this.servingSizes = servingSizes;
    }

    /**
     * 
     * @return
     *     The languageRequested
     */
    public String getLanguageRequested() {
        return languageRequested;
    }

    /**
     * 
     * @param languageRequested
     *     The language_requested
     */
    public void setLanguageRequested(String languageRequested) {
        this.languageRequested = languageRequested;
    }

    /**
     * 
     * @return
     *     The food
     */
    public List<Food> getFood() {
        return food;
    }

    /**
     * 
     * @param food
     *     The food
     */
    public void setFood(List<Food> food) {
        this.food = food;
    }

}
