package com.example.ChessTournamentBot.util;

import com.example.ChessTournamentBot.entity.PlayerEntity;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.List;

@Slf4j
public class ExcelFileCreator {
    public static File createExcelFile(List<PlayerEntity> playerList) {
                try (XSSFWorkbook registrationBook = new XSSFWorkbook()) {
                    int i = 0;

                    Sheet sheet1 = registrationBook.createSheet("RegistrationList");

                    XSSFRow firstRow = (XSSFRow) sheet1.createRow(0);
                    firstRow.createCell(0).setCellValue("Tg_username");
                    firstRow.createCell(1).setCellValue("Chess_nickname");
                    firstRow.createCell(2).setCellValue("Control");
                    firstRow.createCell(3).setCellValue("F_date");

                    for (PlayerEntity player : playerList) {
                        XSSFRow row = (XSSFRow) sheet1.createRow(++i);
                        row.createCell(0).setCellValue(new XSSFRichTextString(player.getUsernameTg()));
                        row.createCell(1).setCellValue(new XSSFRichTextString(player.getChess_nickname()));
                        row.createCell(2).setCellValue(new XSSFRichTextString(player.getControl()));
                        row.createCell(3).setCellValue(new XSSFRichTextString(player.getF_date()));
                    }
                    registrationBook.write(new FileOutputStream("D:\\Github\\JavaProject\\ChessTournamentBot\\targetFile.xlsx"));
                } catch (IOException e) {
                    log.error("Error with XSSFWorkbook " + e.getMessage());
                }
        return new File("D:\\Github\\JavaProject\\ChessTournamentBot\\targetFile.xlsx");
    }
}