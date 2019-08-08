package pages.alto_sites;

import org.openqa.selenium.WebDriver;

public class AltoJpgToPdfPage extends AltoSitesBasePage {


    public AltoJpgToPdfPage(WebDriver driver) {
        super(driver);
        baseUrlProd = "https://altoconvertjpgtopdf.com/";
        baseUrlDev = "https://altojpgtopdf.pdffillers.com/";
    }
}
