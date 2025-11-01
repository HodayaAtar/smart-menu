package com.example.my_project.Service;

import com.example.my_project.models.Food;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelFoodReader {

    public List<Food> readFromExcel(InputStream inputStream) {
        List<Food> foods = new ArrayList<>();
        int idCounter = 1; // ⬅️ מזהה רץ לכל מאכל חדש

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // דף ראשון

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // דלג על כותרת

                String name = (String) getCellValue(row.getCell(0));
                double protein = toDouble(getCellValue(row.getCell(1)));
                double carbs = toDouble(getCellValue(row.getCell(2)));
                double fat = toDouble(getCellValue(row.getCell(3)));
                String type = (String) getCellValue(row.getCell(4));
                double calories = toDouble(getCellValue(row.getCell(5)));

                if (type != null && !type.isEmpty() && calories > 0) {
                    Food food = new Food(name, protein, carbs, fat, calories, type);
                    food.setId(idCounter++); // ✅ קובע ID ייחודי
                    foods.add(food);

                    System.out.println("✅ Loaded food: " + name + ", id: " + food.getId() +
                                       ", type: " + type + ", calories: " + calories);
                }
            }

            System.out.println("✅ Total foods loaded: " + foods.size());

        } catch (Exception e) {
            e.printStackTrace(); // הדפס שגיאה בעת קריאת הקובץ
        }

        return foods;
    }

    private Object getCellValue(Cell cell) {
        if (cell == null) return null;

        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim();
                case NUMERIC:
                    return cell.getNumericCellValue();
                case FORMULA:
                    switch (cell.getCachedFormulaResultType()) {
                        case STRING:
                            return cell.getRichStringCellValue().getString().trim();
                        case NUMERIC:
                            return cell.getNumericCellValue();
                        default:
                            return null;
                    }
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private double toDouble(Object value) {
        if (value == null) return 0.0;

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if (value instanceof String) {
            try {
                String numericPart = ((String) value).replaceAll("[^\\d.]", ""); // משאיר רק מספרים ונקודה
                return Double.parseDouble(numericPart);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }
}
