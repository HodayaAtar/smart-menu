package com.example.my_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.my_project.models.MealPlanResult;
import com.example.my_project.models.MealPlanRequest;
import com.example.my_project.Service.ExcelFoodReader;
import com.example.my_project.Service.MealPlannerService;
import com.example.my_project.Service.SmartMenuService;
import com.example.my_project.Util.TextPrinter;
import com.example.my_project.models.DayMealPlan;
import com.example.my_project.models.Food;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Optional;  
import com.example.my_project.models.User;
import com.example.my_project.repository.UserRepository;


@RestController
public class MealPlanController {
    @Autowired
    private ExcelFoodReader excelReader;
   @Autowired
    private UserRepository userRepository;
    @Autowired
    private MealPlannerService mealPlannerService;

    @Autowired
    private SmartMenuService smartMenuService;


    @PostMapping("/api/mealplan")
    public ResponseEntity<?> generateWeeklyMealPlan(@RequestParam Long userId,@RequestBody List<MealPlanRequest> meals){

//optinal נותן עטיפה לאובייקט ומכיל מה שחוזר האם קיים או לא קיים
    Optional<User> optionalUser = userRepository.findById(userId);
    if (optionalUser.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    // שליפת רשימת אלרגיות למשתמש
    List<String> userAllergies = optionalUser.get().getAllergies();
    System.out.println("User allergies: " + userAllergies);

    //קוראת קובץ מתוך ה-resource 
    try (InputStream inputStream = getClass().getResourceAsStream("/data/dataBase_of_food.xlsx")) {

        // בדיקה אם קובץ המאכלים קיים
        if (inputStream == null) {
            TextPrinter.printlnHebrew("⚠️ קובץ המאכלים לא נמצא!");
            return ResponseEntity.ok(Map.of("error", new MealPlanResult(List.of(), 0, "קובץ המאכלים לא נמצא")));
        }

        TextPrinter.printlnHebrew("טוען קובץ אקסל...");
        List<Food> allFoods = excelReader.readFromExcel(inputStream);
        TextPrinter.printlnHebrew("נטענו " + allFoods.size() + " מאכלים");
        allFoods.forEach(System.out::println);

        if (allFoods.isEmpty()) {
            TextPrinter.printlnHebrew("לא נמצאו מאכלים בקובץ!");
            return ResponseEntity.ok(Map.of("error", new MealPlanResult(List.of(), 0, "לא נמצאו מאכלים בקובץ")));
        }

        //  חילוץ סוגי הארוחות מתוך הבקשה
        List<String> mealTypes = meals.stream()
                .map(MealPlanRequest::getMealType)
                .toList();

        // מיפוי של כל ארוחה עם כמות הקלוריות שלה
        Map<String, Double> caloriesPerMeal = new LinkedHashMap<>();
        for (MealPlanRequest req : meals) {
            caloriesPerMeal.put(req.getMealType(), req.getCalorieLimit());
        }

        // קריאה לפונקציה שמייצרת את התפריט השבועי 
        List<DayMealPlan> weeklyPlans = mealPlannerService.generateWeeklyMealPlans(
                userId,
                allFoods,
                mealTypes,
                caloriesPerMeal,
                userAllergies 
        );

        return ResponseEntity.ok(Map.of("weeklyPlans", weeklyPlans));

    } catch (Exception e) {
        TextPrinter.printlnHebrew("❌ שגיאה במהלך תכנון הארוחות:");
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", new MealPlanResult(List.of(), 0, "אירעה שגיאה במהלך תכנון הארוחות")));
    }
}

// קריאה לפו לבניית מטריצת דירוגים בלחיצת כפתור 
@GetMapping("/api/ratings-matrix")
public ResponseEntity<String> testMatrix() {
    smartMenuService.buildRatingMatrix();
    return ResponseEntity.ok("Matrix built and printed");
}
}