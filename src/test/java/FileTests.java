import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.xlstest.XLS;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.pdftest.assertj.Assertions.assertThat;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

public class FileTests extends BaseTest {

    List<String> listMonths = new ArrayList<>() {{
        add("January");
        add("February");
        add("March");
        add("April");
        add("June");
        add("July");
        add("August");
        add("October");
        add("December");
    }};

    @Test
    @DisplayName("Загрузка файла")
    void uploadFile() {
        open("https://cgi-lib.berkeley.edu/ex/fup.html");
        $("[name='upfile']").uploadFromClasspath("sample1.txt");
        $("[type='submit']").click();
        $("pre").shouldHave(Condition.text("Utilitatis causa amicitia est quaesita."));
    }

    @Test
    @DisplayName("Скачивание txt файла и проверка его содержимого")
    void downloadTxt() throws IOException {
        open("https://filesamples.com/formats/txt");
        File txtFile = $("[href='/samples/document/txt/sample1.txt']").download();
        String txtContent = IOUtils.toString(new FileReader(txtFile));
        assertTrue(txtContent.contains("Utilitatis causa amicitia est quaesita."));
    }

    @Test
    @DisplayName("Скачивание pdf файла и проверка его содержимого")
    void downloadPdf() throws IOException {
        open("https://filesamples.com/formats/pdf");
        File pdfFile = $("[href='/samples/document/pdf/sample2.pdf']").download(6000);
        PDF pdfContent = new PDF(pdfFile);
        assertThat(pdfContent).containsExactText("PDF Form Example");
        assertEquals(1, pdfContent.numberOfPages);
    }

    @Test
    @DisplayName("Скачивание xls файла и проверка его содержимого")
    void downloadXls() throws IOException {
        open("https://filesamples.com/formats/xls");
        File xlsFile = $("[href='/samples/document/xls/sample2.xls']").download();
        XLS parsedXls = new XLS(xlsFile);

        boolean checkPassed = parsedXls.excel
                .getSheetAt(0)
                .getRow(3)
                .getCell(3)
                .getStringCellValue()
                .contains("Politics; Memes; Cats");

        assertTrue(checkPassed);
    }

    @Test
    @DisplayName("Скачивание xlsx файла и проверка его содержимого")
    void downloadXlsx() throws IOException, InvalidFormatException {
        open("https://filesamples.com/formats/xlsx");
        File xlsxFile = $("[href='/samples/document/xlsx/sample3.xlsx']").download();

        XSSFWorkbook parsedXlsx = new XSSFWorkbook(xlsxFile);
        XSSFSheet sheet = parsedXlsx.getSheetAt(0);
        int rowNumber = sheet.getPhysicalNumberOfRows();
        XSSFRow row;
        XSSFCell cell;

        ArrayList<String> monthFromXlsx = new ArrayList<>();

        for (int i = 1; i < rowNumber; i++) {
            row = sheet.getRow(i);
            cell = row.getCell(3);
            String data = cell.getStringCellValue();
            if (!data.equals(""))
                monthFromXlsx.add(data);
        }

        assertThat(monthFromXlsx).containsExactlyInAnyOrderElementsOf(listMonths);
    }

    @Test
    @DisplayName("Скачивание docx файла и проверка его содержимого")
    void downloadDocx() throws IOException, InvalidFormatException {
        open("https://filesamples.com/formats/docx");
        File docFile = $("[href='/samples/document/docx/sample3.docx']").download();
        XWPFDocument document = new XWPFDocument(OPCPackage.open(docFile));
        XWPFWordExtractor we = new XWPFWordExtractor(document);
        String text = we.getText();
        assertTrue(text.contains("There are eight section headings in this document. At the beginning, \"Sample Document\" is a level 1 heading."));
    }
}