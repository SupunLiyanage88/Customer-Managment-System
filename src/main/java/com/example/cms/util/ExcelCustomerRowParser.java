package com.example.cms.util;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public final class ExcelCustomerRowParser {

    public interface RowConsumer {
        void accept(ExcelCustomerRow row);
    }

    private ExcelCustomerRowParser() {
    }

    public static void parse(Path file, RowConsumer consumer) throws Exception {
        try (OPCPackage packageHandle = OPCPackage.open(file.toFile(), PackageAccess.READ)) {
            XSSFReader reader = new XSSFReader(packageHandle);
            StylesTable styles = reader.getStylesTable();
            ReadOnlySharedStringsTable sharedStrings = new ReadOnlySharedStringsTable(packageHandle);
            XMLReader parser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            CustomerSheetHandler sheetHandler = new CustomerSheetHandler(consumer);
            XSSFSheetXMLHandler sheetXmlHandler = new XSSFSheetXMLHandler(styles, sharedStrings, sheetHandler, new DataFormatter(), false);
            parser.setContentHandler(sheetXmlHandler);
            Iterator<InputStream> sheets = reader.getSheetsData();
            if (sheets.hasNext()) {
                try (InputStream sheet = sheets.next()) {
                    parser.parse(new InputSource(sheet));
                }
            }
        }
    }

    private static class CustomerSheetHandler implements SheetContentsHandler {

        private final RowConsumer consumer;
        private final Map<Integer, String> headerColumns = new HashMap<Integer, String>();
        private final Map<Integer, String> rowValues = new HashMap<Integer, String>();

        private CustomerSheetHandler(RowConsumer consumer) {
            this.consumer = consumer;
        }

        @Override
        public void startRow(int rowNum) {
            rowValues.clear();
        }

        @Override
        public void endRow(int rowNum) {
            if (rowNum == 0) {
                for (Map.Entry<Integer, String> entry : rowValues.entrySet()) {
                    headerColumns.put(entry.getKey(), normalize(entry.getValue()));
                }
                return;
            }

            String name = getValue("name");
            String dateOfBirthText = getValue("dateofbirth");
            String nicNumber = getValue("nicnumber");

            if ((name == null || name.trim().isEmpty()) && (dateOfBirthText == null || dateOfBirthText.trim().isEmpty())
                    && (nicNumber == null || nicNumber.trim().isEmpty())) {
                return;
            }

            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name is required in row " + (rowNum + 1));
            }
            if (dateOfBirthText == null || dateOfBirthText.trim().isEmpty()) {
                throw new IllegalArgumentException("Date of birth is required in row " + (rowNum + 1));
            }
            if (nicNumber == null || nicNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("NIC number is required in row " + (rowNum + 1));
            }

            consumer.accept(new ExcelCustomerRow(name.trim(), parseDate(dateOfBirthText.trim(), rowNum + 1), nicNumber.trim()));
        }

        @Override
        public void cell(String cellReference, String formattedValue, XSSFComment comment) {
            int columnIndex = columnIndex(cellReference);
            rowValues.put(Integer.valueOf(columnIndex), formattedValue);
        }

        @Override
        public void headerFooter(String text, boolean isHeader, String tagName) {
        }

        private String getValue(String headerName) {
            for (Map.Entry<Integer, String> entry : headerColumns.entrySet()) {
                if (headerName.equals(entry.getValue())) {
                    return rowValues.get(entry.getKey());
                }
            }
            return null;
        }

        private String normalize(String text) {
            return text == null ? null : text.replace(" ", "").toLowerCase(Locale.ENGLISH);
        }

        private int columnIndex(String cellReference) {
            int index = 0;
            while (index < cellReference.length() && Character.isLetter(cellReference.charAt(index))) {
                index++;
            }
            String columnName = cellReference.substring(0, index).toUpperCase(Locale.ENGLISH);
            int columnNumber = 0;
            for (int i = 0; i < columnName.length(); i++) {
                columnNumber = columnNumber * 26 + (columnName.charAt(i) - 'A' + 1);
            }
            return columnNumber - 1;
        }

        private LocalDate parseDate(String text, int rowNumber) {
            try {
                return LocalDate.parse(text);
            } catch (Exception ignored) {
            }

            try {
                return LocalDate.parse(text, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception ignored) {
            }

            try {
                return LocalDate.parse(text, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            } catch (Exception ignored) {
            }

            throw new IllegalArgumentException("Invalid date of birth in row " + rowNumber + ": " + text);
        }
    }
}