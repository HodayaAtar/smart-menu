package com.example.my_project.controller;

import com.example.my_project.dal.UserDal;
import com.example.my_project.models.SelectionRequest;
import com.example.my_project.models.User;
import com.example.my_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserRepository userRepository;

        @Autowired
    private UserDal userDal;

    @PostMapping("/bmi")
    public ResponseEntity<Map<String, Object>> calculateBmi(@RequestBody User user) {
        System.out.println("Received User: " + user); 

        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            System.out.println("Error: Received invalid user data!");
            return ResponseEntity.badRequest().body(Map.of("message", "Error: Received invalid user data!"));
        }

        double bmi = UserDal.calculateBMI(user.getHeight(), user.getWeight());
      
        User added = userDal.addNewUser(user); // לא סטטי

        if (added != null ) {
            System.out.println("התווספת בהצלחה");
        } else {
            System.out.println("לא התווספת משהו לא תקין");
        }

        String message;
        if (bmi < 18.5) {
            message = "Your BMI is under the normal range.";
        } else if (bmi < 24.9) {
            message = "Your BMI is in the normal range.";
        } else if (bmi < 29.9) {
            message = "Your BMI is above the normal range.";
        } else {
            message = "Your BMI is not in the normal range!";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("bmi", bmi);
        response.put("message", message);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/calories")
    public ResponseEntity<Map<String, Object>> calculateCalories(@RequestBody User user) {
        System.out.println("Received User: " + user);

        if (user == null || user.getHeight() == 0 || user.getWeight() == 0) {
            System.out.println("Error: Received invalid user data!");
            return ResponseEntity.badRequest().body(Map.of("message", "Error: Received invalid user data!"));
        }

        double dailyCalories = UserDal.calculateDailyCalories(user.getHeight(), user.getWeight(), user.getAge(), user.getActivityLevel(), user.getGender());
        System.out.println("Daily Calories: " + dailyCalories);

        return ResponseEntity.ok(Map.of("calories", dailyCalories));
    }

    @PostMapping("/water")
    public ResponseEntity<Map<String, Object>> calculateWater(@RequestBody User user) {
        System.out.println("Received User: " + user);

        if (user == null || user.getWeight() == 0) {
            System.out.println("Error: Received invalid user data!");
            return ResponseEntity.badRequest().body(Map.of("message", "Error: Received invalid user data!"));
        }

        double dailyLiters = UserDal.calculateLiters(user.getWeight());
        System.out.println("Daily Liters: " + dailyLiters);

        return ResponseEntity.ok(Map.of("liters", dailyLiters));
    }

    @PostMapping("/select")
    public ResponseEntity<Map<String, Object>> divCalories(@RequestBody SelectionRequest request) {
        String selection = request.getSelection(); 
        User user = request.getUser(); 

        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: User data is missing!"));
        }

        double dailyCalories = UserDal.calculateDailyCalories(user.getHeight(), user.getWeight(), user.getAge(), user.getActivityLevel(), user.getGender());

        if ("על ידי המערכת".equals(selection)) {
            Map<String, Integer> mealPlan = calculateMealCalories(dailyCalories);
            System.out.println("Meal Plan: " + mealPlan);
            return ResponseEntity.ok(Map.of("mealPlan", mealPlan));
        } else {
            return ResponseEntity.ok(Map.of("message", "בחרת להזין את הנתונים בעצמך."));
        }
    }

    public Map<String, Integer> calculateMealCalories(double dailyCalories) {
        double X = dailyCalories / 7;

        Map<String, Integer> mealPlan = new HashMap<>();
        mealPlan.put("breakfast", (int) Math.round(X * 1.5));
        mealPlan.put("lunch", (int) Math.round(X * 2));
        mealPlan.put("dinner", (int) Math.round(X * 1.5));
        mealPlan.put("snack1", (int) Math.round(X));
        mealPlan.put("snack2", (int) Math.round(X));

        return mealPlan;
    }

    // ✅ פעולות CRUD

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setPassword(updatedUser.getPassword());
            user.setEmail(updatedUser.getEmail());
            user.setGender(updatedUser.getGender());
            user.setAge(updatedUser.getAge());
            user.setHeight(updatedUser.getHeight());
            user.setWeight(updatedUser.getWeight());
            user.setActivityLevel(updatedUser.getActivityLevel());
            user.setAllergies(updatedUser.getAllergies());
            return ResponseEntity.ok(userRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable longdir /a
 id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
