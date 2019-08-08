package tests.alto_sites;

import base_tests.AltoSitesBaseTest;
import data.alto_site.AltoSiteDocs;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.alto_sites.AltoCompressPage;

import java.util.List;

import static java.util.Arrays.asList;
import static pages.alto_sites.AltoSitesBasePage.*;

@Feature("Compress")
public class CompressTests extends AltoSitesBaseTest {

    List<AltoSiteDocs> docs = asList(
            AltoSiteDocs.BIG_PDF,
            AltoSiteDocs.TWO_MB_PDF,
            AltoSiteDocs.MANY_PAGE_PDF,
            AltoSiteDocs.BROKEN_PDF,
            AltoSiteDocs.PROTECTED_PDF,
            AltoSiteDocs.TEN_MB_PDF,
            AltoSiteDocs.SMALL_PDF
    );
    private AltoCompressPage compressPage;

    @BeforeMethod
    public void getCompressPage() {
        compressPage = new AltoCompressPage(driver);
        compressPage.navigateToPage();
        compressPage.isOpened();
    }

    @BeforeTest
    public void altoFileDownload() {
        getCompressPage();
        compressPage.downloadFileCloud(docs);
        downloadFileSelenoid(docs);
        compressPage.checkUploadFromSelenoid(docs);
    }

    @Story("COMPRESS#001")
    @Test
    public void basicFlowConvert() {
        compressPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        compressPage.convertNow();
        compressPage.download();
        //     compressPage.checkDownload("Compressed.pdf");
    }

    @Story("COMPRESS#002")
    @Test
    public void checkValidationMany150Page() {
        compressPage.chooseFile(AltoSiteDocs.MANY_PAGE_PDF.getFilePath());
        compressPage.checkValidationForPages();
        compressPage.closeNotificationError();

    }

    @Story("COMPRESS#003")
    @Test
    public void checkValidationToUploadBrokenFile() {
        compressPage.chooseFile(AltoSiteDocs.BROKEN_PDF.getFilePath());
        compressPage.checkValidationForPages();
        compressPage.closeNotificationError();
    }

    @Story("COMPRESS#004")
    @Test
    public void deleteFileAfterUpload() {
        compressPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        compressPage.checkEnabledButtonConvertNow();
        compressPage.deleteFileWithUseIconTrash();
        compressPage.checkEnabledButtonChooseFile();
    }

    @Story("COMPRESS#005")
    @Test
    public void deleteFileDuringUploadIconTrash() {
        compressPage.chooseFile(AltoSiteDocs.TEN_MB_PDF.getFilePath());
        compressPage.deleteFileWithUseIconTrash();
    }

    @Story("COMPRESS#006")
    @Test(priority = 1)
    public void deleteFileDuringUploadIconX() {
        compressPage.chooseFile(AltoSiteDocs.TEN_MB_PDF.getFilePath());
        compressPage.deleteFileWithUseIconX();
    }

    @Story("COMPRESS#007")
    @Test
    public void cancelConvert() {
        compressPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        compressPage.convertNow();
        compressPage.cancelConvert();
        compressPage.checkEnabledButtonChooseFile();
    }

    @Story("COMPRESS#008")
    @Test
    public void checkValidationForUploadFileMore25MB() {
        compressPage.chooseFile(AltoSiteDocs.BIG_PDF.getFilePath());
        compressPage.checkValidationForPages();
        compressPage.closeNotificationError();
    }

    @Story("COMPRESS#009")
    @Test
    public void checkValidationForUploadProtectFile() {
        compressPage.chooseFile(AltoSiteDocs.PROTECTED_PDF.getFilePath());
        compressPage.checkValidationForPages();
        compressPage.closeNotificationError();
    }

    @Story("COMPRESS#010")
    @Test
    public void checkWorkButtonStartOverAgain() {
        compressPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        compressPage.convertNow();
        compressPage.startOverAgain();
        compressPage.checkEnabledButtonChooseFile();
    }

    @Story("COMPRESS#011")
    @Test
    public void checkRedirectPdffillerSaveDocumentAs() {
        compressPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        compressPage.convertNow();
        compressPage.goToPdffiller(SAVE);
        compressPage.checkRedirectPdffillerWithPdf();
    }

    @Story("COMPRESS#012")
    @Test
    public void checkRedirectPdffillerEditDocuments() {
        compressPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        compressPage.convertNow();
        compressPage.goToPdffiller(EDIT);
        compressPage.checkRedirectPdffillerWithPdf();
    }

    @Story("COMPRESS#013")
    @Test
    public void checkRedirectPdffillerSign() {
        compressPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        compressPage.convertNow();
        compressPage.goToPdffiller(SIGN);
        compressPage.checkRedirectPdffillerWithPdf();
    }

    @Story("COMPRESS#014")
    @Test
    public void checkRedirectPdffillerShare() {
        compressPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        compressPage.convertNow();
        compressPage.goToPdffiller(SHARE);
        compressPage.checkRedirectPdffillerWithPdf();
    }
}