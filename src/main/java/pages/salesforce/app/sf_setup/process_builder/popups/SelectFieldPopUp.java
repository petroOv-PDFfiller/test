package pages.salesforce.app.sf_setup.process_builder.popups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;
import pages.salesforce.app.sf_setup.process_builder.ProcessBuilderEditorPage;
import utils.Logger;

import static core.check.Check.checkTrue;

public class SelectFieldPopUp extends SalesforceBasePopUp {

    public SelectFieldPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(popUpBody, 10), "New Process PopUp is not loaded");
    }

    @Step
    public SelectFieldPopUp setContactId() {
        By field = By.xpath("//div[contains(@class, 'slds-modal')]//input[@role = 'combobox']");
        By contactIdOption = By.xpath("//a[@role = 'option' and .='Contact ID']");

        Logger.info("Set contact id as field");
        checkTrue(isElementPresentAndDisplayed(field, 5), "field select is not displayed");
        clickJS(field);
        type(field, "Contact ID");
        checkTrue(isElementPresentAndDisplayed(contactIdOption, 5), "Contact id option is not displayed");
        clickJS(contactIdOption);
        isOpened();
        return this;
    }

    @Step
    public SelectFieldPopUp setOpportunityId() {
        By field = By.xpath("//div[contains(@class, 'slds-modal')]//input[@role = 'combobox']");
        By opportunityIdOption = By.xpath("//a[@role = 'option' and .='Opportunity ID']");

        Logger.info("Set opportunity id as field");
        checkTrue(isElementPresentAndDisplayed(field, 5), "field select is not displayed");
        clickJS(field);
        type(field, "Opportunity ID");
        checkTrue(isElementPresentAndDisplayed(opportunityIdOption, 5), "Opportunity id option is not displayed");
        clickJS(opportunityIdOption);
        isOpened();
        return this;
    }

    @Step
    public ProcessBuilderEditorPage chooseField() {
        By btnChoose = By.xpath("//div[contains(@class, 'modal-footer ')]//button[contains(@class, 'uiButton--brand')]");
        By spinner = By.xpath("//div[contains(@class , 'processuicommonSpinner')]");

        checkTrue(isElementPresentAndDisplayed(btnChoose, 5), "Save button is not present");
        click(btnChoose);
        checkTrue(isElementNotDisplayed(popUpBody, 15), "PopUp is still displayed");
        checkTrue(isElementNotDisplayed(spinner, 15), "Spinner is still displayed");
        checkTrue(isElementContainsStringInAttribute(spinner, "class", "hide", 15), "Spinner is not hide");
        ProcessBuilderEditorPage editorPage = new ProcessBuilderEditorPage(driver);
        editorPage.isOpened();
        return editorPage;
    }
}
