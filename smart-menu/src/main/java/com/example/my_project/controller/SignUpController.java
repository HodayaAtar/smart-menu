package com.example.my_project.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_project.Service.UserDetailService;
import com.example.my_project.dal.UserDal;
import com.example.my_project.models.User;

@RestController
public class SignUpController {

    @Autowired
    private UserDetailService userDetailService;
    @Autowired
private UserDal userDal;


    @PostMapping("/SignUp")
    public ResponseEntity<Map<String, String>> signUp(@RequestBody User user) {

        // חישובי BMI, קלוריות וליטרים
        double BMI = UserDal.calculateBMI(user.getHeight(), user.getWeight());
        double dailyCalories = UserDal.calculateDailyCalories(user.getHeight(), user.getWeight(), user.getAge(), user.getActivityLevel(), user.getGender());
        double liters = UserDal.calculateLiters(user.getWeight());

        // קודם ננסה לשמור את המשתמש
           User added = userDal.addNewUser(user);
           if (added != null) {
            System.out.println("נשמר עם ID: " + user.getUserId());
            System.out.println("Received allergies: " + user.getAllergies());


            // שמירת פרטי המשתמש ב-userDetailService
            userDetailService.saveOrUpdateDetail(user.getUserId(), "BMI", BMI);
            userDetailService.saveOrUpdateDetail(user.getUserId(), "DailyCalories", dailyCalories);
            userDetailService.saveOrUpdateDetail(user.getUserId(), "Liters", liters);

return ResponseEntity.ok(
    Map.of(
        "message", "התווספת בהצלחה",
        "userId",String.valueOf(user.getUserId())
    )
);

        }

        // אם המשתמש כבר קיים, נחזיר הודעת שגיאה
boolean exists = userDal.isUserExists(user.getUsername());
        if (exists) {
            System.out.println("לא התווספת, משתמש קיים");
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "משתמש קיים. אנא נסה עם שם משתמש אחר."));
        } else {
            System.out.println("לא התווספת, משהו לא תקין");
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "אירעה שגיאה. אנא נסה שוב."));
        }
    }
}
