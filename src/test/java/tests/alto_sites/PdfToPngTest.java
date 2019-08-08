package tests.alto_sites;

import base_tests.AltoSitesBaseTest;
import data.alto_site.AltoSiteDocs;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.alto_sites.AltoPdfToPngPage;

import java.util.List;

import static java.util.Arrays.asList;
import static pages.alto_sites.AltoSitesBasePage.*;

@Feature("PDF2PNG")
public class PdfToPngTest extends AltoSitesBaseTest {

    List<AltoSiteDocs> docs = asList(
            AltoSiteDocs.BIG_PDF,
            AltoSiteDocs.TWO_MB_PDF,
            AltoSiteDocs.MANY_PAGE_PDF,
            AltoSiteDocs.BROKEN_PDF,
            AltoSiteDocs.PROTECTED_PDF,
            AltoSiteDocs.TEN_MB_PDF,
            AltoSiteDocs.SMALL_PDF
    );
    private AltoPdfToPngPage pdfToPngPage;

    @BeforeMethod
    public void getPdfToPngPage() {
        pdfToPngPage = new AltoPdfToPngPage(driver);
        pdfToPngPage.navigateToPage();
        pdfToPngPage.isOpened();
    }

    @BeforeTest
    public void altoFileDownload() {
        getPdfToPngPage();
        pdfToPngPage.downloadFileCloud(docs);
        downloadFileSelenoid(docs);
        pdfToPngPage.checkUploadFromSelenoid(docs);
    }

    @Story("PDF2PNG#001")
    @Test
    public void basicFlowConvert() {
        pdfToPngPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToPngPage.convertNow();
        pdfToPngPage.download();
        //   pdfToPngPage.checkDownload("Converted.zip");
    }

    @Story("PDF2PNG#002")
    @Test
    public void flowConvertWithSelectRangePages() {
        pdfToPngPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        pdfToPngPage.typePageRange("1-3, 7, 13, 24-25");
        pdfToPngPage.convertNow();
        pdfToPngPage.download();
        //  pdfToPngPage.checkDownload("Converted.zip");
    }

    @Story("PDF2PNG#003")
    @Test
    public void checkValidationMany150Page() {
        pdfToPngPage.chooseFile(AltoSiteDocs.MANY_PAGE_PDF.getFilePath());
        pdfToPngPage.checkValidationForPages();
        pdfToPngPage.closeNotificationError();
    }

    @Story("PDF2PNG#004")
    @Test
    public void checkValidationToUploadBrokenFile() {
        pdfToPngPage.chooseFile(AltoSiteDocs.BROKEN_PDF.getFilePath());
        pdfToPngPage.checkValidationForPages();
        pdfToPngPage.closeNotificationError();
    }

    @Story("PDF2PNG#005")
    @Test
    public void cancelConvert() {
        pdfToPngPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        pdfToPngPage.convertNow();
        pdfToPngPage.cancelConvert();
        pdfToPngPage.checkEnabledButtonChooseFile();
    }

    @Story("PDF2PNG#006")
    @Test
    public void checkValidationForUploadFileMore25MB() {
        pdfToPngPage.chooseFile(AltoSiteDocs.BIG_PDF.getFilePath());
        pdfToPngPage.checkValidationForPages();
        pdfToPngPage.closeNotificationError();
    }

    @Story("PDF2PNG#007")
    @Test
    public void checkValidationForUploadProtectFile() {
        pdfToPngPage.chooseFile(AltoSiteDocs.PROTECTED_PDF.getFilePath());
        pdfToPngPage.checkValidationForPages();
        pdfToPngPage.closeNotificationError();
    }

    @Story("PDF2PNG#008")
    @Test
    public void checkWorkButtonStartOverAgain() {
        pdfToPngPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToPngPage.convertNow();
        pdfToPngPage.startOverAgain();
        pdfToPngPage.checkEnabledButtonChooseFile();
    }

    @Story("PDF2PNG#009")
    @Test
    public void checkRedirectPdffillerSaveDocumentAs() {
        pdfToPngPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToPngPage.convertNow();
        pdfToPngPage.goToPdffiller(SAVE);
        pdfToPngPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PDF2PNG#010")
    @Test
    public void checkRedirectPdffillerEditDocuments() {
        pdfToPngPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        pdfToPngPage.convertNow();
        pdfToPngPage.goToPdffiller(EDIT);
        pdfToPngPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PDF2PNG#011")
    @Test
    public void checkRedirectPdffillerSign() {
        pdfToPngPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToPngPage.convertNow();
        pdfToPngPage.goToPdffiller(SIGN);
        pdfToPngPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PDF2PNG#012")
    @Test
    public void checkRedirectPdffillerShare() {
        pdfToPngPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        pdfToPngPage.convertNow();
        pdfToPngPage.goToPdffiller(SHARE);
        pdfToPngPage.checkRedirectPdffillerWithPdf();
    }
}
