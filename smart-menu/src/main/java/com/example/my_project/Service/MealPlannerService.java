package com.example.my_project.Service;

import com.example.my_project.models.*;
import com.example.my_project.Util.MealUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class MealPlannerService {

   @Autowired
    private SmartMenuService smartMenuService;

    public List<DayMealPlan> generateWeeklyMealPlans(Long userId, List<Food> allFoods,
     List<String> mealTypes, Map<String, Double> caloriesPerMeal, List<String> userAllergies){

        List<DayMealPlan> weeklyPlans = new ArrayList<>();
        Set<Integer> usedFoodIds = new HashSet<>();
        List<Food> originalAllFoods = new ArrayList<>(allFoods);

        for (int day = 0; day < 7; day++) {
            Map<String, MealPlanResult> dailyMeals = new LinkedHashMap<>();
            Set<Integer> usedToday = new HashSet<>();
            Set<Integer> usedInSnacks = new HashSet<>();

            for (String mealType : mealTypes) {
                double calories = caloriesPerMeal.getOrDefault(mealType.trim(), 0.0);
                List<Food> typeFiltered = getFoodsForMealType(originalAllFoods, mealType);

                if (userAllergies != null && !userAllergies.isEmpty()) {
                    typeFiltered = typeFiltered.stream()
                        .filter(f -> !isFoodAllergic(f, userAllergies))
                        .collect(Collectors.toList());
                }

                List<Food> filteredFoods = typeFiltered.stream()
                    .filter(f -> !usedFoodIds.contains(f.getId()))//משאיר רק מאכלים שעדיין לא השתמשנו בהם
                    .filter(f -> !mealType.contains("ביניים") || !usedInSnacks.contains(f.getId()))//רק אם מדובר בביניים, אל תביא משהו שכבר היה בביניים.
                    .collect(Collectors.toList());

                MealPlanResult result = filteredFoods.isEmpty()
                    ? new MealPlanResult(new ArrayList<>(), 0, "אין מאכלים זמינים לארוחה זו")
                    : generateMealPlan(userId, filteredFoods, mealType, calories);

                for (MealItem item : result.getItems()) {
                    usedToday.add(item.getFood().getId());
                    if (mealType.contains("ביניים")) {
                        usedInSnacks.add(item.getFood().getId());
                    }
                }

                dailyMeals.put(mealType, result);
            }

            usedFoodIds.addAll(usedToday);
            weeklyPlans.add(new DayMealPlan(day + 1, dailyMeals));
        }

        return weeklyPlans;
    }

    private boolean isFoodAllergic(Food food, List<String> allergies) {
        String foodName = food.getName().toLowerCase();

        Map<String, List<String>> allergyKeywords = Map.of(
            "gluten", List.of("לחם", "חיטה", "קמח", "פיתה", "בצק","וופל" ,"עוגה","לחמני","בייגל","פחזני", "עוגיות", "פסטה","קרקר", "אטריות", "בורקס", "בייגלה", "גלוטן", "wheat", "flour", "bread", "pita", "noodle", "cake"),
            "milk", List.of("חלב", "חמאה", "גבינה", "שמנת", "קוטג'", "יוגורט", "רוויון", "milk", "cheese", "cream", "butter", "yogurt"),
            "nuts", List.of("אגוז", "אגוזים", "שקד", "שקדים", "לוז", "פקאן", "pistachio", "hazelnut", "nut", "nuts", "pecan", "almond", "cashew"),
            "soy", List.of("סויה", "soy", "טופו", "סויה מוקפצת"),
            "egg", List.of("ביצה", "ביצים", "חביתה", "שקשוקה", "egg", "eggs", "omelet"),
            "fish", List.of("דג", "דגים", "סלמון", "טונה", "פילה", "fish", "tuna", "salmon")
        );

        for (String allergy : allergies) {
            List<String> keywords = allergyKeywords.getOrDefault(allergy, List.of());
            for (String keyword : keywords) {
                if (foodName.contains(keyword)) return true;
            }
        }
        return false;
    }

    public void buildAndPrintRatingMatrix() {
        Map<Long, Map<Long, Integer>> matrix = smartMenuService.buildRatingMatrix();
        smartMenuService.printMatrix(matrix); 
    }

    private List<Food> getFoodsForMealType(List<Food> allFoods, String mealType) {
        return allFoods.stream()
            .filter(f -> MealUtils.isFoodAllowedForMealType(f, mealType))
            .filter(f -> f.getId() != null)
            .collect(Collectors.toList());
    }

    private double[] vectorFromField(List<Food> foods, java.util.function.ToDoubleFunction<Food> mapper) {
        return foods.stream().mapToDouble(mapper).toArray();
    }

    private double[] vectorForType(List<Food> foods, String type, double weight) {
        double[] row = new double[foods.size()];
        for (int i = 0; i < foods.size(); i++) {
            if (foods.get(i).getType().equals(type)) {
                row[i] = weight;
            }
        }
        return row;
    }

    public MealPlanResult generateMealPlan(Long userId, List<Food> filteredFoods, String mealType, double totalCalories) {
        try {
            if (filteredFoods.isEmpty()) return new MealPlanResult(List.of(), 0, "לא נמצאו מאכלים מתאימים");

            // המלצה לפי למידת מכונה
            List<Food> recommendedFoods = smartMenuService.recommendFoodsForUser(userId, filteredFoods);
            List<Food> finalFoods = recommendedFoods.isEmpty() ? filteredFoods : recommendedFoods;

            // Map<String, Food> bestFoodsByName = new HashMap<>();
            // for (Food food : finalFoods) {
            //     String nameKey = food.getName().trim().toLowerCase();
            //     if (!bestFoodsByName.containsKey(nameKey)) {
            //         bestFoodsByName.put(nameKey, food);
            //     } else {
            //         Food existing = bestFoodsByName.get(nameKey);
            //         if (food.getCalories() < existing.getCalories()) {
            //             bestFoodsByName.put(nameKey, food);
            //         }
            //     }
            // }
            // finalFoods = new ArrayList<>(bestFoodsByName.values());

            int n = finalFoods.size();
            double[] goal = new double[n];
            for (int i = 0; i < n; i++) {
                goal[i] = finalFoods.get(i).getCalories() / 100.0;
            }
            //וקטור+גבול
            List<double[]> constraints = new ArrayList<>();
            List<Double> bounds = new ArrayList<>();

            constraints.add(vectorFromField(finalFoods, f -> f.getCalories() / 100.0));
            bounds.add(totalCalories);

            constraints.add(vectorFromField(finalFoods, f -> -f.getProtein() / 100.0));
            bounds.add(-(totalCalories * 0.2 / 4.0));

            constraints.add(vectorFromField(finalFoods, f -> -f.getCarbs() / 100.0));
            bounds.add(-(totalCalories * 0.5 / 4.0));

            constraints.add(vectorFromField(finalFoods, f -> -f.getFat() / 100.0));
            bounds.add(-(totalCalories * 0.3 / 9.0));

            for (int i = 0; i < n; i++) {
                double[] row = new double[n];
                row[i] = 1.0;
                constraints.add(row);
                bounds.add(110.0);
            }

            Map<String, Double[]> typeLimits = switch (mealType) {
                case "צהריים" -> Map.of(
                    "בשרי", new Double[]{80.0, 160.0},
                    "תוספת לבשרי", new Double[]{20.0, 40.0}
                );
                case "בוקר" -> Map.of(
                    "חלבי", new Double[]{80.0, 160.0},
                    "תוספת לחלבי", new Double[]{20.0, 40.0}
                );
                default -> Map.of();
            };

            for (var entry : typeLimits.entrySet()) {
                constraints.add(vectorForType(finalFoods, entry.getKey(), -1.0));
                bounds.add(-entry.getValue()[0]);
                constraints.add(vectorForType(finalFoods, entry.getKey(), 1.0));
                bounds.add(entry.getValue()[1]);
            }

            double[][] A = constraints.toArray(new double[0][]);
            double[] b = bounds.stream().mapToDouble(Double::doubleValue).toArray();

            SimplexSolverManual solver = new SimplexSolverManual(A, b, goal);
            double[] solution = solver.solve();
            if (solution == null) return new MealPlanResult(List.of(), 0, "No solution.");

            List<MealItem> items = new ArrayList<>();
            double sumCalories = 0;

            for (int i = 0; i < solution.length; i++) {
                double grams = solution[i];
                if (grams > 0.01) {
                    Food food = finalFoods.get(i);
                    double cal = food.getCalories() * (grams / 100.0);
                    items.add(new MealItem(food, grams));
                    sumCalories += cal;
                }
            }

            if (items.isEmpty()) return new MealPlanResult(List.of(), 0, "Solution is empty.");
            return new MealPlanResult(items, sumCalories, "Success");

        } catch (Exception e) {
            return new MealPlanResult(List.of(), 0, "Error: " + e.getMessage());
        }
    }
}
