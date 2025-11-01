package com.example.my_project.dal;

import com.example.my_project.models.User;
import com.example.my_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Component
public class UserDal {

    private final UserRepository userRepository;

    @Autowired
    public UserDal(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // בדיקה אם משתמש קיים במסד נתונים
    public boolean isUserExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    // הוספת משתמש חדש למסד הנתונים
    public User addNewUser(User user) {
        if (isUserExists(user.getUsername())) {
            System.out.println("User already exists, not adding.");
            return user;
        }
        userRepository.save(user);
        return user;
    }

    // קריאת משתמש לפי שם וסיסמה
    public Optional<User> getUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password));
    }

    // הסרת משתמש
    public void removeUser(User user) {
        userRepository.delete(user);
    }

    // עדכון משתמש (שומר אם קיים)
    public void updateUser(User user) {
        userRepository.save(user);
    }

    // חישוב BMI
    public static double calculateBMI(double height, double weight) {
        double bmi = weight / Math.pow(height / 100.0, 2);
        return roundToTwoDecimals(bmi);
    }

    public static double calculateDailyCalories(double height, double weight, int age, int activityLevel, String gender) {
        double bmr = 10 * weight + 6.25 * height - 5 * age + ("זכר".equals(gender) ? 5 : -161);

        switch (activityLevel) {
            case 1 -> bmr *= 1.2;
            case 2 -> bmr *= 1.375;
            case 3 -> bmr *= 1.425;
            case 4 -> bmr *= 1.55;
            case 5 -> bmr *= 1.75;
            case 6 -> bmr *= 1.9;
        }

        return roundToTwoDecimals(bmr);
    }

    // חישוב ליטרים של מים ליום
    public static double calculateLiters(double weight) {
        double liters = (30 * weight) / 1000.0;
        return roundToTwoDecimals(liters);
    }

    // עיגול מספר ל-2 ספרות אחרי הנקודה
    private static double roundToTwoDecimals(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
