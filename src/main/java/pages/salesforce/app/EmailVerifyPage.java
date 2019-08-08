package pages.salesforce.app;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.SalesforceBasePage;
import utils.Logger;

import static core.check.Check.checkTrue;

public class EmailVerifyPage extends SalesforceBasePage {

    By codeInput = By.id("emc");
    By btnVerify = By.id("save");
    By cbDontAskAgain = By.id("RememberDeviceCheckbox");

    public EmailVerifyPage(WebDriver driver) {
        super(driver);
    }

    public void isOpened() {
        checkTrue(waitUntilPageLoaded(), "Verify page is not loaded");
        checkTrue(isElementPresentAndDisplayed(btnVerify, 15), "Verify button is not present");
        checkTrue(isElementPresentAndDisplayed(codeInput, 15), "Code input field is not present");
    }

    public void verify(String code) {
        Logger.info("Get salesforce login verification code from email");

        checkTrue(isElementPresentAndDisplayed(codeInput, 5), "Code input is not displayed");
        type(codeInput, code);
        checkTrue(isElementPresentAndDisplayed(btnVerify, 5), "Verify button is not displayed");
        click(btnVerify);
    }
}
