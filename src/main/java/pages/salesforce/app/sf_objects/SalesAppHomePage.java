package pages.salesforce.app.sf_objects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;

import static core.check.Check.checkTrue;

public class SalesAppHomePage extends SalesAppBasePage {

    private By salesTitle = By.xpath("//span[@title='Sales']");

    public SalesAppHomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        driver.switchTo().defaultContent();
        checkTrue(waitUntilPageLoaded(), "SalesAppHomePage isn`t loaded");
        waitForSalesforceLoading();
        checkTrue(isElementPresent(salesTitle, 60), "Sales app page is not opened");
    }
}
