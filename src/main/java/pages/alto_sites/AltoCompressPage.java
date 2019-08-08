package pages.alto_sites;

import org.openqa.selenium.WebDriver;

public class AltoCompressPage extends AltoSitesBasePage {
    public AltoCompressPage(WebDriver driver) {
        super(driver);
        baseUrlProd = "https://altocompresspdf.com/";
        baseUrlDev = "https://altocompress.pdffillers.com/";
    }
}
