package com.example.my_project.Service;

import com.example.my_project.models.Food;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
 
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

@Service
public class FoodVectorizer {

    private static final Map<String, Integer> CATEGORY_INDEX = Map.of(
        "בשרי", 0,
        "חלבי", 1,
        "פרווה", 2,
        "תוספת לחלבי", 3,
        "תוספת לבשרי", 4,
        "קינוח", 5
    );

    public List<Food> readFoodVectorsFromExcel(String filePath) throws Exception {
        List<Food> result = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 1||row.getRowNum() == 0) continue; // כותרת

                String name = row.getCell(0).getStringCellValue();
                double protein = row.getCell(1).getNumericCellValue();
                double carbs = row.getCell(2).getNumericCellValue();
                double fat = row.getCell(3).getNumericCellValue();
                String type = row.getCell(4).getStringCellValue().trim();
                double calories = row.getCell(5).getNumericCellValue();
                
                result.add(new Food(name, protein, fat, carbs, calories, type));
            }
        }
        return result;
    }

    public double[] getProteinVector(List<Food> foods) {
        return foods.stream().mapToDouble(Food::getProtein).toArray();
    }

    public double[] getCarbsVector(List<Food> foods) {
        return foods.stream().mapToDouble(Food::getCarbs).toArray();
    }

    public double[] getFatsVector(List<Food> foods) {
        return foods.stream().mapToDouble(Food::getFat).toArray();
    }
    
    public double[] getCaloriesVector(List<Food> foods) {
        return foods.stream().mapToDouble(Food::getCalories).toArray();
    }
    public double[] getInclusionVector(List<Food> foods) {
        return foods.stream().mapToDouble(f -> 1.0).toArray();
    }

    public double[] foodToVector(Food food) {
        double[] vector = new double[4 + CATEGORY_INDEX.size()];
        vector[0] = food.getProtein();
        vector[1] = food.getFat();
        vector[2] = food.getCarbs();
        vector[3] = food.getCalories();

        CATEGORY_INDEX.getOrDefault(food.getType(), -1);
        if (CATEGORY_INDEX.containsKey(food.getType())) {
            vector[4 + CATEGORY_INDEX.get(food.getType())] = 1.0;
        }
        return vector;
    }
}
