package tests.alto_sites;

import base_tests.AltoSitesBaseTest;
import data.alto_site.AltoSiteDocs;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.alto_sites.AltoRotatePage;

import java.util.List;

import static java.util.Arrays.asList;
import static pages.alto_sites.AltoSitesBasePage.*;

@Feature("ROTATEPDF")
public class RotateTests extends AltoSitesBaseTest {

    List<AltoSiteDocs> docs = asList(
            AltoSiteDocs.BIG_PDF,
            AltoSiteDocs.TWO_MB_PDF,
            AltoSiteDocs.MANY_PAGE_PDF,
            AltoSiteDocs.BROKEN_PDF,
            AltoSiteDocs.PROTECTED_PDF,
            AltoSiteDocs.TEN_MB_PDF,
            AltoSiteDocs.SMALL_PDF
    );
    private AltoRotatePage rotatePdfPage;

    @BeforeMethod
    public void getRotatePage() {
        rotatePdfPage = new AltoRotatePage(driver);
        rotatePdfPage.navigateToPage();
        rotatePdfPage.isOpened();
    }

    @BeforeTest
    public void altoFileDownload() {
        getRotatePage();
        rotatePdfPage.downloadFileCloud(docs);
        downloadFileSelenoid(docs);
        rotatePdfPage.checkUploadFromSelenoid(docs);
    }

    @Story("ROTATEPDF#001")
    @Test
    public void basicFlowConvertWithAllPage() {
        rotatePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        rotatePdfPage.rotateLeftAllPages();
        rotatePdfPage.checkEnabledButtonApplyChanges();
        rotatePdfPage.rotateRightAllPages();
        rotatePdfPage.checkDisabledButtonApplyChanges();
        rotatePdfPage.rotateLeftAllPages();
        rotatePdfPage.applyChanges();
        rotatePdfPage.download();
        //    rotatePdfPage.checkDownload("Rotated.pdf");
    }

    @Story("ROTATEPDF#002")
    @Test
    public void flowConvertWithSelectPageForRotate() {
        rotatePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        rotatePdfPage.iconRotateRight();
        rotatePdfPage.checkEnabledButtonApplyChanges();
        rotatePdfPage.iconRotateLeft();
        rotatePdfPage.checkDisabledButtonApplyChanges();
    }

    @Story("ROTATEPDF#003")
    @Test
    public void checkValidation150Page() {
        rotatePdfPage.chooseFile(AltoSiteDocs.MANY_PAGE_PDF.getFilePath());
        rotatePdfPage.checkValidationForPages();
        rotatePdfPage.closeNotificationError();
    }

    @Story("ROTATEPDF#004")
    @Test
    public void checkValidationToUploadBrokenFile() {
        rotatePdfPage.chooseFile(AltoSiteDocs.BROKEN_PDF.getFilePath());
        rotatePdfPage.checkValidationForPages();
        rotatePdfPage.closeNotificationError();
    }

    @Story("ROTATEPDF#005")
    @Test
    public void cancelConvert() {
        rotatePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        rotatePdfPage.rotateLeftAllPages();
        rotatePdfPage.applyChanges();
        rotatePdfPage.cancelConvert();
        rotatePdfPage.checkEnabledButtonChooseFile();
    }

    @Story("ROTATEPDF#006")
    @Test
    public void checkValidationForUploadFileMore25MB() {
        rotatePdfPage.chooseFile(AltoSiteDocs.BIG_PDF.getFilePath());
        rotatePdfPage.checkValidationForPages();
        rotatePdfPage.closeNotificationError();
    }

    @Story("ROTATEPDF#007")
    @Test
    public void checkValidationForUploadProtectFile() {
        rotatePdfPage.chooseFile(AltoSiteDocs.BROKEN_PDF.getFilePath());
        rotatePdfPage.checkValidationForPages();
        rotatePdfPage.closeNotificationError();
    }

    @Story("ROTATEPDF#008")
    @Test
    public void checkWorkButtonStartOverAgain() {
        rotatePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        rotatePdfPage.rotateRightAllPages();
        rotatePdfPage.applyChanges();
        rotatePdfPage.startOverAgain();
    }

    @Story("ROTATEPDF#009")
    @Test
    public void checkRedirectPdffillerSaveDocumentAs() {
        rotatePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        rotatePdfPage.rotateRightAllPages();
        rotatePdfPage.applyChanges();
        rotatePdfPage.goToPdffiller(SAVE);
        rotatePdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("ROTATEPDF#010")
    @Test
    public void checkRedirectPdffillerEditDocuments() {
        rotatePdfPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        rotatePdfPage.rotateRightAllPages();
        rotatePdfPage.applyChanges();
        rotatePdfPage.goToPdffiller(EDIT);
        rotatePdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("ROTATEPDF#011")
    @Test
    public void checkRedirectPdffillerSign() {
        rotatePdfPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        rotatePdfPage.rotateRightAllPages();
        rotatePdfPage.applyChanges();
        rotatePdfPage.goToPdffiller(SIGN);
        rotatePdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("ROTATEPDF#012")
    @Test
    public void checkRedirectPdffillerShare() {
        rotatePdfPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        rotatePdfPage.rotateRightAllPages();
        rotatePdfPage.applyChanges();
        rotatePdfPage.goToPdffiller(SHARE);
        rotatePdfPage.checkRedirectPdffillerWithPdf();
    }
}
