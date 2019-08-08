package pages.salesforce.app.airSlate_app.admin_tools.popups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.airSlate_app.admin_tools.tabs.CustomButtonTab;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;
import pages.salesforce.enums.PopUpActions;

import static core.check.Check.checkEquals;

public class DeleteCustomButtonPopUp extends SalesforceBasePopUp {

    public DeleteCustomButtonPopUp(WebDriver driver) {
        super(driver);
        popUpBody = By.xpath("//*[contains(@class, 'popupOpened__')]//*[contains(@class, 'popupBody__')]");
    }

    @Override
    public void isOpened() {
        By popUpTitle = By.xpath("//*[contains(@class, 'popupOpened__')]//*[contains(@class, 'popupTitle__')]");
        checkIsElementDisplayed(popUpBody, 15, "Delete custom button popup");
        checkEquals(getText(popUpTitle), "Delete custom button", "Incorrect pop up opened");
    }

    @Step
    public CustomButtonTab remove() {
        return choosePopUpAction(PopUpActions.REMOVE, CustomButtonTab.class);
    }

    @Step
    public String getPopUpBodyText() {
        By popUpText = By.xpath("//*[contains(@class, 'popupText__')]");
        return getAttribute(popUpText, "textContent");
    }

    @Step
    public CustomButtonTab cancel() {
        return choosePopUpAction(PopUpActions.CANCEL, CustomButtonTab.class);
    }
}
