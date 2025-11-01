package com.example.my_project.Util;
import com.example.my_project.models.Food;

public class MealUtils {
 public static boolean isFoodAllowedForMealType(Food food, String mealType) {
    System.out.println(">> Checking: " + food.getName() + " for meal type: " + mealType+ "   cal "+ food.getCalories());
    String type = food.getType();
    if (type == null) return false;
    type = type.trim();

    return switch (mealType.toLowerCase()) {
        case "צהריים" -> type.equals("בשרי") || type.equals("תוספת לבשרי");
        case "בוקר" -> type.equals("חלבי") || type.equals("תוספת לחלבי");
        case "ערב"  -> type.equals("פרווה");
        case "ביניים 1", "ביניים 2" ->type.equals("קינוח");
        default -> false;
    };
}

}

