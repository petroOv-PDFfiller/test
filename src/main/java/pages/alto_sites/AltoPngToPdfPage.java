package pages.alto_sites;

import org.openqa.selenium.WebDriver;

public class AltoPngToPdfPage extends AltoSitesBasePage {

    public AltoPngToPdfPage(WebDriver driver) {
        super(driver);
        baseUrlProd = "https://altoconvertpngtopdf.com/";
        baseUrlDev = "https://altopngtopdf.pdffillers.com/";
    }
}
