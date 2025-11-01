package com.example.my_project.models;
import java.util.List;

public class MealPlanResult {
    private List<MealItem> items;
    private double totalCalories;
    private String message;

    public MealPlanResult(List<MealItem> items, double totalCalories, String message) {
        this.items = items;
        this.totalCalories = totalCalories;
        this.message = message;
    }

public MealPlanResult() {
}

    public List<MealItem> getItems() {
        return items;
    }

    public void setItems(List<MealItem> items) {
        this.items = items;
    }

    public double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
