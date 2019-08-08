package pages.salesforce.app;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.SalesforceBasePage;
import utils.Logger;

import static core.check.Check.checkTrue;

public class PhoneVerifyPage extends SalesforceBasePage {

    By btnRegister = By.xpath("//input[@value='Register']");
    By btnDontUsePhone = By.xpath("//a[contains(text(), 'Want to Register')]");

    public PhoneVerifyPage(WebDriver driver) {
        super(driver);
    }

    public void isOpened() {
        checkTrue(waitUntilPageLoaded(), "Verify page is not loaded");
        checkTrue(isElementPresentAndDisplayed(btnRegister, 15), "Verify button is not present");
    }

    public void declineVerification() {
        Logger.info("Dont register phone to salesforce org");
        checkTrue(isElementPresentAndDisplayed(btnDontUsePhone, 5), "Dont use phone umber button is not present");
        click(btnDontUsePhone);
        checkTrue(isElementDisappeared(btnDontUsePhone, 5), "Dont use phone number button is still present");
    }
}
