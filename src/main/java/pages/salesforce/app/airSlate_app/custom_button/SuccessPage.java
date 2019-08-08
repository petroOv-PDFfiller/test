package pages.salesforce.app.airSlate_app.custom_button;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;

import static core.check.Check.checkTrue;

public class SuccessPage extends SalesAppBasePage {

    private By title = By.xpath("//*[contains(@class, 'title__') and text()='All set!']");

    public SuccessPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "Success page is not loaded");
        checkIsElementDisplayed(iframe, 10, "Success page frame");
        checkTrue(switchToFrame(iframe, 15), "Cannot switch to success page frame");
        checkIsElementDisplayed(title, 20, "Success page content");
    }
}
