package com.example.my_project;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import com.example.my_project.Service.TrainingDataExporter;

@SpringBootTest(classes = MyProjectApplication.class)  
class MyProjectApplicationTests {

    @Test
    void generateCsvOnce() {
        try {
            String inputExcel = new ClassPathResource("data/dataBase_of_food.xlsx").getFile().getAbsolutePath();
            String outputCsv = "target/food_training_data.csv";

            TrainingDataExporter.exportTrainingData(inputExcel, outputCsv);

            System.out.println("✅ נוצר קובץ CSV בהצלחה: " + outputCsv);
        } catch (Exception e) {
            System.err.println("❌ שגיאה ביצירת CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
