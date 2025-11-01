package com.example.my_project.Util;

import com.example.my_project.Service.TextService;

public class TextPrinter {
    public static void printlnHebrew(String text) {
        System.out.println(TextService.reverseIfHebrew(text));
    }
}

