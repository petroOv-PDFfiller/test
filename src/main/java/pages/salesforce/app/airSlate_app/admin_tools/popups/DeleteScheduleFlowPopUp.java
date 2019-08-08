package pages.salesforce.app.airSlate_app.admin_tools.popups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.airSlate_app.admin_tools.tabs.ScheduledFlowsTab;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;
import pages.salesforce.enums.PopUpActions;

import static core.check.Check.checkEquals;
import static data.salesforce.SalesforceTestData.ASAppPopUpTexts.DELETING_SCHEDULE_WILL_STOP_SLATES;

public class DeleteScheduleFlowPopUp extends SalesforceBasePopUp {

    public DeleteScheduleFlowPopUp(WebDriver driver) {
        super(driver);
        popUpBody = By.xpath("//*[contains(@class, 'popupBody__')]");
        btnClosePopUp = By.xpath("//div[contains(@class, 'popupHeader__')]//button");
    }

    @Override
    public void isOpened() {
        By popUpTitle = By.xpath("//*[contains(@class, 'popupTitle__')]");
        By popUpText = By.xpath("//*[contains(@class, 'popupText')]/p");
        checkIsElementDisplayed(popUpBody, 5, "Disconnect admin popup");
        checkEquals(getText(popUpTitle), "Delete schedule", "Wrong popup title");
        checkEquals(getAttribute(popUpText, "textContent"), DELETING_SCHEDULE_WILL_STOP_SLATES, "Wrong popup text");
    }

    @Step
    public ScheduledFlowsTab remove() {
        return choosePopUpAction(PopUpActions.REMOVE, ScheduledFlowsTab.class);
    }

    @Step
    public ScheduledFlowsTab cancel() {
        return choosePopUpAction(PopUpActions.CANCEL, ScheduledFlowsTab.class);
    }
}