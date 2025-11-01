package com.example.my_project.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.my_project"})
public class ServerSideApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerSideApplication.class, args);
        System.out.println("Server is running on port http://localhost:8080");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:3000");
            }
        };
    }

    @RestController
    class HelloWorldController {
        @GetMapping("/")
        public String helloWorld() {
            return "Hello World";
        }
    }
}