package pages.salesforce.app.sf_setup.object_manager;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.enums.SalesforceFields;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class EditCustomObjectPage extends ObjectManagerPage {
    private By inputQuickFind = By.id("globalQuickfind");
    private By titleEditObject = By.xpath("//*[@data-aura-class='objectManagerLeftNav']");
    private By linkFieldsRelationships = By.cssSelector("a[data-list='Fields & Relationships']");
    private By titleFieldsRelationships = By.cssSelector("[title='Fields & Relationships']");
    private By checkboxLookup = By.id("dtypeY");
    private By checkboxFormula = By.id("dtypeZ");
    private By checkboxText = By.id("dtypeS");
    private By checkboxTextArea = By.id("dtypeX");
    private By checkboxTextAreaLong = By.id("dtypeJ");
    private By checkboxTextAreaRich = By.id("dtypez");
    private By checkboxNumber = By.id("dtypeN");
    private By checkboxCurrency = By.id("dtypeC");
    private By btnNew = By.cssSelector("[title='Custom Field']");
    private By inputLabel = By.id("MasterLabel");
    private By inputPluralLabel = By.id("PluralLabel");
    private By inputFieldName = By.id("DeveloperName");
    private By inputLength = By.id("Length");
    private By inputLengthInNumber = By.id("digleft");
    private By inputDecimalPlaces = By.id("Scale");
    private By checkboxRequired = By.xpath("//td[contains(text(), 'Required')]/../td[2]/input[@type='checkbox']");
    private By btnNext = By.xpath("(//input[@title='Next'])[1]");
    private By titleThirdStep = By.xpath("//*[contains(text(),'Step 3. Establish field-level security')]");
    private By titleFourthStep = By.xpath("//*[contains(text(),'Step 4. Add to page layouts')]");
    private By titleFourthStepLookups = By.xpath("//*[contains(text(),'Step 4. Establish field-level security')]");
    private By titleFifthStepLookups = By.xpath("//*[contains(text(),'Step 5. Add reference field to Page Layouts')]");
    private By titleFifthStepFormula = By.xpath("//*[contains(text(),'Step 5. Add to page layouts')]");
    private By titleSixStepLookups = By.xpath("//*[contains(text(),'Step 6. Add custom related lists')]");
    private By btnSave = By.xpath("(//input[@title='Save'])[1]");
    private By dropdownRelatedTo = By.id("DomainEnumOrId");
    private By linkObjectsListFirstElement = By.xpath("//*[@data-aura-class='uiVirtualDataGrid--default uiVirtualDataGrid']/tbody/tr[1]/th/a");
    private By titleObjectManager = By.xpath("//*[@class='breadcrumbDetail uiOutputText' and contains(text(),'Object Manager')]");
    private By loaderSpinner = By.cssSelector("[class='indicator  forceInlineSpinner']");
    private By titleSingularLabel = By.xpath("//*[@class='slds-form-element__label' and contains(text(), 'Singular Label')]/../div/span");
    private By btnEdit = By.xpath("//*[@class='slds-button slds-button--neutral uiButton']/span[contains(text(), 'Edit')]");
    private By btnDelete = By.xpath("//button[@title='delete']");
    private By btnDeleteInPopup = By.xpath("//*[@class='forceModalActionContainer--footerAction forceModalActionContainer']/button[@title='delete']");
    private By checkboxTextSecondStep = By.id("fdtypeS");
    private By checkboxNumberSecondStep = By.id("fdtypeN");
    private By checkboxCurrencySecondStep = By.id("fdtypeC");
    private By checkboxNoneSecondStep = By.id("fdtypeZ");
    private By inputFormulaEditor = By.id("CalculatedFormula");

    public EditCustomObjectPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "Edit object Page is not loaded");
        checkTrue(isElementPresent(titleEditObject, 60), "Setup app page is not opened");
    }

    @Step("Create new field. Parametes: fieldType - {0}, fieldName - {1}, length - {2}, required - {3}")
    public EditCustomObjectPage createNewFieldWithLength(SalesforceFields fieldType, String fieldLabel, String fieldName, String length, boolean required) {
        checkTrue(isElementDisplayed(linkFieldsRelationships, 5), "Edit object Page is not loaded");
        click(linkFieldsRelationships);
        checkTrue(isElementDisplayed(btnNew, 5), "Create button not present");
        click(btnNew);
        checkTrue(switchToFrame(iframe), "Iframe not present");
        switch (fieldType) {
            case TEXT: {
                checkTrue(isElementPresent(checkboxText, 5), "Couldn't locate choosen radiobutton");
                click(checkboxText);
                break;
            }
            case NUMBER: {
                checkTrue(isElementPresent(checkboxNumber, 5), "Couldn't locate choosen radiobutton");
                click(checkboxNumber);
                break;
            }
            case TEXT_AREA: {
                checkTrue(isElementPresent(checkboxTextArea, 5), "Couldn't locate choosen radiobutton");
                click(checkboxTextArea);
                break;
            }
            case TEXT_AREA_LONG: {
                checkTrue(isElementPresent(checkboxTextAreaLong, 5), "Couldn't locate choosen radiobutton");
                click(checkboxTextAreaLong);
                break;
            }
            case TEXT_AREA_RICH: {
                checkTrue(isElementPresent(checkboxTextAreaRich, 5), "Couldn't locate choosen radiobutton");
                click(checkboxTextAreaRich);
                break;
            }
        }
        click(btnNext);
        hoverOverAndClick(inputLabel);
        type(inputLabel, fieldLabel);
        if (!fieldName.isEmpty()) {
            type(inputFieldName, fieldName);
        }
        if (!length.isEmpty() && fieldType != SalesforceFields.TEXT_AREA && fieldType != SalesforceFields.TEXT_AREA_LONG
                && fieldType != SalesforceFields.TEXT_AREA_RICH) {
            if (fieldType == SalesforceFields.NUMBER) {
                hoverOverAndClick(inputLengthInNumber);
                type(inputLengthInNumber, length);
            } else {
                hoverOverAndClick(inputLength);
                type(inputLength, length);
            }
        }
        if (required) {
            click(checkboxRequired);
        }
        click(btnNext);
        checkTrue(isElementPresentAndDisplayed(titleThirdStep, 5), "Third step not shown");
        click(btnNext);
        checkTrue(isElementPresentAndDisplayed(titleFourthStep, 5), "Fourth step not shown");
        click(btnSave);
        EditCustomObjectPage editCustomObjectPage = new EditCustomObjectPage(driver);
        editCustomObjectPage.isOpened();
        return editCustomObjectPage;
    }

    @Step("Create new field. Parametes: fieldType - {0}, fieldName - {1}, length - {2}, required - {3}")
    public EditCustomObjectPage createNewCurrencyField(String fieldLabel, String fieldName, String length, String decimalPlaces, boolean required) {
        checkTrue(isElementDisplayed(linkFieldsRelationships, 5), "Edit object Page is not loaded");
        click(linkFieldsRelationships);
        click(btnNew);
        checkTrue(switchToFrame(iframe), "Iframe not present");
        checkTrue(isElementPresent(checkboxCurrency, 5), "Couldn't locate choosen radiobutton");
        click(checkboxCurrency);
        click(btnNext);
        hoverOverAndClick(inputLabel);
        type(inputLabel, fieldLabel);
        if (!fieldName.isEmpty()) {
            hoverOverAndClick(inputFieldName);
            type(inputFieldName, fieldName);
        }

        if (!length.isEmpty()) {
            hoverOverAndClick(inputLengthInNumber);
            type(inputLengthInNumber, length);
        }
        if (!decimalPlaces.isEmpty()) {
            hoverOverAndClick(inputDecimalPlaces);
            type(inputDecimalPlaces, decimalPlaces);
        }
        if (required) {
            click(checkboxRequired);
        }
        click(btnNext);
        checkTrue(isElementPresentAndDisplayed(titleThirdStep, 5), "Third step not shown");
        click(btnNext);
        checkTrue(isElementPresentAndDisplayed(titleFourthStep, 5), "Fourth step not shown");
        click(btnSave);
        EditCustomObjectPage editCustomObjectPage = new EditCustomObjectPage(driver);
        editCustomObjectPage.isOpened();
        return editCustomObjectPage;
    }

    @Step("Create new field. Parametes: fieldType - {0}, fieldName - {1}, length - {2}, required - {3}")
    public EditCustomObjectPage createNewFormulaField(SalesforceFields fieldSecondaryType, String fieldLabel, String fieldName, String formula, String length, String decimalPlaces, boolean required) {
        checkTrue(isElementDisplayed(linkFieldsRelationships, 5), "Edit object Page is not loaded");
        click(linkFieldsRelationships);
        click(btnNew);
        checkTrue(switchToFrame(iframe), "Iframe not present");
        checkTrue(isElementPresent(checkboxFormula, 5), "Couldn't locate choosen radiobutton");
        click(checkboxFormula);
        click(btnNext);
        hoverOverAndClick(inputLabel);
        type(inputLabel, fieldLabel);
        if (!fieldName.isEmpty()) {
            type(inputFieldName, fieldName);
        }
        if (!decimalPlaces.isEmpty()) {
            hoverOverAndClick(inputDecimalPlaces);
            type(inputDecimalPlaces, decimalPlaces);
        }
        switch (fieldSecondaryType) {
            default: {
                checkTrue(isElementPresent(checkboxNoneSecondStep, 5), "Couldn't locate choosen radiobutton");
                click(checkboxNoneSecondStep);
                break;
            }
            case TEXT: {
                checkTrue(isElementPresent(checkboxTextSecondStep, 5), "Couldn't locate choosen radiobutton");
                click(checkboxTextSecondStep);
                break;
            }
            case NUMBER: {
                checkTrue(isElementPresent(checkboxNumberSecondStep, 5), "Couldn't locate choosen radiobutton");
                click(checkboxNumberSecondStep);
                break;
            }
            case CURRENCY: {
                checkTrue(isElementPresentAndDisplayed(checkboxCurrencySecondStep, 5), "Couldn't locate choosen radiobutton");
                click(checkboxCurrencySecondStep);
                break;
            }
        }
        click(btnNext);
        checkTrue(isElementPresentAndDisplayed(inputFormulaEditor, 5), "It impossible to find  formula editor");
        hoverOverAndClick(inputFormulaEditor);
        type(inputFormulaEditor, formula);
        if (!length.isEmpty()) {
            hoverOverAndClick(inputLength);
            type(inputLength, length);
        }

        if (required) {
            click(checkboxRequired);
        }
        click(btnNext);
        checkTrue(isElementPresentAndDisplayed(titleFourthStepLookups, 5), "Fourthstep not shown");
        click(btnNext);
        checkTrue(isElementPresentAndDisplayed(titleFifthStepFormula, 5), "Fifth step not shown");
        click(btnSave);
        EditCustomObjectPage editCustomObjectPage = new EditCustomObjectPage(driver);
        editCustomObjectPage.isOpened();
        return editCustomObjectPage;
    }


    @Step("Create new field. Parametes: fieldType - {0}, fieldName - {1}, lookObjectName - {2}, required - {3}")
    public EditCustomObjectPage createNewFieldWithRelation(SalesforceFields fieldType, String fieldName, String lookObjectName, boolean required) {
        checkTrue(isElementDisplayed(linkFieldsRelationships, 5), "Edit object Page is not loaded");
        click(linkFieldsRelationships);
        click(btnNew);
        checkTrue(switchToFrame(iframe), "Iframe not present");
        String type = fieldType.getName();

        switch (fieldType) {
            case LOOKUP_RELATIONSHIP: {
                checkTrue(isElementPresent(checkboxLookup, 5), "Couldn't locate choosen radiobutton");
                click(checkboxLookup);
                click(btnNext);
                By chosenObjectType = By.xpath("//*[@id='DomainEnumOrId']/option[@value='" + lookObjectName + "']");
                checkTrue(isElementPresent(chosenObjectType, 5), "lookObjectName not presented in list");
                click(dropdownRelatedTo);
                click(chosenObjectType);
                click(btnNext);
                click(inputLabel);
                click(btnNext);
                checkTrue(isElementPresentAndDisplayed(titleFourthStepLookups, 5), "Fourth step not shown");
                click(btnNext);
                checkTrue(isElementPresentAndDisplayed(titleFifthStepLookups, 5), "Fifth step not shown");
                click(btnNext);
                checkTrue(isElementPresentAndDisplayed(titleSixStepLookups, 5), "Six step not shown");
                click(btnSave);
                break;
            }
        }
        EditCustomObjectPage editCustomObjectPage = new EditCustomObjectPage(driver);
        editCustomObjectPage.isOpened();
        return editCustomObjectPage;
    }

    @Step("Open fields list")
    public EditCustomObjectPage openListOfFields() {
        checkTrue(isElementPresentAndDisplayed(linkFieldsRelationships, 30), "Object not open");
        click(linkFieldsRelationships);
        checkTrue(isElementPresentAndDisplayed(titleFieldsRelationships, 30), "Fields list not present");
        EditCustomObjectPage editCustomObjectPage = new EditCustomObjectPage(driver);
        editCustomObjectPage.isOpened();
        return editCustomObjectPage;
    }

    @Step("Open Edit object page. Parametes: objectName - {0}")
    public EditCustomObjectPage openEditObjectPage(String objectName) {
        checkTrue(isElementPresentAndDisplayed(inputQuickFind, 5), "CreateButton not present on page");
        hoverOverAndClick(inputQuickFind);
        type(objectName);
        checkTrue(isElementPresentAndDisplayed(titleObjectManager, 5), objectName + " not found");
        if (isElementPresentAndDisplayed(loaderSpinner, 5)) {
            isElementDisappeared(loaderSpinner, 5);
        }
        checkEquals(getText(linkObjectsListFirstElement), objectName, objectName + " not found");
        click(linkObjectsListFirstElement);
        EditCustomObjectPage editCustomObjectPage = new EditCustomObjectPage(driver);
        editCustomObjectPage.isOpened();
        checkEquals(getText(titleSingularLabel), objectName, "There are not correct object opened");
        return editCustomObjectPage;
    }

    @Step("Open end rename the object. Parameters: objectName - {0}, newObjectName - {1}, newPluralLabel - {2}")
    public EditCustomObjectPage renameObject(String objectName, String newObjectName, String newPluralLabel) {
        openEditObjectPage(objectName);
        checkTrue(isElementPresentAndDisplayed(btnEdit, 5), "Object not editable");
        click(btnEdit);
        checkTrue(switchToFrame(iframe), "Iframe not present");
        type(inputLabel, newObjectName);
        if (!newPluralLabel.isEmpty()) {
            type(inputPluralLabel, newPluralLabel);
        }
        click(btnSave);
        EditCustomObjectPage editCustomObjectPage = new EditCustomObjectPage(driver);
        editCustomObjectPage.isOpened();
        return editCustomObjectPage;
    }

    @Step("Find object")
    public ObjectManagerPage findObject(String objectName) {
        checkTrue(isElementPresentAndDisplayed(inputQuickFind, 10), "CreateButton not present on page");
        hoverOverAndClick(inputQuickFind);
        type(objectName);
        checkTrue(isElementPresentAndDisplayed(titleObjectManager, 5), objectName + " not found");
        if (isElementPresentAndDisplayed(loaderSpinner, 5)) {
            isElementDisappeared(loaderSpinner, 5);
        }
        checkEquals(getText(linkObjectsListFirstElement), objectName, objectName + " not found");
        ObjectManagerPage objectManagerPage = new ObjectManagerPage(driver);
        objectManagerPage.isOpened();
        return objectManagerPage;
    }

    @Step("Delete object")
    public ObjectManagerPage deleteObject(String objectName) {
        findObject(objectName);
        checkTrue(isElementPresentAndDisplayed(linkObjectsListFirstElement, 5), "No such object found");
        click(linkObjectsListFirstElement);
        click(btnDelete);
        click(btnDeleteInPopup);
        ObjectManagerPage objectManagerPage = new ObjectManagerPage(driver);
        objectManagerPage.isOpened();
        return objectManagerPage;
    }
}
