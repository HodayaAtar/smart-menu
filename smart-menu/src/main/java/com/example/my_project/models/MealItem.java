package com.example.my_project.models;

public class MealItem {
    private Food food;
    private double quantity;

    public MealItem(Food food, double quantity) {
        this.food = food;
        this.quantity = quantity;
    }


    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getCalories() {
        double caloriesPer100g = food.getCalories(); // נניח שיש את זה ב-Food
        return (caloriesPer100g * quantity) / 100.0;
    }
}   