package pages.salesforce.app.airSlate_app.admin_tools.popups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.airSlate_app.admin_tools.tabs.AccountTab;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;
import pages.salesforce.enums.PopUpActions;

import static core.check.Check.checkEquals;

public class DisconnectAdminPopUp extends SalesforceBasePopUp {

    public DisconnectAdminPopUp(WebDriver driver) {
        super(driver);
        popUpBody = By.xpath("//*[contains(@class, 'popupBody__')]");
        btnClosePopUp = By.xpath("//div[contains(@class, 'popupHeader__')]//button");
    }

    @Override
    public void isOpened() {
        By popUpTitle = By.xpath("//*[contains(@class, 'popupTitle__')]");
        checkIsElementDisplayed(popUpBody, 5, "Disconnect admin popup");
        checkEquals(getText(popUpTitle), "Are you sure?", "Wrong popup title");
    }

    @Step
    public AccountTab disconnect() {
        return choosePopUpAction(PopUpActions.DISCONNECT, AccountTab.class);
    }

    @Step
    public AccountTab cancel() {
        return choosePopUpAction(PopUpActions.CANCEL, AccountTab.class);
    }
}
