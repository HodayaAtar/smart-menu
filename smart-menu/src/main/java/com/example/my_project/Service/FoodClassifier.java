package com.example.my_project.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class FoodClassifier {

    private static final String INPUT_FILE = "src/main/resources/data/dataBase_of_food.xlsx";
    private static final String OUTPUT_FILE = "src/main/resources/data/מאכלים_מתוייגים.xlsx";

    public static void main(String[] args) {
        try {
            classifyExcelFile(INPUT_FILE, OUTPUT_FILE);
            System.out.println("✔ הקובץ תויג נשמר בהצלחה: " + OUTPUT_FILE);
        } catch (Exception e) {
            System.err.println("שגיאה בעיבוד הקובץ: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void classifyExcelFile(String inputPath, String outputPath) throws IOException {
        try (FileInputStream fis = new FileInputStream(inputPath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);

            int nameIndex = findColumnIndex(header, "שם מאכל");
            int categoryIndex = findColumnIndex(header, "סוג מאכל");
            int autoTagIndex = header.getLastCellNum();

            // Add new header column
            Cell newHeaderCell = header.createCell(autoTagIndex);
            newHeaderCell.setCellValue("תיוג אוטומטי");

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String name = getCellString(row.getCell(nameIndex)).toLowerCase();
                String type = getCellString(row.getCell(categoryIndex));

                Cell tagCell = row.createCell(autoTagIndex);
                if (type == null || type.trim().isEmpty()) {
                    tagCell.setCellValue(classify(name));
                } else {
                    tagCell.setCellValue(type);
                }
            }

            // Ensure directory exists
            Files.createDirectories(Path.of(outputPath).getParent());

            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                workbook.write(fos);
            }
        }
    }

    private static int findColumnIndex(Row header, String columnName) {
        for (Cell cell : header) {
            if (columnName.equalsIgnoreCase(cell.getStringCellValue().trim())) {
                return cell.getColumnIndex();
            }
        }
        throw new IllegalArgumentException("לא נמצאה עמודה בשם: " + columnName);
    }

    private static String getCellString(Cell cell) {
        return (cell != null) ? cell.toString().trim() : "";
    }

    public static String classify(String name) {  
        if (matches(name, "סלט|ירק|פירות|תפוח|בננה|אבטיח|מלון|גזר|עגבנייה")) return "קינוח";
        if (matches(name, "בייגל|לחמנייה|פרווה|טבעוני")) return "פרווה";
        if (matches(name, "עוגה|חטיפ|עוגייה|שוקולד|קינוח|מאפה|פאי|וופל")) return "קינוח";
        if (matches(name, "אורז|פסטה|פתיתים|תפוחי אדמה|קוסקוס ")) return " תוספת לבשרי";
        if (matches(name, "בשר|עוף|בקר|נקניק|כבש|קציצ|סטייק|דג")) return "בשרי";
        if (matches(name, "חלב|גבינה|שמנת|יוגורט|קוטג'|גבינ|ריקוטה|")) return "תוספת לחלבי";
        if (matches(name, "פוקאצ|פיצה|טוסט")) return "חלבי";
        return "לא ידוע";
    }

    private static boolean matches(String text, String pattern) {
        return Pattern.compile(pattern).matcher(text).find();
    }
}
