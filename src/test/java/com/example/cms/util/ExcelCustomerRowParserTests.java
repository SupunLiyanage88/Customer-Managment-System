package com.example.cms.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

class ExcelCustomerRowParserTests {

    @Test
    void parsesRowsFromSingleSheetWithHeaderFirstRow() throws Exception {
        Path file = Files.createTempFile("customer-import-test-", ".xlsx");
        try {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                Sheet customerSheet = workbook.createSheet("Customers");

                Row headerRow = customerSheet.createRow(0);
                headerRow.createCell(0).setCellValue("Name");
                headerRow.createCell(1).setCellValue("Date of Birth");
                headerRow.createCell(2).setCellValue("NIC Number");

                Row dataRow = customerSheet.createRow(1);
                dataRow.createCell(0).setCellValue("Nimal Perera");
                dataRow.createCell(1).setCellValue("1990-05-12");
                dataRow.createCell(2).setCellValue("NIC-0001");

                try (OutputStream outputStream = Files.newOutputStream(file)) {
                    workbook.write(outputStream);
                }
            }

            List<ExcelCustomerRow> rows = new ArrayList<ExcelCustomerRow>();
            ExcelCustomerRowParser.parse(file, rows::add);

            assertThat(rows).hasSize(1);
            assertThat(rows.get(0).getName()).isEqualTo("Nimal Perera");
            assertThat(rows.get(0).getNicNumber()).isEqualTo("NIC-0001");
            assertThat(rows.get(0).getDateOfBirth()).hasToString("1990-05-12");
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void parsesRowsFromLaterSheetWithHeaderBelowIntroRow() throws Exception {
        Path file = Files.createTempFile("customer-import-test-", ".xlsx");
        try {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                Sheet introSheet = workbook.createSheet("Intro");
                introSheet.createRow(0).createCell(0).setCellValue("Read me first");

                Sheet customerSheet = workbook.createSheet("Customers");
                customerSheet.createRow(0).createCell(0).setCellValue("Bulk customer import");

                Row headerRow = customerSheet.createRow(1);
                headerRow.createCell(0).setCellValue("Name");
                headerRow.createCell(1).setCellValue("Date of Birth");
                headerRow.createCell(2).setCellValue("NIC Number");

                Row dataRow = customerSheet.createRow(2);
                dataRow.createCell(0).setCellValue("Nimal Perera");
                dataRow.createCell(1).setCellValue("1990-05-12");
                dataRow.createCell(2).setCellValue("NIC-0001");

                try (OutputStream outputStream = Files.newOutputStream(file)) {
                    workbook.write(outputStream);
                }
            }

            List<ExcelCustomerRow> rows = new ArrayList<ExcelCustomerRow>();
            ExcelCustomerRowParser.parse(file, rows::add);

            assertThat(rows).hasSize(1);
            assertThat(rows.get(0).getName()).isEqualTo("Nimal Perera");
            assertThat(rows.get(0).getNicNumber()).isEqualTo("NIC-0001");
            assertThat(rows.get(0).getDateOfBirth()).hasToString("1990-05-12");
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
