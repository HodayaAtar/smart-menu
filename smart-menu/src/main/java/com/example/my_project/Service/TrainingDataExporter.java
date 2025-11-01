package com.example.my_project.Service;

import org.apache.poi.ss.usermodel.*;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class TrainingDataExporter {

    public static void exportTrainingData(String inputExcel, String outputCsv) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(inputExcel));
             FileWriter writer = new FileWriter(outputCsv)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);

            int nameIndex = findColumnIndex(header, "שם מאכל");
            int categoryIndex = findColumnIndex(header, "סוג מאכל");

            writer.append("שם מאכל,סוג מאכל\n");

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String name = getCellString(row.getCell(nameIndex)).replace(",", " ");
                String category = getCellString(row.getCell(categoryIndex));

                if (!category.isEmpty() && !category.equalsIgnoreCase("לא ידוע")) {
                    writer.append(name).append(",").append(category).append("\n");
                }
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
}
