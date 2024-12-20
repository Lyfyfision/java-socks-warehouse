package com.javarest.socks.util;

import com.javarest.socks.dto.SocksRequest;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {
    public static List<SocksRequest> parseExcelFile(File file) {
        List<SocksRequest> sockList = new ArrayList<>();

        try(InputStream fis = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for(Row row : sheet) {
                if(row.getRowNum() == 0) {
                    continue;
                }
                String color = row.getCell(0).getStringCellValue();
                int cottonPercentage = (int) row.getCell(1).getNumericCellValue();
                int quantity = (int) row.getCell(2).getNumericCellValue();

                sockList.add(SocksRequest.builder()
                        .color(color)
                        .cottonPercentage(cottonPercentage)
                        .quantity(quantity)
                        .build());
            }
            //TODO добавить ошибку
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return sockList;
    }

    private ExcelUtils() {
    }
}
