package tests.alto_sites;

import base_tests.AltoSitesBaseTest;
import data.alto_site.AltoSiteDocs;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.alto_sites.AltoPptToPdfPage;

import java.util.List;

import static java.util.Arrays.asList;
import static pages.alto_sites.AltoPptToPdfPage.*;


@Feature("PPT2PDF")
public class PptToPdfTest extends AltoSitesBaseTest {
    List<AltoSiteDocs> docs = asList(
            AltoSiteDocs.BIG_PPT,
            AltoSiteDocs.BIG_PPTX,
            AltoSiteDocs.MEDIUM_PPTX,
            AltoSiteDocs.MANY_PAGE_PPT,
            AltoSiteDocs.MANY_PAGE_PPTX,
            AltoSiteDocs.BROKEN_PPT,
            AltoSiteDocs.BROKEN_PPTX,
            AltoSiteDocs.SMALL_PPT,
            AltoSiteDocs.SMALL_PPTX
    );
    private AltoPptToPdfPage pptToPdfPage;

    @BeforeMethod
    public void getPptToPdfPage() {
        pptToPdfPage = new AltoPptToPdfPage(driver);
        pptToPdfPage.navigateToPage();
        pptToPdfPage.isOpened();
    }

    @BeforeTest
    public void altoFileDownload() {
        getPptToPdfPage();
        pptToPdfPage.downloadFileCloud(docs);
        downloadFileSelenoid(docs);
        pptToPdfPage.checkUploadFromSelenoid(docs);
    }

    @Story("PPT2PDF#001")
    @Test
    public void basicFlowConvertPptFile() {
        pptToPdfPage.chooseFile(AltoSiteDocs.SMALL_PPT.getFilePath());
        pptToPdfPage.convertNow();
        pptToPdfPage.download();
        //  pptToPdfPage.checkDownload("smallpptfile.pdf");
    }

    @Story("PPT2PDF#002")
    @Test
    public void basicFlowConvertPptxFile() {
        pptToPdfPage.chooseFile(AltoSiteDocs.SMALL_PPTX.getFilePath());
        pptToPdfPage.convertNow();
        pptToPdfPage.download();
        //   pptToPdfPage.checkDownload("smallpptxfile.pdf");
    }

    @Story("PPT2PDF#003")
    @Test
    public void checkValidationMany150PagePptFile() {
        pptToPdfPage.chooseFile(AltoSiteDocs.MANY_PAGE_PPT.getFilePath());
        pptToPdfPage.checkValidationForPages();
        pptToPdfPage.closeNotificationError();
    }

    @Story("PPT2PDF#004")
    @Test
    public void checkValidationMany150PagePptxFile() {
        pptToPdfPage.chooseFile(AltoSiteDocs.SMALL_PPTX.getFilePath());
        pptToPdfPage.checkValidationForPages();
        pptToPdfPage.closeNotificationError();
    }

    @Story("PPT2PDF#005")
    @Test
    public void checkValidationToUploadBrokenPptFile() {
        pptToPdfPage.chooseFile(AltoSiteDocs.BROKEN_PPT.getFilePath());
        pptToPdfPage.checkValidationForPages();
        pptToPdfPage.closeNotificationError();
    }

    @Story("PPT2PDF#006")
    @Test
    public void checkValidationToUploadBrokenPptxFile() {
        pptToPdfPage.chooseFile(AltoSiteDocs.BROKEN_PPTX.getFilePath());
        pptToPdfPage.checkValidationForPages();
        pptToPdfPage.closeNotificationError();
    }

    @Story("PPT2PDF#007")
    @Test
    public void deleteFileAfterUpload() {
        pptToPdfPage.chooseFile(AltoSiteDocs.SMALL_PPT.getFilePath());
        pptToPdfPage.checkEnabledButtonConvertNow();
        pptToPdfPage.deleteFileWithUseIconTrash();
        pptToPdfPage.checkEnabledButtonChooseFile();
    }

    @Story("PPT2PDF#008")
    @Test
    public void deleteFileDuringUploadIconTrash() {
        pptToPdfPage.chooseFile(AltoSiteDocs.MEDIUM_PPTX.getFilePath());
        pptToPdfPage.deleteFileWithUseIconTrash();
    }

    @Story("PPT2PDF#009")
    @Test(priority = 1)
    public void deleteFileDuringUploadIconX() {
        pptToPdfPage.chooseFile(AltoSiteDocs.MEDIUM_PPTX.getFilePath());
        pptToPdfPage.deleteFileWithUseIconX();
    }

    @Story("PPT2PDF#010")
    @Test
    public void cancelConvert() {
        pptToPdfPage.chooseFile(AltoSiteDocs.SMALL_PPTX.getFilePath());
        pptToPdfPage.convertNow();
        pptToPdfPage.cancelConvert();
        pptToPdfPage.checkEnabledButtonChooseFile();
    }

    @Story("PPT2PDF#011")
    @Test
    public void checkValidationForUploadFileMore25MB() {
        pptToPdfPage.chooseFile(AltoSiteDocs.BIG_PPT.getFilePath());
        pptToPdfPage.checkValidationForPages();
        pptToPdfPage.closeNotificationError();
        pptToPdfPage.chooseFile(AltoSiteDocs.BIG_PPTX.getFilePath());
        pptToPdfPage.checkValidationForPages();
        pptToPdfPage.closeNotificationError();
    }

    @Story("PPT2PDF#012")
    @Test
    public void checkWorkButtonStartOverAgain() {
        pptToPdfPage.chooseFile(AltoSiteDocs.SMALL_PPTX.getFilePath());
        pptToPdfPage.convertNow();
        pptToPdfPage.startOverAgain();
        pptToPdfPage.checkEnabledButtonChooseFile();
    }

    @Story("PPT2PDF#013")
    @Test
    public void checkRedirectPdffillerSaveDocumentAs() {
        pptToPdfPage.chooseFile(AltoSiteDocs.SMALL_PPT.getFilePath());
        pptToPdfPage.convertNow();
        pptToPdfPage.goToPdffiller(SAVE);
        pptToPdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PPT2PDF#014")
    @Test
    public void checkRedirectPdffillerEditDocuments() {
        pptToPdfPage.chooseFile(AltoSiteDocs.SMALL_PPTX.getFilePath());
        pptToPdfPage.convertNow();
        pptToPdfPage.goToPdffiller(EDIT);
        pptToPdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PPT2PDF#015")
    @Test
    public void checkRedirectPdffillerSign() {
        pptToPdfPage.chooseFile(AltoSiteDocs.SMALL_PPT.getFilePath());
        pptToPdfPage.convertNow();
        pptToPdfPage.goToPdffiller(SIGN);
        pptToPdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("PPT2PDF#016")
    @Test
    public void checkRedirectPdffillerShare() {
        pptToPdfPage.chooseFile(AltoSiteDocs.SMALL_PPTX.getFilePath());
        pptToPdfPage.convertNow();
        pptToPdfPage.goToPdffiller(SHARE);
        pptToPdfPage.checkRedirectPdffillerWithPdf();
    }
}
