package pages.alto_sites;

import org.openqa.selenium.WebDriver;

public class AltoMergePage extends AltoSitesBasePage {
    public AltoMergePage(WebDriver driver) {
        super(driver);
        baseUrlProd = "https://altomerge.com/";
        baseUrlDev = "https://altomerge.pdffillers.com/";
    }
}
