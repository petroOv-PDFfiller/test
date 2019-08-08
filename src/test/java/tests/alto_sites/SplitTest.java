package tests.alto_sites;

import base_tests.AltoSitesBaseTest;
import data.alto_site.AltoSiteDocs;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.alto_sites.AltoSplitPage;

import java.util.List;

import static java.util.Arrays.asList;
import static pages.alto_sites.AltoSitesBasePage.*;

@Feature("SplitPDF")
public class SplitTest extends AltoSitesBaseTest {

    List<AltoSiteDocs> docs = asList(
            AltoSiteDocs.BIG_PDF,
            AltoSiteDocs.TWO_MB_PDF,
            AltoSiteDocs.MANY_PAGE_PDF,
            AltoSiteDocs.BROKEN_PDF,
            AltoSiteDocs.PROTECTED_PDF,
            AltoSiteDocs.TEN_MB_PDF,
            AltoSiteDocs.SMALL_PDF
    );
    private AltoSplitPage splitPage;

    @BeforeMethod
    public void getSplitPage() {
        splitPage = new AltoSplitPage(driver);
        splitPage.navigateToPage();
        splitPage.isOpened();
    }

    @BeforeTest
    public void altoFileDownload() {
        getSplitPage();
        splitPage.downloadFileCloud(docs);
        downloadFileSelenoid(docs);
        splitPage.checkUploadFromSelenoid(docs);
    }

    @Story("SplitPDF#001")
    @Test
    public void basicFlowConvert() {
        splitPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        splitPage.typePageRange("1-3;6,8-10;5,17-18,22");
        splitPage.convertNow();
        splitPage.download();
        //    splitPage.checkDownload("Splitted.zip");
    }

    @Story("SplitPDF#002")
    @Test
    public void checkValidationMany150Page() {
        splitPage.chooseFile(AltoSiteDocs.MANY_PAGE_PDF.getFilePath());
        splitPage.checkValidationForPages();
        splitPage.closeNotificationError();
    }

    @Story("SplitPDF#003")
    @Test
    public void checkValidationToUploadBrokenFile() {
        splitPage.chooseFile(AltoSiteDocs.BROKEN_PDF.getFilePath());
        splitPage.checkValidationForPages();
        splitPage.closeNotificationError();
    }

    @Story("SplitPDF#004")
    @Test
    public void cancelConvert() {
        splitPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        splitPage.typePageRange("1-3;10-11");
        splitPage.convertNow();
        splitPage.cancelConvert();
        splitPage.checkEnabledButtonChooseFile();
    }

    @Story("SplitPDF#005")
    @Test
    public void checkValidationForUploadFileMore25MB() {
        splitPage.chooseFile(AltoSiteDocs.BIG_PDF.getFilePath());
        splitPage.checkValidationForPages();
        splitPage.closeNotificationError();
    }

    @Story("SplitPDF#006")
    @Test
    public void checkValidationForUploadProtectFile() {
        splitPage.chooseFile(AltoSiteDocs.PROTECTED_PDF.getFilePath());
        splitPage.checkValidationForPages();
        splitPage.closeNotificationError();
    }

    @Story("SplitPDF#007")
    @Test
    public void checkWorkButtonStartOverAgain() {
        splitPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        splitPage.typePageRange("1-2");
        splitPage.convertNow();
        splitPage.startOverAgain();
        splitPage.checkEnabledButtonChooseFile();
    }

    @Story("SplitPDF#008")
    @Test
    public void checkRedirectPdffillerSaveDocumentAs() {
        splitPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        splitPage.typePageRange("1");
        splitPage.convertNow();
        splitPage.goToPdffiller(SAVE);
        splitPage.checkRedirectPdffillerWithPdf();
    }

    @Story("SplitPDF#009")
    @Test
    public void checkRedirectPdffillerEditDocuments() {
        splitPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        splitPage.typePageRange("1-2");
        splitPage.convertNow();
        splitPage.goToPdffiller(EDIT);
        splitPage.checkRedirectPdffillerWithPdf();
    }

    @Story("SplitPDF#010")
    @Test
    public void checkRedirectPdffillerSign() {
        splitPage.chooseFile(AltoSiteDocs.SMALL_PDF.getFilePath());
        splitPage.typePageRange("1");
        splitPage.convertNow();
        splitPage.goToPdffiller(SIGN);
        splitPage.checkRedirectPdffillerWithPdf();
    }

    @Story("SplitPDF#011")
    @Test
    public void checkRedirectPdffillerShare() {
        splitPage.chooseFile(AltoSiteDocs.TWO_MB_PDF.getFilePath());
        splitPage.typePageRange("1-2");
        splitPage.convertNow();
        splitPage.goToPdffiller(SHARE);
        splitPage.checkRedirectPdffillerWithPdf();
    }
}