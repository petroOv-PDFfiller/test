package pages.alto_sites;

import org.openqa.selenium.WebDriver;

public class AltoWordToPdfPage extends AltoSitesBasePage {
    public AltoWordToPdfPage(WebDriver driver) {
        super(driver);
        baseUrlProd = "https://altoconvertwordtopdf.com/";
        baseUrlDev = "https://altowordtopdf.pdffillers.com/";
    }
}
