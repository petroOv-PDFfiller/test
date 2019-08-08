package tests.alto_sites;

import base_tests.AltoSitesBaseTest;
import data.alto_site.AltoSiteDocs;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.alto_sites.AltoPdfToPptPage;

import java.util.List;

import static java.util.Arrays.asList;
import static pages.alto_sites.AltoSitesBasePage.*;

@Feature("PDF2PPT")
public class PdfToPptTest extends AltoSitesBaseTest {
    List<AltoSiteDocs> docs = asList(
            AltoSiteDocs.BIG_PDF,
            AltoSiteDocs.TWO_MB_PDF,
            AltoSiteDocs.MANY_PAGE_PDF,
            AltoSiteDocs.BROKEN_PDF,
            AltoSiteDocs.PROTECTED_PDF,
            AltoSiteDocs.TEN_MB_PDF,
            AltoSiteDocs.SMALL_PDF
    );
    private AltoPdfToPptPage pdfToPptPage;

    @BeforeMethod
    public void getPdfToPptPage() {
        pdfToPptPage = new AltoPdfToPptPage(driver);
        pdfToPptPage.navigateToPage();
        pdfToPptPage.isOpened();
    }

    @BeforeTest
    public void altoFileDownload() {
        getPdfToPptPage();
        pdfToPptPage.downloadFileCloud(docs);
        downloadFileSelenoid(docs);
        pdfToPptPage.checkUploadFromSelenoid(docs);
    }

    @Story("PDF2PPT#001")
    @Test
    public void basicFlowConvert() {
        pdfToPptPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToPptPage.convertNow();
        pdfToPptPage.download();
        //   pdfToPptPage.checkDownload("smallpdffile.pptx");
    }

    @Story("PDF2PPT#002")
    @Test
    public void checkValidationMany150Page() {
        pdfToPptPage.chooseFile(AltoSiteDocs.MANY_PAGE_PDF.getFilePath());
        pdfToPptPage.checkValidationForPages();
        pdfToPptPage.closeNotificationError();
    }

    @Story("PDF2PPT#003")
    @Test
    public void checkValidationToUploadBrokenFile() {
        pdfToPptPage.chooseFile(AltoSiteDocs.BROKEN_PDF.getFilePath());
        pdfToPptPage.checkValidationForPages();
        pdfToPptPage.closeNotificationError();
    }

    @Story("PDF2PPT#004")
    @Test
    public void deleteFileAfterUpload() {
        pdfToPptPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToPptPage.checkEnabledButtonConvertNow();
        pdfToPptPage.deleteFileWithUseIconTrash();
        pdfToPptPage.checkEnabledButtonChooseFile();
    }

    @Story("PDF2PPT#005")
    @Test
    public void deleteFileDuringUploadIconTrash() {
        pdfToPptPage.chooseFile(AltoSiteDocs.TEN_MB_PDF.getFilePath());
        pdfToPptPage.deleteFileWithUseIconTrash();
    }

    @Story("PDF2PPT#006")
    @Test(priority = 1)
    public void deleteFileDuringUploadIconX() {
        pdfToPptPage.chooseFile(AltoSiteDocs.TEN_MB_PDF.getFilePath());
        pdfToPptPage.deleteFileWithUseIconX();
    }

    @Story("PDF2PPT#007")
    @Test
    public void cancelConvert() {
        pdfToPptPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        pdfToPptPage.convertNow();
        pdfToPptPage.cancelConvert();
        pdfToPptPage.checkEnabledButtonChooseFile();
    }

    @Story("PDF2PPT#008")
    @Test
    public void checkValidationForUploadFileMore25MB() {
        pdfToPptPage.chooseFile(AltoSiteDocs.BIG_PDF.getFilePath());
        pdfToPptPage.checkValidationForPages();
        pdfToPptPage.closeNotificationError();
    }

    @Story("PDF2PPT#009")
    @Test
    public void checkValidationForUploadProtectFile() {
        pdfToPptPage.chooseFile(AltoSiteDocs.PROTECTED_PDF.getFilePath());
        pdfToPptPage.checkValidationForPages();
        pdfToPptPage.closeNotificationError();
    }

    @Story("PDF2PPT#010")
    @Test
    public void checkWorkButtonStartOverAgain() {
        pdfToPptPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        pdfToPptPage.convertNow();
        pdfToPptPage.startOverAgain();
        pdfToPptPage.checkEnabledButtonChooseFile();
    }

    @Story("PDF2PPT#011")
    @Test
    public void checkRedirectPdffillerSaveDocumentAs() {
        pdfToPptPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToPptPage.convertNow();
        pdfToPptPage.goToPdffiller(SAVE);
        pdfToPptPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PDF2PPT#012")
    @Test
    public void checkRedirectPdffillerEditDocuments() {
        pdfToPptPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        pdfToPptPage.convertNow();
        pdfToPptPage.goToPdffiller(EDIT);
        pdfToPptPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PDF2PPT#013")
    @Test
    public void checkRedirectPdffillerSign() {
        pdfToPptPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        pdfToPptPage.convertNow();
        pdfToPptPage.goToPdffiller(SIGN);
        pdfToPptPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PDF2PPT#014")
    @Test
    public void checkRedirectPdffillerShare() {
        pdfToPptPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        pdfToPptPage.convertNow();
        pdfToPptPage.goToPdffiller(SHARE);
        pdfToPptPage.checkRedirectPdffillerWithPdf();
    }
}
