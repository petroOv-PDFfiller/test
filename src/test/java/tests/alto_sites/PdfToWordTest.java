package tests.alto_sites;

import base_tests.AltoSitesBaseTest;
import data.alto_site.AltoSiteDocs;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.alto_sites.AltoPdfToWordPage;

import java.util.List;

import static java.util.Arrays.asList;
import static pages.alto_sites.AltoSitesBasePage.*;

@Feature("PDF2WORD")
public class PdfToWordTest extends AltoSitesBaseTest {
    List<AltoSiteDocs> docs = asList(
            AltoSiteDocs.BIG_PDF,
            AltoSiteDocs.TWO_MB_PDF,
            AltoSiteDocs.MANY_PAGE_PDF,
            AltoSiteDocs.BROKEN_PDF,
            AltoSiteDocs.PROTECTED_PDF,
            AltoSiteDocs.TEN_MB_PDF,
            AltoSiteDocs.SMALL_PDF
    );
    private AltoPdfToWordPage pdfToWordPage;

    @BeforeMethod
    public void getPdfToWordPage() {
        pdfToWordPage = new AltoPdfToWordPage(driver);
        pdfToWordPage.navigateToPage();
        pdfToWordPage.isOpened();
    }

    @BeforeTest
    public void altoFileDownload() {
        getPdfToWordPage();
        pdfToWordPage.downloadFileCloud(docs);
        downloadFileSelenoid(docs);
        pdfToWordPage.checkUploadFromSelenoid(docs);
    }

    @Story("PDF2WORD#001")
    @Test
    public void basicFlowConvert() {
        pdfToWordPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToWordPage.convertNow();
        pdfToWordPage.download();
        //  pdfToWordPage.checkDownload("smallpdffile.docx");
    }

    @Story("PDF2WORD#002")
    @Test
    public void checkValidationMany150Page() {
        pdfToWordPage.chooseFile(AltoSiteDocs.MANY_PAGE_PDF.getFilePath());
        pdfToWordPage.checkValidationForPages();
        pdfToWordPage.closeNotificationError();
    }

    @Story("PDF2WORD#003")
    @Test
    public void checkValidationToUploadBrokenFile() {
        pdfToWordPage.chooseFile(AltoSiteDocs.BROKEN_PDF.getFilePath());
        pdfToWordPage.checkValidationForPages();
        pdfToWordPage.closeNotificationError();
    }

    @Story("PDF2WORD#004")
    @Test
    public void deleteFileAfterUpload() {
        pdfToWordPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToWordPage.checkEnabledButtonConvertNow();
        pdfToWordPage.deleteFileWithUseIconTrash();
        pdfToWordPage.checkEnabledButtonChooseFile();
    }

    @Story("PDF2WORD#005")
    @Test
    public void deleteFileDuringUploadIconTrash() {
        pdfToWordPage.chooseFile(AltoSiteDocs.TEN_MB_PDF.getFilePath());
        pdfToWordPage.deleteFileWithUseIconTrash();
    }

    @Story("PDF2WORD#006")
    @Test(priority = 1)
    public void deleteFileDuringUploadIconX() {
        pdfToWordPage.chooseFile(AltoSiteDocs.TEN_MB_PDF.getFilePath());
        pdfToWordPage.deleteFileWithUseIconX();
    }


    @Story("PDF2WORD#007")
    @Test
    public void cancelConvert() {
        pdfToWordPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        pdfToWordPage.convertNow();
        pdfToWordPage.cancelConvert();
        pdfToWordPage.checkEnabledButtonChooseFile();
    }

    @Story("PDF2WORD#008")
    @Test
    public void checkValidationForUploadFileMore25MB() {
        pdfToWordPage.chooseFile(AltoSiteDocs.BIG_PDF.getFilePath());
        pdfToWordPage.checkValidationForPages();
        pdfToWordPage.closeNotificationError();
    }

    @Story("PDF2WORD#009")
    @Test
    public void checkValidationForUploadProtectFile() {
        pdfToWordPage.chooseFile(AltoSiteDocs.PROTECTED_PDF.getFilePath());
        pdfToWordPage.checkValidationForPages();
        pdfToWordPage.closeNotificationError();
    }

    @Story("PDF2WORD#010")
    @Test
    public void checkWorkButtonStartOverAgain() {
        pdfToWordPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToWordPage.convertNow();
        pdfToWordPage.startOverAgain();
        pdfToWordPage.checkEnabledButtonChooseFile();
    }

    @Story("PDF2WORD#011")
    @Test
    public void checkRedirectPdffillerSaveDocumentAs() {
        pdfToWordPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToWordPage.convertNow();
        pdfToWordPage.goToPdffiller(SAVE);
        pdfToWordPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PDF2WORD#012")
    @Test
    public void checkRedirectPdffillerEditDocuments() {
        pdfToWordPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        pdfToWordPage.convertNow();
        pdfToWordPage.goToPdffiller(EDIT);
        pdfToWordPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PDF2WORD#013")
    @Test
    public void checkRedirectPdffillerSign() {
        pdfToWordPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToWordPage.convertNow();
        pdfToWordPage.goToPdffiller(SIGN);
        pdfToWordPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PDF2WORD#014")
    @Test
    public void checkRedirectPdffillerShare() {
        pdfToWordPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        pdfToWordPage.convertNow();
        pdfToWordPage.goToPdffiller(SHARE);
        pdfToWordPage.checkRedirectPdffillerWithPdf();
    }
}
