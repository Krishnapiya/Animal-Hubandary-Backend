package com.keltron.utility.manage.service.abs;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.keltron.utility.requests.bean.XlsConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

public class ExcelExportUtil {

    public static ByteArrayOutputStream generateExcel(List<?> data, List<XlsConfig> xlsConfig) {
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Sheet sheet = workbook.createSheet("Report");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);

            // Header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < xlsConfig.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(xlsConfig.get(i).getHeader());
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            int rowNum = 1;
            for (Object dto : data) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < xlsConfig.size(); i++) {
                    String attr = xlsConfig.get(i).getAttr();
                    Object value = getFieldValue(dto, attr);
                    Cell cell = row.createCell(i);

                    if (value == null) {
                        cell.setCellValue("");
                    } else if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else if (value instanceof Boolean) {
                        cell.setCellValue((Boolean) value);
                    } else if (value instanceof Date) {
                        cell.setCellValue((Date) value);
                        CellStyle dateStyle = workbook.createCellStyle();
                        dateStyle.setDataFormat(workbook.getCreationHelper()
                            .createDataFormat().getFormat("dd-MM-yyyy"));
                        cell.setCellStyle(dateStyle);
                    } else if (value instanceof byte[]) {
                        cell.setCellValue("[BINARY]");
                    } else {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            workbook.write(out);
            return out;
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel", e);
        } finally {
            try {
                workbook.close();
            } catch (IOException ignored) {}
        }
    }

    private static Object getFieldValue(Object obj, String fieldPath) {
        if (obj == null || fieldPath == null) return null;

        try {
            String[] parts = fieldPath.split("\\.");
            Object current = obj;

            for (String part : parts) {
                if (current == null) return null;

                try {
                    Field field = current.getClass().getDeclaredField(part);
                    field.setAccessible(true);
                    current = field.get(current);
                } catch (NoSuchFieldException e) {
                    String getterName = "get" + part.substring(0, 1).toUpperCase() + part.substring(1);
                    Method getter = current.getClass().getMethod(getterName);
                    current = getter.invoke(current);
                }
            }
            return current;
        } catch (Exception e) {
            return null;
        }
    }
}
