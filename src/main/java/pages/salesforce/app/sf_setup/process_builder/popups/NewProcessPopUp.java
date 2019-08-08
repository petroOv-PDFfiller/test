package pages.salesforce.app.sf_setup.process_builder.popups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;
import pages.salesforce.app.sf_setup.process_builder.ProcessBuilderEditorPage;
import utils.Logger;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class NewProcessPopUp extends SalesforceBasePopUp {

    public NewProcessPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(popUpBody, 10), "New Process PopUp is not loaded");
    }

    @Step
    public NewProcessPopUp setName(String name) {
        By processName = By.id("processName");

        Logger.info("Set new process name:" + name);
        checkTrue(isElementPresentAndDisplayed(processName, 5), "Process name input is not displayed");
        type(processName, name);
        isOpened();
        return this;
    }

    @Step
    public NewProcessPopUp setRecordChangesAsProcessTrigger() {
        By triggerSelect = By.xpath("//div[contains(@class,'margin')]/select");

        Logger.info("Set process trigger - when record changes");
        String triggerValue = "workflow";
        checkTrue(isElementPresentAndDisplayed(triggerSelect, 5), "Trigger select is not displayed");
        Select trigger = new Select(driver.findElement(triggerSelect));
        trigger.selectByValue(triggerValue);
        checkEquals(getAttribute(triggerSelect, "value"), triggerValue, "Value is not selected");
        isOpened();
        return this;
    }

    @Step
    public ProcessBuilderEditorPage saveProcess() {
        By btnSave = By.xpath("//div[contains(@class, 'modal-footer ')]//button[contains(@class, 'uiButton--brand')]");
        By spinner = By.xpath("//div[contains(@class , 'processuicommonSpinner')]");

        Logger.info("Save process");
        checkTrue(isElementPresentAndDisplayed(btnSave, 5), "Save button is not present");
        click(btnSave);
        checkTrue(isElementNotDisplayed(popUpBody, 10), "PopUp is still displayed");
        checkTrue(isElementNotDisplayed(spinner, 10), "Spinner is still displayed");
        checkTrue(isElementContainsStringInAttribute(spinner, "class", "hide", 15), "Spinner is not hide");
        ProcessBuilderEditorPage editorPage = new ProcessBuilderEditorPage(driver);
        editorPage.isOpened();
        return editorPage;
    }
}
