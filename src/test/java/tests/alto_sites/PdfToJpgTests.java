package tests.alto_sites;

import base_tests.AltoSitesBaseTest;
import data.alto_site.AltoSiteDocs;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.alto_sites.AltoPdfToJpgPage;

import java.util.List;

import static java.util.Arrays.asList;
import static pages.alto_sites.AltoSitesBasePage.*;

@Feature("PDF2JPG")
public class PdfToJpgTests extends AltoSitesBaseTest {

    List<AltoSiteDocs> docs = asList(
            AltoSiteDocs.BIG_PDF,
            AltoSiteDocs.TWO_MB_PDF,
            AltoSiteDocs.MANY_PAGE_PDF,
            AltoSiteDocs.BROKEN_PDF,
            AltoSiteDocs.PROTECTED_PDF,
            AltoSiteDocs.TEN_MB_PDF,
            AltoSiteDocs.SMALL_PDF
    );
    private AltoPdfToJpgPage pdfToJpgPage;

    @BeforeMethod
    public void getPdfToJpgPage() {
        pdfToJpgPage = new AltoPdfToJpgPage(driver);
        pdfToJpgPage.navigateToPage();
        pdfToJpgPage.isOpened();
    }

    @BeforeTest
    public void altoFileDownload() {
        getPdfToJpgPage();
        pdfToJpgPage.downloadFileCloud(docs);
        downloadFileSelenoid(docs);
        pdfToJpgPage.checkUploadFromSelenoid(docs);
    }

    @Story("PDF2JPG#001")
    @Test
    public void basicFlowConvert() {
        pdfToJpgPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToJpgPage.convertNow();
        pdfToJpgPage.download();
        //     pdfToJpgPage.checkDownload("Converted.zip");
    }

    @Story("PDF2JPG#002")
    @Test
    public void flowConvertWithSelectRangePages() {
        pdfToJpgPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        pdfToJpgPage.typeJpgRange("1-3, 7, 13, 24-25");
        pdfToJpgPage.convertNow();
        pdfToJpgPage.download();
        //    pdfToJpgPage.checkDownload("Converted.zip");
    }

    @Story("PDF2JPG#003")
    @Test
    public void checkValidationMany150Page() {
        pdfToJpgPage.chooseFile(AltoSiteDocs.MANY_PAGE_PDF.getFilePath());
        pdfToJpgPage.checkValidationForPages();
        pdfToJpgPage.closeNotificationError();
    }

    @Story("PDF2JPG#004")
    @Test
    public void checkValidationToUploadBrokenFile() {
        pdfToJpgPage.chooseFile(AltoSiteDocs.BROKEN_PDF.getFilePath());
        pdfToJpgPage.checkValidationForPages();
        pdfToJpgPage.closeNotificationError();
    }

    @Story("PDF2JPG#005")
    @Test
    public void cancelConvert() {
        pdfToJpgPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        pdfToJpgPage.convertNow();
        pdfToJpgPage.cancelConvert();
        pdfToJpgPage.checkEnabledButtonChooseFile();
    }

    @Story("PDF2JPG#006")
    @Test
    public void checkValidationForUploadFileMore25MB() {
        pdfToJpgPage.chooseFile(AltoSiteDocs.BIG_PDF.getFilePath());
        pdfToJpgPage.checkValidationForPages();
        pdfToJpgPage.closeNotificationError();
    }

    @Story("PDF2JPG#007")
    @Test
    public void checkValidationForUploadProtectFile() {
        pdfToJpgPage.chooseFile(AltoSiteDocs.PROTECTED_PDF.getFilePath());
        pdfToJpgPage.checkValidationForPages();
        pdfToJpgPage.closeNotificationError();
    }

    @Story("PDF2JPG#008")
    @Test
    public void checkWorkButtonStartOverAgain() {
        pdfToJpgPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToJpgPage.convertNow();
        pdfToJpgPage.startOverAgain();
        pdfToJpgPage.checkEnabledButtonChooseFile();
    }

    @Story("PDF2JPG#009")
    @Test
    public void checkRedirectPdffillerSaveDocumentAs() {
        pdfToJpgPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToJpgPage.convertNow();
        pdfToJpgPage.goToPdffiller(SAVE);
        pdfToJpgPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PDF2JPG#010")
    @Test
    public void checkRedirectPdffillerEditDocuments() {
        pdfToJpgPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        pdfToJpgPage.convertNow();
        pdfToJpgPage.goToPdffiller(EDIT);
        pdfToJpgPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PDF2JPG#011")
    @Test
    public void checkRedirectPdffillerSign() {
        pdfToJpgPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToJpgPage.convertNow();

        pdfToJpgPage.goToPdffiller(SIGN);
        pdfToJpgPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PDF2JPG#012")
    @Test
    public void checkRedirectPdffillerShare() {
        pdfToJpgPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        pdfToJpgPage.convertNow();
        pdfToJpgPage.goToPdffiller(SHARE);
        pdfToJpgPage.checkRedirectPdffillerWithPdf();
    }
}
