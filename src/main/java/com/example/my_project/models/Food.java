package com.example.my_project.models;

import jakarta.persistence.*;

@Entity
@Table(name = "food")
public class Food {
   @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer id;

@Column(name = "name", length = 255, unique = true)
    private String name;
    private double protein;
    private double fat;
    private double carbs;
    private double calories;
    private String type;
    private double weight = 0.0;

    public Food() {}

    public Food(String name, double protein, double fat, double carbs, double calories, String type) {
        this.name = name;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.calories = calories;
        this.type = type;
    }

public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; } 


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }

    public double getFat() { return fat; }
    public void setFat(double fat) { this.fat = fat; }

    public double getCarbs() { return carbs; }
    public void setCarbs(double carbs) { this.carbs = carbs; }

    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }


    @Override
public String toString() {
    return "Food{" +
            "name='" + name + '\'' +
            ", calories=" + calories +
            ", protein=" + protein +
            ", fat=" + fat +
            ", carbs=" + carbs +
            ", type='" + type + '\'' +
            '}';
}

}
