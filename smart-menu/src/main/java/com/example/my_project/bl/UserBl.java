package com.example.my_project.bl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.my_project.dal.UserDal;
import com.example.my_project.dal.UserDetailsDal;
import com.example.my_project.models.User;
import com.example.my_project.models.UserDetail;

@Service
public class UserBl {

    @Autowired
    private UserDal userDal;
private UserDetailsDal us;
    public boolean addUser(User user) {
        System.out.println("UserBl.addUser() called with user: " + user.getUsername());

        // הוספת משתמש למסד נתונים
        User savedUser = userDal.addNewUser(user);  // מחזיר את המשתמש שנשמר כולל מזהה

        if (savedUser != null && savedUser.getUserId() != 0) {
            // חישוב BMI וקלוריות
            double bmi = UserDal.calculateBMI(user.getHeight(), user.getWeight());
            double dailyCalories = UserDal.calculateDailyCalories(
                    user.getHeight(),
                    user.getWeight(),
                    user.getAge(),
                    user.getActivityLevel(),
                    user.getGender()
            );

            UserDetail details = new UserDetail();
            details.setUserId(savedUser.getUserId());
            us.updateDetail(savedUser.getUserId(), "BMI", bmi);
            us.updateDetail(savedUser.getUserId(), "DailyCalories", dailyCalories);


            // שמירת הפרטים
            us.saveDetail(details);

            return true;
        }

        return false;
    }

    public void removeUser(User user) {
        userDal.removeUser(user);
    }

    public void updateUser(User user) {
        userDal.updateUser(user);
    }

    public Optional<User> getUser(String username, String password) {
        return userDal.getUser(username, password);
    }

    public Optional<User> getDetails(String username, String password) {
        return getUser(username, password);
    }
}
