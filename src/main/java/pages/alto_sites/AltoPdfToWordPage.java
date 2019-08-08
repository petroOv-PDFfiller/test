package pages.alto_sites;

import org.openqa.selenium.WebDriver;

public class AltoPdfToWordPage extends AltoSitesBasePage {
    public AltoPdfToWordPage(WebDriver driver) {
        super(driver);
        baseUrlProd = "https://altoconvertpdftoword.com/";
        baseUrlDev = "https://altopdftoword.pdffillers.com/";
    }
}
