package tests.alto_sites;

import base_tests.AltoSitesBaseTest;
import data.alto_site.AltoSiteDocs;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.alto_sites.AltoProtectPage;

import java.util.List;

import static java.util.Arrays.asList;
import static pages.alto_sites.AltoSitesBasePage.*;

@Feature("ProtectPDF")
public class ProtectTest extends AltoSitesBaseTest {
    List<AltoSiteDocs> docs = asList(
            AltoSiteDocs.BIG_PDF,
            AltoSiteDocs.TWO_MB_PDF,
            AltoSiteDocs.MANY_PAGE_PDF,
            AltoSiteDocs.BROKEN_PDF,
            AltoSiteDocs.PROTECTED_PDF,
            AltoSiteDocs.TEN_MB_PDF,
            AltoSiteDocs.SMALL_PDF
    );
    private AltoProtectPage protectPage;

    @BeforeMethod
    public void getProtectPage() {
        protectPage = new AltoProtectPage(driver);
        protectPage.navigateToPage();
        protectPage.isOpened();
    }

    @BeforeTest
    public void altoFileDownload() {
        getProtectPage();
        protectPage.downloadFileCloud(docs);
        downloadFileSelenoid(docs);
        protectPage.checkUploadFromSelenoid(docs);
    }

    @Story("ProtectPDF#001")
    @Test
    public void basicFlowProtectPdf() {
        protectPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        protectPage.typePassPdf("123");
        protectPage.convertNow();
        protectPage.download();
        //      protectPage.checkDownload("smallpdffile.pdf");
    }

    @Story("ProtectPDF#002")
    @Test
    public void checkValidationFieldPassword() {
        protectPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        protectPage.typePassPdf("pas@@@");
        protectPage.checkValidationField();
    }

    @Story("ProtectPDF#003")
    @Test
    public void checkValidation150Page() {
        protectPage.chooseFile(AltoSiteDocs.MANY_PAGE_PDF.getFilePath());
        protectPage.checkValidationForPages();
        protectPage.closeNotificationError();
    }

    @Story("ProtectPDF#004")
    @Test
    public void checkValidationToUploadBrokenFile() {
        protectPage.chooseFile(AltoSiteDocs.BROKEN_PDF.getFilePath());
        protectPage.checkValidationForPages();
        protectPage.closeNotificationError();
    }

    @Story("ProtectPDF#005")
    @Test
    public void checkValidationForUploadFileMore25MB() {
        protectPage.chooseFile(AltoSiteDocs.BIG_PDF.getFilePath());
        protectPage.checkValidationForPages();
        protectPage.closeNotificationError();
    }

    @Story("ProtectPDF#006")
    @Test
    public void checkValidationForUploadProtectFile() {
        protectPage.chooseFile(AltoSiteDocs.PROTECTED_PDF.getFilePath());
        protectPage.checkValidationForPages();
        protectPage.closeNotificationError();
    }

    @Story("ProtectPDF#007")
    @Test
    public void checkWorkButtonStartOverAgain() {
        protectPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        protectPage.typePassPdf("2701");
        protectPage.convertNow();
        protectPage.startOverAgain();
    }

    @Story("ProtectPDF#008")
    @Test
    public void checkRedirectPdffillerSaveDocumentAs() {
        protectPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        protectPage.typePassPdf("yulianrusylo");
        protectPage.convertNow();
        protectPage.goToPdffiller(SAVE);
        protectPage.checkRedirectPdffillerWithPdf();
    }

    @Story("ProtectPDF#009")
    @Test
    public void checkRedirectPdffillerEditDocuments() {
        protectPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        protectPage.typePassPdf("yulianrusylo");
        protectPage.convertNow();
        protectPage.goToPdffiller(EDIT);
        protectPage.checkRedirectPdffillerWithPdf();
    }

    @Story("ProtectPDF#010")
    @Test
    public void checkRedirectPdffillerSign() {
        protectPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        protectPage.typePassPdf("yulianrusylo");
        protectPage.convertNow();
        protectPage.goToPdffiller(SIGN);
        protectPage.checkRedirectPdffillerWithPdf();
    }

    @Story("ProtectPDF#011")
    @Test
    public void checkRedirectPdffillerShare() {
        protectPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        protectPage.typePassPdf("yulianrusylo");
        protectPage.convertNow();
        protectPage.goToPdffiller(SHARE);
        protectPage.checkRedirectPdffillerWithPdf();
    }
}
