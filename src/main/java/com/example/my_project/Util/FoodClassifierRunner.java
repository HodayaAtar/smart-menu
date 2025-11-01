package com.example.my_project.Util;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;

// import com.example.my_project.Service.FoodClassifier;
import com.example.my_project.Service.TrainingDataExporter;

public class FoodClassifierRunner {
    public static void runOnceForTagging() {
        //         try {
        //     FoodClassifier.classifyExcelFile(
        //         "src/main/resources/data/dataBase_of_food.xlsx",
        //         "src/main/resources/data/מאכלים_מתוייגים.xlsx"
        //     );
        //     System.out.println("סיום תיוג בהצלחה ✅");
        // } catch (Exception e) {
        //     System.out.println("שגיאה בתהליך התיוג: " + e.getMessage());
        //     e.printStackTrace();
        // }

     try {
            String inputExcel = new ClassPathResource("data/dataBase_of_food.xlsx").getFile().getAbsolutePath();
            String outputCsv = "target/food_training_data.csv";

            TrainingDataExporter.exportTrainingData(inputExcel, outputCsv);

            System.out.println("✅ נוצר קובץ CSV בהצלחה: " + outputCsv);
        } catch (IOException e) {
            System.err.println("❌ שגיאה ביצירת CSV: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        runOnceForTagging();
    }
}