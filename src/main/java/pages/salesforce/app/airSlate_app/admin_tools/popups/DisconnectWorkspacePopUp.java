package pages.salesforce.app.airSlate_app.admin_tools.popups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.admin_tools.tabs.WorkspaceTab;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;
import pages.salesforce.enums.PopUpActions;

import static core.check.Check.checkEquals;

public class DisconnectWorkspacePopUp extends SalesforceBasePopUp {

    public DisconnectWorkspacePopUp(WebDriver driver) {
        super(driver);
        popUpBody = By.xpath("//*[contains(@class, 'popupBody__')]");
        btnClosePopUp = By.xpath("//div[contains(@class, 'popupHeader__')]//button");
    }

    @Override
    public void isOpened() {
        By popUpTitle = By.xpath("//*[contains(@class, 'popupTitle__')]");
        checkIsElementDisplayed(popUpBody, 5, "Disconnect workspace popup");
        checkEquals(getText(popUpTitle), "Disconnect airSlate workspace", "Wrong popup title");
    }

    @Step
    public WorkspaceTab disconnect() {
        return choosePopUpAction(PopUpActions.DISCONNECT, WorkspaceTab.class);
    }

    @Step
    public <T extends SalesAppBasePage> T disconnect(Class<T> expectedPage) {
        return choosePopUpAction(PopUpActions.DISCONNECT, expectedPage);
    }

    @Step
    public WorkspaceTab cancel() {
        return choosePopUpAction(PopUpActions.CANCEL, WorkspaceTab.class);
    }

    @Step
    public void checkPopUpBodyText() {
        //Todo uncomment after package 2.2
        /*By popUpText = By.xpath("//*[contains(@class, 'popupText__')]");
        checkEquals(getAttribute(popUpText, "textContent"), IF_YOU_DISCONNECT_AIRSLATE_WORKSPACE,
                "Incorrect text in disconnect airSlate workspace popup");*/
    }
}
