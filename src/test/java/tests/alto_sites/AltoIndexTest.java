package tests.alto_sites;

import base_tests.AltoSitesBaseTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.alto_sites.AltoIndexPage;

@Feature("AltoPDF")
public class AltoIndexTest extends AltoSitesBaseTest {

    private AltoIndexPage indexPage;

    @BeforeMethod
    public void getCompressPage() {
        indexPage = new AltoIndexPage(driver);
        indexPage.navigateToPage();
        indexPage.isOpened();
    }

    @Story("ALTOPDF#001")
    @Test
    public void goToMergePdf() {
        indexPage.navigateToAnyAltoSites("merge-pdf");
        indexPage.checkOpenAltoSites("altomerge");
        indexPage.checkEnabledButtonChooseFile();
    }


    @Story("ALTOPDF#002")
    @Test
    public void goToRotatePdf() {
        indexPage.navigateToAnyAltoSites("rotate-pdf");
        indexPage.checkOpenAltoSites("altorotate");
        indexPage.checkEnabledButtonChooseFile();
    }

    @Story("ALTOPDF#003")
    @Test
    public void goToSplitPdf() {
        indexPage.navigateToAnyAltoSites("split-pdf");
        indexPage.checkOpenAltoSites("altosplit");
        indexPage.checkEnabledButtonChooseFile();
    }

    @Story("ALTOPDF#004")
    @Test
    public void goToCompressPdf() {
        indexPage.navigateToAnyAltoSites("compress-pdf");
        indexPage.checkOpenAltoSites("altocompress");
        indexPage.checkEnabledButtonChooseFile();
    }

    @Story("ALTOPDF#005")
    @Test
    public void goToProtectPdf() {
        indexPage.navigateToAnyAltoSites("protect-pdf");
        indexPage.checkOpenAltoSites("altoprotectpdf");
        indexPage.checkEnabledButtonChooseFile();
    }

    @Story("ALTOPDF#006")
    @Test
    public void goToUnlockPdf() {
        indexPage.navigateToAnyAltoSites("unlock-pdf");
        indexPage.checkOpenAltoSites("altounlock");
        indexPage.checkEnabledButtonChooseFile();
    }

    @Story("ALTOPDF#007")
    @Test
    public void goToExtractPages() {
        indexPage.navigateToAnyAltoSites("extract-page");
        indexPage.checkOpenAltoSites("altoextract");
        indexPage.checkEnabledButtonChooseFile();
    }

    @Story("ALTOPDF#008")
    @Test
    public void goToJpgToPdf() {
        indexPage.navigateToAnyAltoSites("jpg-pdf");
        indexPage.checkOpenAltoSites("altoconvertjpgtopdf");
        indexPage.checkEnabledButtonChooseFile();
    }

    @Story("ALTOPDF#009")
    @Test
    public void goToPdfToJpg() {
        indexPage.navigateToAnyAltoSites("pdf-jpg");
        indexPage.checkOpenAltoSites("altoconvertpdftojpg");
        indexPage.checkEnabledButtonChooseFile();
    }

    @Story("ALTOPDF#010")
    @Test
    public void goToPngToPdf() {
        indexPage.navigateToAnyAltoSites("png-pdf");
        indexPage.checkOpenAltoSites("altoconvertpngtopdf");
        indexPage.checkEnabledButtonChooseFile();
    }

    @Story("ALTOPDF#011")
    @Test
    public void goToPdfToPng() {
        indexPage.navigateToAnyAltoSites("pdf-png");
        indexPage.checkOpenAltoSites("altoconvertpdftopng");
        indexPage.checkEnabledButtonChooseFile();
    }

    @Story("ALTOPDF#012")
    @Test
    public void goToWordToPdf() {
        indexPage.navigateToAnyAltoSites("word-pdf");
        indexPage.checkOpenAltoSites("altoconvertwordtopdf");
        indexPage.checkEnabledButtonChooseFile();
    }

    @Story("ALTOPDF#013")
    @Test
    public void goToPdfToWord() {
        indexPage.navigateToAnyAltoSites("pdf-word");
        indexPage.checkOpenAltoSites("altoconvertpdftoword");
        indexPage.checkEnabledButtonChooseFile();
    }

    @Story("ALTOPDF#014")
    @Test
    public void goToPdfToExcel() {
        indexPage.navigateToAnyAltoSites("pdf-excel");
        indexPage.checkOpenAltoSites("altoconvertpdftoexcel");
        indexPage.checkEnabledButtonChooseFile();
    }

    @Story("ALTOPDF#015")
    @Test
    public void goToPptToPdf() {
        indexPage.navigateToAnyAltoSites("ppt-pdf");
        indexPage.checkOpenAltoSites("altoconvertppttopdf");
        indexPage.checkEnabledButtonChooseFile();
    }

    @Story("ALTOPDF#016")
    @Test
    public void goToPdfToPpt() {
        indexPage.navigateToAnyAltoSites("pdf-ppt");
        indexPage.checkOpenAltoSites("altoconvertpdftoppt");
        indexPage.checkEnabledButtonChooseFile();
    }
}
