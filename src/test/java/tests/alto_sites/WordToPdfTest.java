package tests.alto_sites;

import base_tests.AltoSitesBaseTest;
import data.alto_site.AltoSiteDocs;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.alto_sites.AltoWordToPdfPage;

import java.util.List;

import static java.util.Arrays.asList;
import static pages.alto_sites.AltoJpgToPdfPage.*;


@Feature("Word2PDF")
public class WordToPdfTest extends AltoSitesBaseTest {
    List<AltoSiteDocs> docs = asList(
            AltoSiteDocs.BIG_DOC,
            AltoSiteDocs.BIG_DOCX,
            AltoSiteDocs.MEDIUM_DOC,
            AltoSiteDocs.BROKEN_DOC,
            AltoSiteDocs.BROKEN_DOCX,
            AltoSiteDocs.MANY_PAGE_DOC,
            AltoSiteDocs.MANY_PAGE_DOCX,
            AltoSiteDocs.SMALL_DOC,
            AltoSiteDocs.SMALL_DOCX
    );
    private AltoWordToPdfPage wordToPdfPage;

    @BeforeMethod
    public void getWordToPdfPage() {
        wordToPdfPage = new AltoWordToPdfPage(driver);
        wordToPdfPage.navigateToPage();
        wordToPdfPage.isOpened();
    }

    @BeforeTest
    public void altoFileDownload() {
        getWordToPdfPage();
        wordToPdfPage.downloadFileCloud(docs);
        downloadFileSelenoid(docs);
        wordToPdfPage.checkUploadFromSelenoid(docs);
    }

    @Story("Word2PDF#001")
    @Test
    public void basicFlowConvertDocFile() {
        wordToPdfPage.chooseFile(AltoSiteDocs.SMALL_DOC.getFilePath());
        wordToPdfPage.convertNow();
        wordToPdfPage.download();
        // wordToPdfPage.checkDownload("smalldocfile.pdf");
    }

    @Story("Word2PDF#002")
    @Test
    public void basicFlowConvertDocxFile() {
        wordToPdfPage.chooseFile(AltoSiteDocs.SMALL_DOCX.getFilePath());
        wordToPdfPage.convertNow();
        wordToPdfPage.download();
        //   wordToPdfPage.checkDownload("smalldocxfile.pdf");
    }

    @Story("Word2PDF#003")
    @Test
    public void checkValidationMany150PageDocFile() {
        wordToPdfPage.chooseFile(AltoSiteDocs.MANY_PAGE_DOC.getFilePath());
        wordToPdfPage.checkValidationForPages();
        wordToPdfPage.closeNotificationError();
    }

    @Story("Word2PDF#004")
    @Test
    public void checkValidationMany150PageDocxFile() {
        wordToPdfPage.chooseFile(AltoSiteDocs.MANY_PAGE_DOCX.getFilePath());
        wordToPdfPage.checkValidationForPages();
        wordToPdfPage.closeNotificationError();
    }

    @Story("Word2PDF#005")
    @Test
    public void checkValidationToUploadBrokenDocFile() {
        wordToPdfPage.chooseFile(AltoSiteDocs.BROKEN_DOC.getFilePath());
        wordToPdfPage.checkValidationForPages();
        wordToPdfPage.closeNotificationError();
    }

    @Story("Word2PDF#006")
    @Test
    public void checkValidationToUploadBrokenDocxFile() {
        wordToPdfPage.chooseFile(AltoSiteDocs.BROKEN_DOCX.getFilePath());
        wordToPdfPage.checkValidationForPages();
        wordToPdfPage.closeNotificationError();
    }

    @Story("Word2PDF#007")
    @Test
    public void deleteFileAfterUpload() {
        wordToPdfPage.chooseFile(AltoSiteDocs.SMALL_DOCX.getFilePath());
        wordToPdfPage.checkEnabledButtonConvertNow();
        wordToPdfPage.deleteFileWithUseIconTrash();
        wordToPdfPage.checkEnabledButtonChooseFile();
    }

    @Story("Word2PDF#008")
    @Test
    public void deleteFileDuringUploadIconTrash() {
        wordToPdfPage.chooseFile(AltoSiteDocs.MEDIUM_DOC.getFilePath());
        wordToPdfPage.deleteFileWithUseIconTrash();
    }

    @Story("Word2PDF#009")
    @Test
    public void deleteFileDuringUploadIconX() {
        wordToPdfPage.chooseFile(AltoSiteDocs.MEDIUM_DOC.getFilePath());
        wordToPdfPage.deleteFileWithUseIconX();
    }

    @Story("Word2PDF#010")
    @Test
    public void cancelConvert() {
        wordToPdfPage.chooseFile(AltoSiteDocs.SMALL_DOCX.getFilePath());
        wordToPdfPage.convertNow();
        wordToPdfPage.cancelConvert();
        wordToPdfPage.checkEnabledButtonChooseFile();
    }

    @Story("Word2PDF#011")
    @Test
    public void checkValidationForUploadFileMore25MB() {
        wordToPdfPage.chooseFile(AltoSiteDocs.BIG_DOC.getFilePath());
        wordToPdfPage.checkValidationForPages();
        wordToPdfPage.closeNotificationError();
        wordToPdfPage.chooseFile(AltoSiteDocs.BIG_DOCX.getFilePath());
        wordToPdfPage.checkValidationForPages();
        wordToPdfPage.closeNotificationError();
    }

    @Story("Word2PDF#012")
    @Test
    public void checkWorkButtonStartOverAgain() {
        wordToPdfPage.chooseFile(AltoSiteDocs.SMALL_DOC.getFilePath());
        wordToPdfPage.convertNow();
        wordToPdfPage.startOverAgain();
        wordToPdfPage.checkEnabledButtonChooseFile();
    }

    @Story("Word2PDF#013")
    @Test
    public void checkRedirectPdffillerSaveDocumentAs() {
        wordToPdfPage.chooseFile(AltoSiteDocs.SMALL_DOCX.getFilePath());
        wordToPdfPage.convertNow();
        wordToPdfPage.goToPdffiller(SAVE);
        wordToPdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("Word2PDF#014")
    @Test
    public void checkRedirectPdffillerEditDocuments() {
        wordToPdfPage.chooseFile(AltoSiteDocs.SMALL_DOC.getFilePath());
        wordToPdfPage.convertNow();
        wordToPdfPage.goToPdffiller(EDIT);
        wordToPdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("Word2PDF#015")
    @Test
    public void checkRedirectPdffillerSign() {
        wordToPdfPage.chooseFile(AltoSiteDocs.SMALL_DOCX.getFilePath());
        wordToPdfPage.convertNow();
        wordToPdfPage.goToPdffiller(SIGN);
        wordToPdfPage.checkRedirectPdffillerWithPdf();
    }

    @Story("Word2PDF#016")
    @Test
    public void checkRedirectPdffillerShare() {
        wordToPdfPage.chooseFile(AltoSiteDocs.SMALL_DOC.getFilePath());
        wordToPdfPage.convertNow();
        wordToPdfPage.goToPdffiller(SHARE);
        wordToPdfPage.checkRedirectPdffillerWithPdf();
    }
}
