package pages.alto_sites;

import org.openqa.selenium.WebDriver;

public class AltoSplitPage extends AltoSitesBasePage {
    public AltoSplitPage(WebDriver driver) {
        super(driver);
        baseUrlProd = "https://altosplitpdf.com/";
        baseUrlDev = "https://altosplit.pdffillers.com/";
    }
}