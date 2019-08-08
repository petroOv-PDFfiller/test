package pages.salesforce.app.DaDaDocs.full_app.pop_ups;

import org.openqa.selenium.WebDriver;
import pages.pdffiller.export.print.PrintPage;

public class SalesForcePrintPage extends PrintPage {

    public SalesForcePrintPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        waitUntilPageLoaded();
        if (driver.getCurrentUrl().contains("www")) {
            checkExportReady();
        }
    }
}
