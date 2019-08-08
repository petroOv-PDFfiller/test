package pages.alto_sites;

import org.openqa.selenium.WebDriver;

public class AltoPdfToPngPage extends AltoSitesBasePage {
    public AltoPdfToPngPage(WebDriver driver) {
        super(driver);
        baseUrlProd = "https://altoconvertpdftopng.com/";
        baseUrlDev = "https://altopdftopng.pdffillers.com/";
    }
}
