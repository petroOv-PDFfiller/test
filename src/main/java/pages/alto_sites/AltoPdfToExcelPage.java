package pages.alto_sites;

import org.openqa.selenium.WebDriver;

public class AltoPdfToExcelPage extends AltoSitesBasePage {
    public AltoPdfToExcelPage(WebDriver driver) {
        super(driver);
        baseUrlProd = "https://altoconvertpdftoexcel.com/";
        baseUrlDev = "https://altopdftoexcel.pdffillers.com/";
    }
}
