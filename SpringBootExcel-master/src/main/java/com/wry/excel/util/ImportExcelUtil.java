package com.wry.excel.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ImportExcelUtil {
    public static List<Map> readXlsx(String path) throws IOException {
        InputStream input = new FileInputStream(path);
        return readXlsx(input);
    }

    public static List<Map> readXls(String path) throws IOException {
        InputStream input = new FileInputStream(path);
        return readXls(input);
    }

    public static List<Map> readXlsx(InputStream input) throws IOException {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(input);
        List<Map> result = new ArrayList<Map>();
        //保存第一行
        List list = new ArrayList();
        // 获取每一个工作薄
        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            // 获取当前工作薄的每一行
            for (int rowNum = 0; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow != null) {
                    int minCellNum = xssfRow.getFirstCellNum();
                    int maxCellNum = xssfRow.getLastCellNum();

                    Map map = new HashMap();
                    //循环每一列
                    for (int i = minCellNum; i < maxCellNum; i++) {
                        XSSFCell cell = xssfRow.getCell(i);
                        if (cell == null || cell.equals("")) {
                            continue;
                        }
                        if (rowNum == 0) {
                            list.add(cell.toString().trim());
                        } else {
                            //每列,每列的数值
                            if (i >= list.size()) {
                                break;
                            }
                            map.put(list.get(i), getStringVal(cell));
                        }
                    }
                    if (!map.isEmpty()) {
                        result.add(map);
                    }
                }

            }
        }
        return result;
    }

    public static List<Map> readXls(InputStream input) throws IOException {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(input);
        List<Map> result = new ArrayList<Map>();
        //保存第一行
        List list = new ArrayList();
        // 获取每一个工作薄
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // 获取当前工作薄的每一行
            for (int rowNum = 0; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    int minCellNum = hssfRow.getFirstCellNum();
                    int maxCellNum = hssfRow.getLastCellNum();

                    Map map = new HashMap();

                    //循环每一列
                    for (int i = minCellNum; i < maxCellNum; i++) {
                        //读取每列数据
                        HSSFCell cell = hssfRow.getCell(i);
                        if (cell == null || cell.equals("")) {
                            continue;
                        }
                        if (rowNum == 0) {
                            list.add(cell.toString().trim());
                        } else {
                            //每列,每列的数值
                            if (i >= list.size()) {
                                break;
                            }
                            map.put(list.get(i), getStringVal(cell));
                        }
                    }
                    if (!map.isEmpty()) {
                        result.add(map);
                    }
                }
            }
        }
        return result;
    }

    private static String getStringVal(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue() ? "TRUE" : "FALSE";
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula();
            case Cell.CELL_TYPE_NUMERIC:
                cell.setCellType(Cell.CELL_TYPE_STRING);
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            default:
                return null;
        }
    }

}