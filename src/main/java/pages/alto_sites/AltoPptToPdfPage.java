package pages.alto_sites;

import org.openqa.selenium.WebDriver;

public class AltoPptToPdfPage extends AltoSitesBasePage {

    public AltoPptToPdfPage(WebDriver driver) {
        super(driver);
        baseUrlProd = "https://altoconvertppttopdf.com/";
        baseUrlDev = "https://altoppttopdf.pdffillers.com/";
    }
}
