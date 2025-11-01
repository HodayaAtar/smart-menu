package com.example.my_project.controller;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.my_project.bl.UserBl;
import com.example.my_project.models.User;
import java.util.Map;


@RestController
public class LoginController {
    
@Autowired
private UserBl userBl;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        System.out.println("Received userName: " + user.getUsername());
        System.out.println("Received Password: " + user.getPassword());
        System.out.println("Received allergies: " + user.getAllergies());

    
        Optional<User> u = userBl.getUser(user.getUsername(), user.getPassword());
    
        if (u.isEmpty()) {
            System.out.println("User not found!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Invalid username or password"));
        } else {
            System.out.println("User found: " + user.getUsername());
            return ResponseEntity.ok(u.get()); // מחזיר את כל המשתמש עם כל השדות
        }
    }}
    

