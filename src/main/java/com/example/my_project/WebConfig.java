package com.example.my_project;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**") // הגדרת כל ה-URLים של השרת
                .allowedOrigins("http://localhost:3000") // כתובת ה-React שלך
                .allowedMethods("GET", "POST", "PUT", "DELETE") // שיטות HTTP שמותרות
                .allowedHeaders("*"); // כותרות HTTP שמותרות
    }
}

