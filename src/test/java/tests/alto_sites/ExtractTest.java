package tests.alto_sites;

import base_tests.AltoSitesBaseTest;
import data.alto_site.AltoSiteDocs;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.alto_sites.AltoExtractPage;

import java.util.List;

import static java.util.Arrays.asList;
import static pages.alto_sites.AltoSitesBasePage.*;

@Feature("EXTRACTPDF")
public class ExtractTest extends AltoSitesBaseTest {

    List<AltoSiteDocs> docs = asList(
            AltoSiteDocs.BIG_PDF,
            AltoSiteDocs.TWO_MB_PDF,
            AltoSiteDocs.MANY_PAGE_PDF,
            AltoSiteDocs.BROKEN_PDF,
            AltoSiteDocs.PROTECTED_PDF,
            AltoSiteDocs.TEN_MB_PDF,
            AltoSiteDocs.SMALL_PDF
    );
    private AltoExtractPage extractPdfPage;

    @BeforeMethod
    public void getExtractPdfPage() {
        extractPdfPage = new AltoExtractPage(driver);
        extractPdfPage.navigateToPage();
        extractPdfPage.isOpened();
    }

    @BeforeTest
    public void altoFileDownload() {
        getExtractPdfPage();
        extractPdfPage.downloadFileCloud(docs);
        downloadFileSelenoid(docs);
        extractPdfPage.checkUploadFromSelenoid(docs);
    }

    @Story("EXTRACTPDF#001")
    @Test
    public void basicFlowConvert() {
        extractPdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        extractPdfPage.convertNow();
        extractPdfPage.download();
//        extractPdfPage.checkDownload("Extracted.pdf");
    }

    @Story("EXTRACTPDF#002")
    @Test
    public void flowConvertWithSelectRangePages() {
        extractPdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        extractPdfPage.typePageRange("2");
        extractPdfPage.convertNow();
        extractPdfPage.download();
        //     extractPdfPage.checkDownload("Extracted.pdf");
    }

    @Story("EXTRACTPDF#003")
    @Test
    public void checkValidationMany150Page() {
        extractPdfPage.chooseFile(AltoSiteDocs.MANY_PAGE_PDF.getFilePath());
        extractPdfPage.checkValidationForPages();
        extractPdfPage.closeNotificationError();
    }

    @Story("EXTRACTPDF#004")
    @Test
    public void checkValidationToUploadBrokenFile() {
        extractPdfPage.chooseFile(AltoSiteDocs.BROKEN_PDF.getFilePath());
        extractPdfPage.checkValidationForPages();
        extractPdfPage.closeNotificationError();
    }


    @Story("EXTRACTPDF#005")
    @Test
    public void cancelConvert() {
        extractPdfPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        extractPdfPage.convertNow();
        extractPdfPage.cancelConvert();
        extractPdfPage.checkEnabledButtonChooseFile();
    }

    @Story("EXTRACTPDF#006")
    @Test
    public void checkValidationForUploadFileMore25MB() {
        extractPdfPage.chooseFile(AltoSiteDocs.BIG_PDF.getFilePath());
        extractPdfPage.checkValidationForPages();
        extractPdfPage.closeNotificationError();
    }

    @Story("EXTRACTPDF#007")
    @Test
    public void checkValidationForUploadProtectFile() {
        extractPdfPage.chooseFile(AltoSiteDocs.PROTECTED_PDF.getFilePath());
        extractPdfPage.checkValidationForPages();
        extractPdfPage.closeNotificationError();
    }

    @Story("EXTRACTPDF#008")
    @Test
    public void checkWorkButtonStartOverAgain() {
        extractPdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        extractPdfPage.convertNow();
        extractPdfPage.startOverAgain();
        extractPdfPage.checkEnabledButtonChooseFile();
    }

    @Story("EXTRACTPDF#009")
    @Test
    public void checkRedirectPdffillerSaveDocumentAs() {
        extractPdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        extractPdfPage.convertNow();
        extractPdfPage.goToPdffiller(SIGN);
        extractPdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("EXTRACTPDF#010")
    @Test
    public void checkRedirectPdffillerEdit() {
        extractPdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        extractPdfPage.convertNow();
        extractPdfPage.goToPdffiller(EDIT);
        extractPdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("EXTRACTPDF#011")
    @Test
    public void checkRedirectPdffillerSign() {
        extractPdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        extractPdfPage.convertNow();
        extractPdfPage.goToPdffiller(SIGN);
        extractPdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("EXTRACTPDF#012")
    @Test
    public void checkRedirectPdffillerSaveShare() {
        extractPdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        extractPdfPage.convertNow();
        extractPdfPage.goToPdffiller(SHARE);
        extractPdfPage.checkRedirectPdffillerWithPdf();
    }
}
