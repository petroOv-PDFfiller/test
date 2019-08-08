package pages.salesforce.app.airSlate_app.admin_tools.wizards;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;

import static core.check.Check.checkTrue;
import static pages.salesforce.enums.PopUpActions.LEAVE_NOW;

public class LeaveWizardPopUp extends SalesforceBasePopUp {

    protected By popUpTitle = By.xpath("//*[contains(@class, 'popupOpened__')]//*[contains(@class, 'popupTitle__')]");

    public LeaveWizardPopUp(WebDriver driver) {
        super(driver);
        popUpBody = By.xpath("//*[contains(@class, 'popupOpened__')]//*[contains(@class, 'popupBody__')]");
    }

    @Override
    public void isOpened() {
        checkIsElementDisplayed(popUpBody, 15, "Leave create custom button popup popup");
        checkPopUpText();
    }

    private void checkPopUpText() {
        String incompleteText = "Your custom button is incomplete...";
        String changesHaventSaved = "Your changes haven't been saved...";
        String titleText = getText(popUpTitle);
        checkTrue(titleText.equals(incompleteText) || titleText.equals(changesHaventSaved), "Incorrect pop up opened");
    }

    @Step
    public <T extends SalesAppBasePage> T leaveNow(Class<T> expectedPage) {
        return choosePopUpAction(LEAVE_NOW, expectedPage);
    }
}
