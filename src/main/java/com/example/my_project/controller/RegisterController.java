package com.example.my_project.controller;
//register controller
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.my_project.models.User;

@RestController
public class RegisterController {

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        // Add your registration logic here
        return "User registered successfully";
    }
}