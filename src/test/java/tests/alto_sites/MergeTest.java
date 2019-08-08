package tests.alto_sites;

import base_tests.AltoSitesBaseTest;
import data.alto_site.AltoSiteDocs;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.alto_sites.AltoMergePage;

import java.util.List;

import static java.util.Arrays.asList;
import static pages.alto_sites.AltoSitesBasePage.*;


@Feature("MergePDF")
public class MergeTest extends AltoSitesBaseTest {

    List<AltoSiteDocs> docs = asList(
            AltoSiteDocs.BIG_PDF,
            AltoSiteDocs.TWO_MB_PDF,
            AltoSiteDocs.MANY_PAGE_PDF,
            AltoSiteDocs.BROKEN_PDF,
            AltoSiteDocs.PROTECTED_PDF,
            AltoSiteDocs.TEN_MB_PDF,
            AltoSiteDocs.SMALL_PDF
    );
    private AltoMergePage mergePdfPage;

    @BeforeMethod
    public void getMergePdfPage() {
        mergePdfPage = new AltoMergePage(driver);
        mergePdfPage.navigateToPage();
        mergePdfPage.isOpened();
    }

    @BeforeTest
    public void altoFileDownload() {
        getMergePdfPage();
        mergePdfPage.downloadFileCloud(docs);
        downloadFileSelenoid(docs);
        mergePdfPage.checkUploadFromSelenoid(docs);
    }

    @Story("MergePDF#001")
    @Test
    public void basicFlowConvert() {
        mergePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        mergePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        mergePdfPage.convertNow();
        mergePdfPage.download();
        //  mergePdfPage.checkDownload("Merged.pdf");
    }

    @Story("MergePDF#002")
    @Test
    public void checkValidationMany150Page() {
        mergePdfPage.chooseFile(AltoSiteDocs.MANY_PAGE_PDF.getFilePath());
        mergePdfPage.checkValidationForPages();
        mergePdfPage.closeNotificationError();
    }

    @Story("MergePDF#003")
    @Test
    public void checkValidationUploadFirstLowMbFileSecond151Page() {
        mergePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        mergePdfPage.chooseFile(AltoSiteDocs.MANY_PAGE_PDF.getFilePath());
        mergePdfPage.checkValidationForPages();
        mergePdfPage.closeNotificationError();
    }

    @Story("MergePDF#004")
    @Test
    public void checkValidationToUploadBrokenFile() {
        mergePdfPage.chooseFile(AltoSiteDocs.BROKEN_PDF.getFilePath());
        mergePdfPage.checkValidationForPages();
        mergePdfPage.closeNotificationError();
    }

    @Story("MergePDF#005")
    @Test
    public void checkValidationToUploadSecondBrokenFile() {
        mergePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        mergePdfPage.chooseFile(AltoSiteDocs.BROKEN_PDF.getFilePath());
        mergePdfPage.checkValidationForPages();
        mergePdfPage.closeNotificationError();
    }

    @Story("MergePDF#006")
    @Test
    public void deleteFileAfterUpload() {
        mergePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        mergePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        mergePdfPage.checkEnabledButtonConvertNow();
        mergePdfPage.deleteFileWithUseIconTrash();
        mergePdfPage.checkEnabledButtonChooseFile();
    }

    @Story("MergePDF#007")
    @Test
    public void deleteFileDuringUpload() {
        mergePdfPage.chooseFile(AltoSiteDocs.TEN_MB_PDF.getFilePath());
        mergePdfPage.deleteFileWithUseIconX();
        mergePdfPage.checkEnabledButtonChooseFile();
    }

    @Story("MergePDF#008")
    @Test
    public void checkValidationForUploadFileMore25MB() {
        mergePdfPage.chooseFile(AltoSiteDocs.BIG_PDF.getFilePath());
        mergePdfPage.checkValidationForPages();
        mergePdfPage.closeNotificationError();
    }

    @Story("MergePDF#009")
    @Test
    public void checkValidationForUploadSecondFileMore25MB() {
        mergePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        mergePdfPage.chooseFile(AltoSiteDocs.BIG_PDF.getFilePath());
        mergePdfPage.checkValidationForPages();
        mergePdfPage.closeNotificationError();
    }

    @Story("MergePDF#010")
    @Test
    public void checkValidationForUploadProtectFile() {
        mergePdfPage.chooseFile(AltoSiteDocs.PROTECTED_PDF.getFilePath());
        mergePdfPage.checkValidationForPages();
        mergePdfPage.closeNotificationError();
    }

    @Story("MergePDF#011")
    @Test
    public void checkWorkButtonStartOverAgain() {
        mergePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        mergePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        mergePdfPage.convertNow();
        mergePdfPage.startOverAgain();
        mergePdfPage.checkEnabledButtonChooseFile();
    }

    @Story("MergePDF#012")
    @Test
    public void checkRedirectPdffillerSaveDocumentAs() {
        mergePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        mergePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        mergePdfPage.convertNow();
        mergePdfPage.goToPdffiller(SAVE);
        mergePdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("MergePDF#013")
    @Test
    public void checkRedirectPdffillerEditDocuments() {
        mergePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        mergePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        mergePdfPage.convertNow();
        mergePdfPage.goToPdffiller(EDIT);
        mergePdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("MergePDF#014")
    @Test
    public void checkRedirectPdffillerSign() {
        mergePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        mergePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        mergePdfPage.convertNow();
        mergePdfPage.goToPdffiller(SIGN);
        mergePdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("MergePDF#015")
    @Test
    public void checkRedirectPdffillerShare() {
        mergePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        mergePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        mergePdfPage.convertNow();
        mergePdfPage.goToPdffiller(SHARE);
        mergePdfPage.checkRedirectPdffillerWithPdf();
    }
}