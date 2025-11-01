package com.example.my_project.Service;

public class TextService {
    public static String reverseIfHebrew(String text) {
        if (text != null && text.matches(".*[\\p{IsHebrew}]+.*")) {
            return new StringBuilder(text).reverse().toString();
        }
        return text;
    }
}

