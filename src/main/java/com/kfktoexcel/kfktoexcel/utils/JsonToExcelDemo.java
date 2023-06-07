package com.kfktoexcel.kfktoexcel.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class JsonToExcelDemo {

    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\chengkui\\Documents\\WeChat Files\\wxid_67a4gaqprec221\\FileStorage\\File\\2023-06\\新建文件夹\\新建文件夹");
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (File file1 : files) {
                if (file1.getName().endsWith(".txt")){
                    String jsonFilePath = file1.getCanonicalPath();
                    String excelFilePath =jsonFilePath.replace(".txt",".xlsx");
                    try {
                        // 读取JSON文件并解析为JSON对象
                        JsonObject jsonObject = readJsonFile(jsonFilePath);

                        // 提取字段并生成Excel文件
                        generateExcelFromJson(jsonObject, excelFilePath);

                        System.out.println("Excel file generated successfully.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static JsonObject readJsonFile(String filePath) throws IOException {
        FileReader fileReader = new FileReader(filePath);
        Gson gson = new Gson();
        return gson.fromJson(fileReader, JsonObject.class);
    }

    private static void generateExcelFromJson(JsonObject jsonObject, String excelFilePath) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        int rowIndex = 0;
        JsonArray rows = jsonObject.getAsJsonArray("rows");
        // 创建表头
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0, CellType.STRING).setCellValue("姓名");
        headerRow.createCell(1, CellType.STRING).setCellValue("身份证号");

        int rownum = 1;
        for (JsonElement js : rows) {
            Row dataRow = sheet.createRow(rownum++);
            JsonObject object = js.getAsJsonObject();
            dataRow.createCell(0, CellType.STRING).setCellValue(object.get("name").getAsString());
            dataRow.createCell(1, CellType.STRING).setCellValue(object.get("idCardNo").getAsString());
        }

        try ( FileOutputStream fileOutputStream = new FileOutputStream(excelFilePath);) {
            workbook.write(fileOutputStream);
        }
        workbook.close();
    }
}

