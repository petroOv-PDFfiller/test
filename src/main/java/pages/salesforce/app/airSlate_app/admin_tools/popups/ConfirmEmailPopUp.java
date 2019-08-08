package pages.salesforce.app.airSlate_app.admin_tools.popups;

import core.check.SoftCheck;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;

import static core.check.Check.checkEquals;
import static core.check.Check.checkFail;
import static pages.salesforce.enums.PopUpActions.DONE;

public class ConfirmEmailPopUp extends SalesforceBasePopUp {

    public ConfirmEmailPopUp(WebDriver driver) {
        super(driver);
        popUpBody = By.xpath("//*[contains(@class, 'popupOpened__')]//*[contains(@class, 'popupBody__')]");
        btnClosePopUp = By.xpath("//*[contains(@class, 'popupOpened__')]//button[contains(@class, 'close__')]");
    }

    @Override
    public void isOpened() {
        By popUpTitle = By.xpath("//*[contains(@class, 'popupOpened__')]//*[contains(@class, 'confirmEmailTitle__')]");
        checkIsElementDisplayed(popUpBody, 15, "Confirm email popup");
        checkEquals(getText(popUpTitle), "Confirm Your Email", "Incorrect pop up opened");
    }

    @Step
    public ConfirmEmailPopUp setPin(String pin) {
        if (pin.length() < 7) {
            SoftCheck softCheck = new SoftCheck();
            for (int i = 0; i < pin.length(); i++) {
                By inputPin = By.xpath("//input[@name='" + i + "']");
                softCheck.checkTrue(isElementPresent(inputPin), i + " pin input not present");
                type(inputPin, String.valueOf(pin.toCharArray()[i]));
            }
            softCheck.checkAll();
        } else {
            checkFail("Pin length more than 6 symbols");
        }
        return this;
    }

    @Step
    public <T extends SalesAppBasePage> T done(Class<T> expectedPage) {
        return choosePopUpAction(DONE, expectedPage);
    }
}
