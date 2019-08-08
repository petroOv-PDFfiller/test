package base_tests;

import data.TestData;
import data.alto_site.AltoSiteDocs;
import io.qameta.allure.Step;
import listeners.WebTestListener;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Listeners({WebTestListener.class})
public class AltoSitesBaseTest extends PDFfillerTest {
    public static boolean isProduction;
    public WebDriver driver;

    public AltoSitesBaseTest() {
    }

    @Parameters({"isProduction"})
    @BeforeTest
    public void setUpEnvironment(@Optional("true") String isProduction) {
        AltoSitesBaseTest.isProduction = Boolean.valueOf(isProduction);
    }

    @Step("Setup driver")
    @BeforeTest
    public void setUp() {
        this.driver = this.getDriver();
    }

    @Step("Clean file directory")
    @AfterSuite
    public void cleanFileDirectory() throws IOException {
        FileUtils.cleanDirectory(new File(TestData.PATH_TO_DOWNLOADS_FOLDER));
    }

    protected void downloadFileSelenoid(List<AltoSiteDocs> docsList) {
        docsList.forEach(doc -> downloadFileSelenoid(doc.getName()));
    }
}