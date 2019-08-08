package pages.salesforce.app.airSlate_app.admin_tools.wizards.scheduled_flow;

import org.openqa.selenium.WebDriver;
import pages.salesforce.app.airSlate_app.admin_tools.wizards.LeaveWizardPopUp;

import static core.check.Check.checkTrue;

public class LeaveScheduledFlowPopUp extends LeaveWizardPopUp {

    public LeaveScheduledFlowPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkIsElementDisplayed(popUpBody, 15, "Leave create custom button popup popup");
        checkPopUpText();
    }

    private void checkPopUpText() {
        String incompleteText = "Your flow is incomplete...";
        String changesHaventSaved = "Your changes haven't been saved...";
        String titleText = getText(popUpTitle);
        checkTrue(titleText.equals(incompleteText) || titleText.equals(changesHaventSaved), "Incorrect pop up opened");
    }
}
