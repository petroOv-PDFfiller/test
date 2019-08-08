package tests.alto_sites;

import base_tests.AltoSitesBaseTest;
import data.alto_site.AltoSiteDocs;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.alto_sites.AltoJpgToPdfPage;

import java.util.List;

import static java.util.Arrays.asList;
import static pages.alto_sites.AltoJpgToPdfPage.*;

@Feature("JPG2PDF")
public class JpgToPdfTest extends AltoSitesBaseTest {
    List<AltoSiteDocs> docs = asList(
            AltoSiteDocs.BIG_JPG,
            AltoSiteDocs.TWO_MB_JPG,
            AltoSiteDocs.BROKEN_JPG,
            AltoSiteDocs.SMALL_JPG
    );
    private AltoJpgToPdfPage jpgToPdfPage;

    @BeforeMethod
    public void getJpgToPdfPage() {
        jpgToPdfPage = new AltoJpgToPdfPage(driver);
        jpgToPdfPage.navigateToPage();
        jpgToPdfPage.isOpened();
    }

    @BeforeTest
    public void altoFileDownload() {
        getJpgToPdfPage();
        jpgToPdfPage.downloadFileCloud(docs);
        downloadFileSelenoid(docs);
        jpgToPdfPage.checkUploadFromSelenoid(docs);
    }

    @Story("JPG2PDF#001")
    @Test
    public void basicFlowConvertJpgFile() {
        jpgToPdfPage.chooseFile(AltoSiteDocs.SMALL_JPG.getName());
        jpgToPdfPage.convertNow();
        jpgToPdfPage.download();
        // jpgToPdfPage.checkDownload("Converted.pdf");
    }

    @Story("JPG2PDF#002")
    @Test
    public void flowConvertThreeJpgFile() {
        jpgToPdfPage.chooseFile(AltoSiteDocs.SMALL_JPG.getName());
        jpgToPdfPage.chooseFile(AltoSiteDocs.TWO_MB_JPG.getName());
        jpgToPdfPage.chooseFile(AltoSiteDocs.SMALL_JPG.getName());
        jpgToPdfPage.convertNow();
        jpgToPdfPage.download();
        //    jpgToPdfPage.checkDownload("Converted.pdf");
    }

    @Story("JPG2PDF#003")
    @Test
    public void checkValidationToUploadBrokenJpgFile() {
        jpgToPdfPage.chooseFile(AltoSiteDocs.BROKEN_JPG.getName());
        jpgToPdfPage.checkValidationForPages();
        jpgToPdfPage.closeNotificationError();
    }

    @Story("JPG2PDF#004")
    @Test
    public void deleteFileAfterUpload() {
        jpgToPdfPage.chooseFile(AltoSiteDocs.SMALL_JPG.getName());
        jpgToPdfPage.checkEnabledButtonConvertNow();
        jpgToPdfPage.deleteFileWithUseIconTrash();
        jpgToPdfPage.checkEnabledButtonChooseFile();
    }

    @Story("JPG2PDF#005")
    @Test
    public void deleteFileDuringUploadIconTrash() {
        jpgToPdfPage.chooseFile(AltoSiteDocs.TWO_MB_JPG.getName());
        jpgToPdfPage.deleteFileWithUseIconTrash();
        jpgToPdfPage.checkEnabledButtonChooseFile();

    }

//    @Story("JPG2PDF#006")
//    @Test(priority = 1)
//    public void deleteFileDuringUploadIconX() {
//        jpgToPdfPage.chooseFile(twoJpgFile);
//        jpgToPdfPage.deleteFileWithUseIconX();
//        jpgToPdfPage.checkEnabledButtonChooseFile();
//    }

    @Story("JPG2PDF#007")
    @Test
    public void cancelConvert() {
        jpgToPdfPage.chooseFile(AltoSiteDocs.SMALL_JPG.getName());
        jpgToPdfPage.convertNow();
        jpgToPdfPage.cancelConvert();
        jpgToPdfPage.checkEnabledButtonChooseFile();
    }

    @Story("JPG2PDF#008")
    @Test
    public void checkValidationForUploadFileMore25MB() {
        jpgToPdfPage.chooseFile(AltoSiteDocs.BIG_JPG.getName());
        jpgToPdfPage.checkValidationForPages();
        jpgToPdfPage.closeNotificationError();
    }

    @Story("JPG2PDF#009")
    @Test
    public void checkWorkButtonStartOverAgain() {
        jpgToPdfPage.chooseFile(AltoSiteDocs.SMALL_JPG.getName());
        jpgToPdfPage.convertNow();
        jpgToPdfPage.startOverAgain();
        jpgToPdfPage.checkEnabledButtonChooseFile();
    }

    @Story("JPG2PDF#010")
    @Test
    public void checkRedirectPdffillerSaveDocumentAs() {
        jpgToPdfPage.chooseFile(AltoSiteDocs.SMALL_JPG.getName());
        jpgToPdfPage.convertNow();
        jpgToPdfPage.goToPdffiller(SAVE);
        jpgToPdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("JPG2PDF#011")
    @Test
    public void checkRedirectPdffillerEditDocuments() {
        jpgToPdfPage.chooseFile(AltoSiteDocs.TWO_MB_JPG.getName());
        jpgToPdfPage.convertNow();
        jpgToPdfPage.goToPdffiller(EDIT);
        jpgToPdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("JPG2PDF#012")
    @Test
    public void checkRedirectPdffillerSign() {
        jpgToPdfPage.chooseFile(AltoSiteDocs.SMALL_JPG.getName());
        jpgToPdfPage.convertNow();
        jpgToPdfPage.goToPdffiller(SIGN);
        jpgToPdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("JPG2PDF#013")
    @Test
    public void checkRedirectPdffillerShare() {
        jpgToPdfPage.chooseFile(AltoSiteDocs.TWO_MB_JPG.getName());
        jpgToPdfPage.convertNow();
        jpgToPdfPage.goToPdffiller(SHARE);
        jpgToPdfPage.checkRedirectPdffillerWithPdf();
    }
}