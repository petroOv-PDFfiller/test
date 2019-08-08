package pages.alto_sites;

import org.openqa.selenium.WebDriver;

public class AltoPdfToPptPage extends AltoSitesBasePage {
    public AltoPdfToPptPage(WebDriver driver) {
        super(driver);
        baseUrlProd = "https://altoconvertpdftoppt.com/";
        baseUrlDev = "https://altopdftoppt.pdffillers.com/";
    }
}