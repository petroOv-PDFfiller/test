package tests.alto_sites;

import base_tests.AltoSitesBaseTest;
import data.alto_site.AltoSiteDocs;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.alto_sites.AltoPngToPdfPage;

import java.util.List;

import static java.util.Arrays.asList;
import static pages.alto_sites.AltoJpgToPdfPage.*;

@Feature("PNG2PDF")
public class PngToPdfTest extends AltoSitesBaseTest {
    List<AltoSiteDocs> docs = asList(
            AltoSiteDocs.BIG_PNG,
            AltoSiteDocs.THREE_MB_PNG,
            AltoSiteDocs.SMALL_PNG,
            AltoSiteDocs.BROKEN_PNG
    );
    private AltoPngToPdfPage pngToPdfPage;

    @BeforeMethod
    public void getPngToPdfPage() {
        pngToPdfPage = new AltoPngToPdfPage(driver);
        pngToPdfPage.navigateToPage();
        pngToPdfPage.isOpened();
    }

    @BeforeTest
    public void altoFileDownload() {
        getPngToPdfPage();
        pngToPdfPage.downloadFileCloud(docs);
        downloadFileSelenoid(docs);
        pngToPdfPage.checkUploadFromSelenoid(docs);
    }

    @Story("PNG2PDF#001")
    @Test
    public void basicFlowConvertJpgFile() {
        pngToPdfPage.chooseFile(AltoSiteDocs.SMALL_PNG.getFilePath());
        pngToPdfPage.convertNow();
        pngToPdfPage.download();
        // pngToPdfPage.checkDownload("Converted.pdf");
    }

    @Story("PNG2PDF#002")
    @Test
    public void flowConvertThreeJpgFile() {
        pngToPdfPage.chooseFile(AltoSiteDocs.SMALL_PNG.getFilePath());
        pngToPdfPage.chooseFile(AltoSiteDocs.THREE_MB_PNG.getFilePath());
        pngToPdfPage.chooseFile(AltoSiteDocs.SMALL_PNG.getFilePath());
        pngToPdfPage.convertNow();
        pngToPdfPage.download();
        //   pngToPdfPage.checkDownload("Converted.pdf");
    }

    @Story("PNG2PDF#003")
    @Test
    public void checkValidationToUploadBrokenJpgFile() {
        pngToPdfPage.chooseFile(AltoSiteDocs.BROKEN_PNG.getFilePath());
        pngToPdfPage.checkValidationForPages();
        pngToPdfPage.closeNotificationError();
    }

    @Story("PNG2PDF#004")
    @Test
    public void deleteFileAfterUpload() {
        pngToPdfPage.chooseFile(AltoSiteDocs.SMALL_PNG.getFilePath());
        pngToPdfPage.checkEnabledButtonConvertNow();
        pngToPdfPage.deleteFileWithUseIconTrash();
        pngToPdfPage.checkEnabledButtonChooseFile();
    }

    @Story("PNG2PDF#005")
    @Test
    public void deleteFileDuringUploadIconTrash() {
        pngToPdfPage.chooseFile(AltoSiteDocs.THREE_MB_PNG.getFilePath());
        pngToPdfPage.deleteFileWithUseIconTrash();
    }

//    @Story("PNG2PDF#006")
//    @Test(priority = 1)
//    public void deleteFileDuringUploadIconX() {
//        pngToPdfPage.chooseFile(threePngFile);
//        pngToPdfPage.deleteFileWithUseIconX();
//    }

    @Story("PNG2PDF#007")
    @Test
    public void cancelConvert() {
        pngToPdfPage.chooseFile(AltoSiteDocs.SMALL_PNG.getFilePath());
        pngToPdfPage.convertNow();
        pngToPdfPage.cancelConvert();
        pngToPdfPage.checkEnabledButtonChooseFile();
    }

    @Story("PNG2PDF#008")
    @Test
    public void checkValidationForUploadFileMore25MB() {
        pngToPdfPage.chooseFile(AltoSiteDocs.BIG_PNG.getFilePath());
        pngToPdfPage.checkValidationForPages();
        pngToPdfPage.closeNotificationError();
    }

    @Story("PNG2PDF#009")
    @Test
    public void checkWorkButtonStartOverAgain() {
        pngToPdfPage.chooseFile(AltoSiteDocs.SMALL_PNG.getFilePath());
        pngToPdfPage.convertNow();
        pngToPdfPage.startOverAgain();
        pngToPdfPage.checkEnabledButtonChooseFile();
    }

    @Story("PNG2PDF#010")
    @Test
    public void checkRedirectPdffillerSaveDocumentAs() {
        pngToPdfPage.chooseFile(AltoSiteDocs.SMALL_PNG.getFilePath());
        pngToPdfPage.convertNow();
        pngToPdfPage.goToPdffiller(SAVE);
        pngToPdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PNG2PDF#011")
    @Test
    public void checkRedirectPdffillerEditDocuments() {
        pngToPdfPage.chooseFile(AltoSiteDocs.THREE_MB_PNG.getFilePath());
        pngToPdfPage.convertNow();
        pngToPdfPage.goToPdffiller(EDIT);
        pngToPdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PNG2PDF#012")
    @Test
    public void checkRedirectPdffillerSign() {
        pngToPdfPage.chooseFile(AltoSiteDocs.SMALL_PNG.getFilePath());
        pngToPdfPage.convertNow();
        pngToPdfPage.goToPdffiller(SIGN);
        pngToPdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PNG2PDF#013")
    @Test
    public void checkRedirectPdffillerShare() {
        pngToPdfPage.chooseFile(AltoSiteDocs.THREE_MB_PNG.getFilePath());
        pngToPdfPage.convertNow();
        pngToPdfPage.goToPdffiller(SHARE);
        pngToPdfPage.checkRedirectPdffillerWithPdf();
    }
}