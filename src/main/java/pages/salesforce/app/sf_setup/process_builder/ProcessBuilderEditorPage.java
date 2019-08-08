package pages.salesforce.app.sf_setup.process_builder;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import pages.salesforce.app.sf_setup.SetupSalesforcePage;
import pages.salesforce.app.sf_setup.process_builder.popups.ActivateProcessPopUp;
import pages.salesforce.app.sf_setup.process_builder.popups.DeactivateProcessPopUp;
import pages.salesforce.app.sf_setup.process_builder.popups.SelectFieldPopUp;
import utils.Logger;

import static core.check.Check.checkTrue;

public class ProcessBuilderEditorPage extends SetupSalesforcePage {

    private By title = By.id("processHeader");
    private By spinner = By.xpath("//div[contains(@class , 'processuicommonSpinner')]");

    public ProcessBuilderEditorPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(title, 10), "process builder editor is not opened");
    }

    @Step("Add object {0}")
    public ProcessBuilderEditorPage addObject(String objectName) {
        By addObject = By.xpath("//a[@title='Add Object']");
        By object = By.xpath("//a[@role='option' and .='" + objectName + "']");
        By objectInput = By.xpath("//div[contains(@class, 'panelContainer')]//input[@role='combobox']");

        Logger.info("Add object: " + objectName);
        checkTrue(isElementPresentAndDisplayed(addObject, 5), "add object block is not displayed");
        click(addObject);
        checkTrue(isElementPresentAndDisplayed(objectInput, 10), "Object input is not displayed");
        click(objectInput);
        type(objectInput, objectName);
        checkTrue(isElementPresentAndDisplayed(object, 5), "Object is not displayed");
        click(object);
        isOpened();
        return this;
    }

    @Step
    public ProcessBuilderEditorPage setProcessStartAlways() {
        By rbAlways = By.xpath("//input[@value='onAllChanges']/parent::div/label");

        Logger.info("Set process trigger - always");
        checkTrue(isElementPresentAndDisplayed(rbAlways, 10), "Always trigger is not displayed");
        click(rbAlways);
        isOpened();
        return this;
    }

    @Step
    public ProcessBuilderEditorPage saveObject() {
        By btnSave = By.xpath("//div[contains(@class , 'panelContainer')]//button[contains(@class, 'saveButton')]");

        Logger.info("Save object");
        checkTrue(isElementPresentAndDisplayed(btnSave, 5), "Save button is not displayed");
        click(btnSave);
        checkTrue(isElementNotDisplayed(spinner, 10), "Spinner is still displayed");
        checkTrue(isElementContainsStringInAttribute(spinner, "class", "hide", 15), "Spinner is not hide");
        isOpened();
        return this;
    }

    @Step("Set criteria {0}")
    public ProcessBuilderEditorPage setCriteriaWithoutAction(String criteriaName) {
        By btnAddCriteria = By.xpath("//a[@title = 'Add Criteria']");
        By criteriaNameField = By.xpath("//span[.='Criteria Name']/parent::label//input");
        By rbAlways = By.xpath("//input[@value='always']/parent::div/label");

        Logger.info("Set new criteria without actions: name - " + criteriaName);
        checkTrue(isElementPresentAndDisplayed(btnAddCriteria, 5), "Add criteria button is not displayed");
        click(btnAddCriteria);
        checkTrue(isElementPresentAndDisplayed(criteriaNameField, 10), "Criteria name is not displayed");
        type(criteriaNameField, criteriaName);
        checkTrue(isElementPresentAndDisplayed(rbAlways, 10), "Always criteria option is not displayed");
        click(rbAlways);
        isOpened();
        return this;
    }

    @Step
    public ProcessBuilderEditorPage saveCriteria() {
        By btnSave = By.xpath("(//div[contains(@class , 'panelContainer')]//button[contains(@class, 'saveButton')])[2]");

        Logger.info("Save criteria");
        checkTrue(isElementPresentAndDisplayed(btnSave, 5), "Save criteria button is not displayed");
        click(btnSave);
        skipSpinner();
        isOpened();
        return this;
    }

    @Step("Add action type -{0}, name - {1}, apexClass - {2}")
    public ProcessBuilderEditorPage addAction(String actionTypeName, String actionName, String apexClassName) {
        By btnAddAction = By.xpath("//div[@class ='addButtonWrapper']/a");
        By actionType = By.xpath("//span[@class ='actionTypes']//select");
        By actionNameField = By.xpath("//span[text()='Action Name']/parent::label//input");
        By apexClassField = By.xpath("//span[text()='Apex Class']/parent::label//input");
        By apexClass = By.xpath("//a[@role = 'option' and .='" + apexClassName + "']/parent::li");

        Logger.info("Add action: actionType - " + actionTypeName + ", actionName - " + actionName + ", apexClass - " + apexClassName);
        checkTrue(isElementPresentAndDisplayed(btnAddAction, 5), "Add action button is not displayed");
        click(btnAddAction);
        checkTrue(isElementPresentAndDisplayed(actionType, 10), "Action type select is not displayed");
        Select actionTypeSelect = new Select(driver.findElement(actionType));
        actionTypeSelect.selectByVisibleText(actionTypeName);
        skipSpinner();
        checkTrue(isElementPresentAndDisplayed(actionNameField, 10), "Action name field is not displayed");
        type(actionNameField, actionName);
        checkTrue(isElementPresentAndDisplayed(apexClassField, 5), "apex class field is not present");
        type(apexClassField, apexClassName);
        checkTrue(isElementPresentAndDisplayed(apexClass, 10), apexClassName + " is not displayed");
        clickJS(apexClass);
        hoverOverAndClick(apexClass);
        skipSpinner();
        isOpened();
        return this;
    }

    @Step("Set automated template sender {0}, type -{1}, templateId - {2}")
    public SelectFieldPopUp setAutomatedTemplateSenderVariables(String email, String parentIdTypeName, String templateId) {
        By emailField = By.xpath("//div[@title = 'Email']/ancestor::tr//input");
        By parentIdField = By.xpath("//div[@title = 'Parent ID']/ancestor::tr//a");
        By parentIdType = By.xpath("//div[@title = 'Parent ID']/ancestor::tr//select");
        By templateIdField = By.xpath("//div[@title = 'Template ID']/ancestor::tr//input");

        Logger.info("Set automated template sender: email - " + email + ", parentId type - " + parentIdTypeName + ", templateID - " + templateId);
        checkTrue(isElementPresentAndDisplayed(emailField, 5), "email field is not displayed");
        type(emailField, email);
        checkTrue(isElementPresentAndDisplayed(templateIdField, 5), "TemplateID field is not displayed");
        type(templateIdField, templateId);
        checkTrue(isElementPresentAndDisplayed(parentIdType, 5), "parent type selec tis not displayed");
        Select parentIdTypeSelect = new Select(driver.findElement(parentIdType));
        parentIdTypeSelect.selectByVisibleText(parentIdTypeName);
        checkTrue(isElementPresentAndDisplayed(parentIdField, 5), "parent id field is not displayed");
        click(parentIdField);
        SelectFieldPopUp selectFieldPopUp = new SelectFieldPopUp(driver);
        selectFieldPopUp.isOpened();
        return selectFieldPopUp;
    }

    @Step("Set opportunity automated template sender {0}, templateId - {1}")
    public SelectFieldPopUp setOpportunityAutomatedTemplateSenderVariables(String parentIdTypeName, String templateId) {
        By parentIdField = By.xpath("//div[@title = 'Parent ID']/ancestor::tr//a");
        By parentIdType = By.xpath("//div[@title = 'Parent ID']/ancestor::tr//select");
        By templateIdField = By.xpath("//div[@title = 'Template ID']/ancestor::tr//input");

        Logger.info("Set opportunity automated template sender: parentId type - " + parentIdTypeName + ", templateID - " + templateId);
        checkTrue(isElementPresentAndDisplayed(templateIdField, 5), "TemplateID field is not displayed");
        type(templateIdField, templateId);
        checkTrue(isElementPresentAndDisplayed(parentIdType, 5), "parent type selec tis not displayed");
        Select parentIdTypeSelect = new Select(driver.findElement(parentIdType));
        parentIdTypeSelect.selectByVisibleText(parentIdTypeName);
        checkTrue(isElementPresentAndDisplayed(parentIdField, 5), "parent id field is not displayed");
        click(parentIdField);
        SelectFieldPopUp selectFieldPopUp = new SelectFieldPopUp(driver);
        selectFieldPopUp.isOpened();
        return selectFieldPopUp;
    }

    @Step("Add recipient {0}")
    public ProcessBuilderEditorPage addRecipientField(String recipientName) {
        By btnAddRow = By.xpath("(//a[@class ='addRow'])[2]");
        By newRecord = By.xpath("//input[@placeholder ='Find a variable...']");
        By recipientOption = By.xpath("//a[@role='option' and .='Recipient Name']");
        By recipientField = By.xpath("(//input[@placeholder ='Find a variable...']/ancestor::tr//input)[2]");

        Logger.info("Add recipient :" + recipientName);
        checkTrue(isElementPresentAndDisplayed(btnAddRow, 5), "add row button is not displayed");
        click(btnAddRow);
        checkTrue(isElementPresentAndDisplayed(newRecord, 5), "New record field is not displayed");
        click(newRecord);
        checkTrue(isElementPresentAndDisplayed(recipientOption, 5), "recipient option is not displayed");
        click(recipientOption);
        checkTrue(isElementPresentAndDisplayed(recipientField, 5), "recipient field is not displayed");
        type(recipientField, recipientName);
        isOpened();
        return this;
    }

    @Step
    public ProcessBuilderEditorPage saveAction() {
        By btnSave = By.xpath("(//div[contains(@class , 'panelContainer')]//button[contains(@class, 'saveButton')])[3]");

        Logger.info("Save action");
        checkTrue(isElementPresentAndDisplayed(btnSave, 5), "Save action button is not displayed");
        click(btnSave);
        skipSpinner();
        isOpened();
        return this;
    }

    @Step
    public ProcessBuilderEditorPage activateProcess() {
        By btnActivate = By.xpath("//span[@dir='ltr' and .='Activate']");

        Logger.info("Activate process");
        checkTrue(isElementPresentAndDisplayed(btnActivate, 5), "Activate button is not displayed");
        click(btnActivate);
        ActivateProcessPopUp activateProcessPopUp = new ActivateProcessPopUp(driver);
        activateProcessPopUp.isOpened();
        return activateProcessPopUp.confirmActivation();
    }

    private void skipSpinner() {
        checkTrue(isElementNotDisplayed(spinner, 20), "Spinner is still displayed");
        checkTrue(isElementNotDisplayed(spinner, 20), "Spinner is still displayed");
        checkTrue(isElementContainsStringInAttribute(spinner, "class", "hide", 15), "Spinner is not hide");
    }

    @Step
    public ProcessBuilderEditorPage deactivateProcess() {
        By btnDeactivate = By.xpath("//span[@dir='ltr' and .='Deactivate']");

        Logger.info("Deactivate process");
        checkTrue(isElementPresentAndDisplayed(btnDeactivate, 10), "Deactivate Button is not displayed");
        click(btnDeactivate);
        DeactivateProcessPopUp processPopUp = new DeactivateProcessPopUp(driver);
        return processPopUp.confirmDeactivation();
    }

    @Step
    public ProcessBuilderPage backToProcessBuilder() {
        By btnViewAllProcesses = By.xpath("//span[@dir='ltr' and .='View All Processes']");

        Logger.info("Go back to process builder");
        checkTrue(isElementPresentAndDisplayed(btnViewAllProcesses, 10), "View all processes btn is not displayed");
        click(btnViewAllProcesses);
        ProcessBuilderPage processBuilderPage = new ProcessBuilderPage(driver);
        processBuilderPage.isOpened();
        return processBuilderPage;
    }
}
