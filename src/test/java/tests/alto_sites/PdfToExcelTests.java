package tests.alto_sites;

import base_tests.AltoSitesBaseTest;
import data.alto_site.AltoSiteDocs;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.alto_sites.AltoPdfToExcelPage;

import java.util.List;

import static java.util.Arrays.asList;
import static pages.alto_sites.AltoSitesBasePage.*;

@Feature("PDF2EXCEL")
public class PdfToExcelTests extends AltoSitesBaseTest {

    List<AltoSiteDocs> docs = asList(
            AltoSiteDocs.BIG_PDF,
            AltoSiteDocs.TWO_MB_PDF,
            AltoSiteDocs.MANY_PAGE_PDF,
            AltoSiteDocs.BROKEN_PDF,
            AltoSiteDocs.PROTECTED_PDF,
            AltoSiteDocs.TEN_MB_PDF,
            AltoSiteDocs.SMALL_PDF
    );
    private AltoPdfToExcelPage pdfToExcelPage;

    @BeforeMethod
    public void getPdfToExcelPage() {
        pdfToExcelPage = new AltoPdfToExcelPage(driver);
        pdfToExcelPage.navigateToPage();
        pdfToExcelPage.isOpened();
    }

    @BeforeTest
    public void altoFileDownload() {
        getPdfToExcelPage();
        pdfToExcelPage.downloadFileCloud(docs);
        downloadFileSelenoid(docs);
        pdfToExcelPage.checkUploadFromSelenoid(docs);
    }

    @Story("PDF2EXCEL#001")
    @Test
    public void basicFlowConvert() {
        pdfToExcelPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToExcelPage.convertNow();
        pdfToExcelPage.download();
        //   pdfToExcelPage.checkDownload("smallpdffile.xlsx");
    }

    @Story("PDF2EXCEL#002")
    @Test
    public void checkValidationMany150Page() {
        pdfToExcelPage.chooseFile(AltoSiteDocs.MANY_PAGE_PDF.getFilePath());
        pdfToExcelPage.checkValidationForPages();
        pdfToExcelPage.closeNotificationError();
    }

    @Story("PDF2EXCEL#003")
    @Test
    public void checkValidationToUploadBrokenFile() {
        pdfToExcelPage.chooseFile(AltoSiteDocs.BROKEN_PDF.getFilePath());
        pdfToExcelPage.checkValidationForPages();
        pdfToExcelPage.closeNotificationError();
    }

    @Story("PDF2EXCEL#004")
    @Test
    public void deleteFileAfterUpload() {
        pdfToExcelPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToExcelPage.checkEnabledButtonConvertNow();
        pdfToExcelPage.deleteFileWithUseIconTrash();
        pdfToExcelPage.checkEnabledButtonChooseFile();
    }

    @Story("PDF2EXCEL#005")
    @Test
    public void deleteFileDuringUploadIconTrash() {
        pdfToExcelPage.chooseFile(AltoSiteDocs.TEN_MB_PDF.getFilePath());
        pdfToExcelPage.deleteFileWithUseIconTrash();
        pdfToExcelPage.checkEnabledButtonChooseFile();
    }

    @Story("PDF2EXCEL#006")
    @Test(priority = 1)
    public void deleteFileDuringUploadIconX() {
        pdfToExcelPage.chooseFile(AltoSiteDocs.TEN_MB_PDF.getFilePath());
        pdfToExcelPage.deleteFileWithUseIconX();
        pdfToExcelPage.checkEnabledButtonChooseFile();
    }

    @Story("PDF2EXCEL#007")
    @Test
    public void cancelConvert() {
        pdfToExcelPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToExcelPage.convertNow();
        pdfToExcelPage.cancelConvert();
        pdfToExcelPage.checkEnabledButtonChooseFile();
    }

    @Story("PDF2EXCEL#008")
    @Test
    public void checkValidationForUploadFileMore25MB() {
        pdfToExcelPage.chooseFile(AltoSiteDocs.BIG_PDF.getFilePath());
        pdfToExcelPage.checkValidationForPages();
        pdfToExcelPage.closeNotificationError();
    }

    @Story("PDF2EXCEL#009")
    @Test
    public void checkValidationForUploadProtectFile() {
        pdfToExcelPage.chooseFile(AltoSiteDocs.PROTECTED_PDF.getFilePath());
        pdfToExcelPage.checkValidationForPages();
        pdfToExcelPage.closeNotificationError();
    }

    @Story("PDF2EXCEL#010")
    @Test
    public void checkWorkButtonStartOverAgain() {
        pdfToExcelPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        pdfToExcelPage.convertNow();
        pdfToExcelPage.startOverAgain();
        pdfToExcelPage.checkEnabledButtonChooseFile();
    }

    @Story("PDF2EXCEL#011")
    @Test
    public void checkRedirectPdffillerSaveDocumentAs() {
        pdfToExcelPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToExcelPage.convertNow();
        pdfToExcelPage.goToPdffiller(SAVE);
        pdfToExcelPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PDF2EXCEL#012")
    @Test
    public void checkRedirectPdffillerEditDocuments() {
        pdfToExcelPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        pdfToExcelPage.convertNow();
        pdfToExcelPage.goToPdffiller(EDIT);
        pdfToExcelPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PDF2EXCEL#013")
    @Test
    public void checkRedirectPdffillerSign() {
        pdfToExcelPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToExcelPage.convertNow();
        pdfToExcelPage.goToPdffiller(SIGN);
        pdfToExcelPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PDF2EXCEL#014")
    @Test
    public void checkRedirectPdffillerShare() {
        pdfToExcelPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        pdfToExcelPage.convertNow();
        pdfToExcelPage.goToPdffiller(SHARE);
        pdfToExcelPage.checkRedirectPdffillerWithPdf();
    }
}
