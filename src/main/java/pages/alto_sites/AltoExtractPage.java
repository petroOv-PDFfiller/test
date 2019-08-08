package pages.alto_sites;

import org.openqa.selenium.WebDriver;

public class AltoExtractPage extends AltoSitesBasePage {
    public AltoExtractPage(WebDriver driver) {
        super(driver);
        baseUrlProd = "https://altoextractpdf.com/";
        baseUrlDev = "https://altoextract.pdffillers.com/";
    }
}