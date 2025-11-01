package com.example.my_project.models;

import java.util.Map;

public class DayMealPlan {
    private int day;
    private Map<String, MealPlanResult> meals;

    public DayMealPlan(int day, Map<String, MealPlanResult> meals) {
        this.day = day;
        this.meals = meals;
    }

    public int getDay() {
        return day;
    }

    public Map<String, MealPlanResult> getMeals() {
        return meals;
    }
}

