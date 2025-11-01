package com.example.my_project.models;
public class MealPlanRequest {
    private String mealType;
    private double calorieLimit;

    public MealPlanRequest() {
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public double getCalorieLimit() {
        return calorieLimit;
    }

    public void setCalorieLimit(double calorieLimit) {
        this.calorieLimit = calorieLimit;
    }
}




