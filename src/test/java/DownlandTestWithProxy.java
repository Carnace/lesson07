import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.FileDownloadMode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DownlandTestWithProxy extends BaseTest {
    @BeforeEach
    @DisplayName("Включение прокси")
    void openProxy() {
        Configuration.proxyEnabled = true;
        Configuration.fileDownload = FileDownloadMode.FOLDER;
    }

    @Test
    @DisplayName("Скачивание zip файла и проверка его содержимого")
    void downloadZip() throws IOException {
        open("https://stuk.github.io/jszip/documentation/examples/download-zip-file.html");
        File zipFile = $("#blob").download();
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            String name;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();
                assertTrue(name.contains("Hello.txt"));
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @AfterEach
    @DisplayName("Отключение прокси")
    void closeProxy() {
        Configuration.proxyEnabled = false;
    }
}
