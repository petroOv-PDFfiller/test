package tests.alto_sites;

import base_tests.AltoSitesBaseTest;
import data.alto_site.AltoSiteDocs;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.alto_sites.AltoUnlockPage;

import java.util.List;

import static java.util.Arrays.asList;
import static pages.alto_sites.AltoSitesBasePage.*;

@Feature("UnlockPDF")
public class UnlockTest extends AltoSitesBaseTest {
    List<AltoSiteDocs> docs = asList(
            AltoSiteDocs.BIG_PDF,
            AltoSiteDocs.TWO_MB_PDF,
            AltoSiteDocs.MANY_PAGE_PDF,
            AltoSiteDocs.BROKEN_PDF,
            AltoSiteDocs.PROTECTED_PDF,
            AltoSiteDocs.TEN_MB_PDF,
            AltoSiteDocs.SMALL_PDF
    );
    private AltoUnlockPage unlockPage;

    @BeforeMethod
    public void getUnlockPage() {
        unlockPage = new AltoUnlockPage(driver);
        unlockPage.navigateToPage();
        unlockPage.isOpened();
    }

    @BeforeTest
    public void altoFileDownload() {
        getUnlockPage();
        unlockPage.downloadFileCloud(docs);
        downloadFileSelenoid(docs);
        unlockPage.checkUploadFromSelenoid(docs);
    }

    @Story("UnlockPDF#001")
    @Test
    public void basicFlowUnlockPdf() {
        unlockPage.chooseFile(AltoSiteDocs.PROTECTED_PDF.getFilePath());
        unlockPage.typePassPdf("test123R");
        unlockPage.convertNow();
        unlockPage.download();
        //  unlockPage.checkDownload("test123R.pdf");
    }

    @Story("UnlockPDF#002")
    @Test
    public void checkValidationFieldPassword() {
        unlockPage.chooseFile(AltoSiteDocs.PROTECTED_PDF.getFilePath());
        unlockPage.typePassPdf("blabla");
        unlockPage.convertNow();
        unlockPage.checkValidationField();
    }

    @Story("UnlockPDF#003")
    @Test
    public void checkValidation150Page() {
        unlockPage.chooseFile(AltoSiteDocs.MANY_PAGE_PDF.getFilePath());
        unlockPage.typePassPdf("badpass");
        unlockPage.convertNow();
        unlockPage.checkValidationForPages();
        unlockPage.closeNotificationError();
    }

    @Story("UnlockPDF#004")
    @Test
    public void checkValidationToUploadBrokenFile() {
        unlockPage.chooseFile(AltoSiteDocs.BROKEN_PDF.getFilePath());
        unlockPage.typePassPdf("badpass");
        unlockPage.convertNow();
        unlockPage.checkValidationForPages();
        unlockPage.closeNotificationError();
    }

    @Story("UnlockPDF#005")
    @Test
    public void checkWorkStartOverAgainAfterUploadFile() {
        unlockPage.chooseFile(AltoSiteDocs.PROTECTED_PDF.getFilePath());
        unlockPage.startOverAgain();
    }

    @Story("UnlockPDF#006")
    @Test
    public void checkValidationForUploadFileMore25MB() {
        unlockPage.chooseFile(AltoSiteDocs.BIG_PDF.getFilePath());
        unlockPage.checkValidationForPages();
        unlockPage.closeNotificationError();
    }

    @Story("UnlockPDF#007")
    @Test
    public void checkWorkButtonStartOverAgain() {
        unlockPage.chooseFile(AltoSiteDocs.PROTECTED_PDF.getFilePath());
        unlockPage.typePassPdf("test123R");
        unlockPage.convertNow();
        unlockPage.download();
        unlockPage.startOverAgain();
        unlockPage.checkEnabledButtonChooseFile();
    }

    @Story("UnlockPDF#008")
    public void checkRedirectPdffillerSaveDocumentAs() {
        unlockPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        unlockPage.typePassPdf("test123R");
        unlockPage.convertNow();
        unlockPage.goToPdffiller(SAVE);
        unlockPage.checkRedirectPdffillerWithPdf();
    }

    @Story("UnlockPDF#009")
    @Test
    public void checkRedirectPdffillerEditDocuments() {
        unlockPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        unlockPage.typePassPdf("test123R");
        unlockPage.convertNow();
        unlockPage.goToPdffiller(EDIT);
        unlockPage.checkRedirectPdffillerWithPdf();
    }

    @Story("UnlockPDF#010")
    @Test
    public void checkRedirectPdffillerSign() {
        unlockPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        unlockPage.typePassPdf("test123R");
        unlockPage.convertNow();
        unlockPage.goToPdffiller(SIGN);
        unlockPage.checkRedirectPdffillerWithPdf();
    }

    @Story("UnlockPDF#011")
    @Test
    public void checkRedirectPdffillerwShare() {
        unlockPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        unlockPage.typePassPdf("test123R");
        unlockPage.convertNow();
        unlockPage.goToPdffiller(SHARE);
        unlockPage.checkRedirectPdffillerWithPdf();
    }
}
