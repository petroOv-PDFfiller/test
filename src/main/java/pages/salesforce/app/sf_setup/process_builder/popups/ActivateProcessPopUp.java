package pages.salesforce.app.sf_setup.process_builder.popups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;
import pages.salesforce.app.sf_setup.process_builder.ProcessBuilderEditorPage;
import utils.Logger;

import static core.check.Check.checkTrue;

public class ActivateProcessPopUp extends SalesforceBasePopUp {

    public ActivateProcessPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(popUpBody, 10), "Activate process  PopUp is not loaded");
    }

    @Step
    public ProcessBuilderEditorPage confirmActivation() {
        By btnConfirm = By.xpath("//div[contains(@class, 'modal-footer ')]//button[contains(@class, 'uiButton--brand')]");
        By spinner = By.xpath("//div[contains(@class , 'processuicommonSpinner')]");

        Logger.info("Confirm process activation");
        checkTrue(isElementPresentAndDisplayed(btnConfirm, 5), "Confirm button is not displayed");
        click(btnConfirm);
        checkTrue(isElementNotDisplayed(popUpBody, 10), "PopUp is still displayed");
        checkTrue(isElementNotDisplayed(spinner, 10), "Spinner is still displayed");
        checkTrue(isElementContainsStringInAttribute(spinner, "class", "hide", 15), "Spinner is not hide");
        ProcessBuilderEditorPage editorPage = new ProcessBuilderEditorPage(driver);
        editorPage.isOpened();
        return editorPage;
    }
}
