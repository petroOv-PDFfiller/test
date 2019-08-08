package pages.salesforce.app.sf_setup.object_manager;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class EditCustomFieldPage extends ObjectManagerPage {
    private By titleEditField = By.xpath("//*[@class='pageType' and contains(text(), 'Field']");
    private By inputQuickFind = By.id("globalQuickfind");
    private By linkFirstFieldInList = By.xpath("//*[@data-aura-class='uiVirtualDataGrid--default uiVirtualDataGrid']/tbody/tr[1]/td/a");
    private By titleFieldLabel = By.xpath("//*[@class='labelCol']/label[contains(text(),'Field Name')]/../../td[@class='dataCol col02']");
    private By linkFastActions = By.xpath("//*[@data-aura-class='uiVirtualDataGrid--default uiVirtualDataGrid']/tbody/tr[1]/*//lightning-primitive-icon");
    private By btnDeleteFastAction = By.xpath("//*[@class='slds-align-middle' and contains(text(),'Delete')]");
    private By btnDeleteInPopup = By.cssSelector("[title='delete']");
    private By btnEdit = By.cssSelector("[title='Edit']");
    private By inputLabel = By.id("MasterLabel");
    private By inputFieldName = By.id("DeveloperName");
    private By inputLength = By.id("digleft");
    private By inputDecimalPlaces = By.id("Scale");
    private By btnSave = By.xpath("(//*[@title='Save'])[1]");
    private By loaderSpinner = By.cssSelector("[class='indicator  forceInlineSpinner']");

    public EditCustomFieldPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(waitUntilPageLoaded(), "Edit field Page is not loaded");
        waitForSalesforceLoading();
        switchToDefaultContent();
        checkTrue(isElementPresent(titleEditField, 60), "Field is not opened");
    }

    @Step
    private EditCustomObjectPage searchForField(String label) {
        EditCustomObjectPage editCustomObjectPage = new EditCustomObjectPage(driver);
        editCustomObjectPage.isOpened();
        editCustomObjectPage.openListOfFields();
        checkTrue(isElementPresentAndDisplayed(inputQuickFind, 5), "Search impossible");
        hoverOverAndClick(inputQuickFind);
        type(inputQuickFind, label);
        if (isElementPresentAndDisplayed(loaderSpinner, 5)) {
            isElementDisappeared(loaderSpinner, 5);
        }
        editCustomObjectPage.isOpened();
        return editCustomObjectPage;
    }

    @Step("Open field. Parametes: label - {0}")
    public EditCustomFieldPage openCustomField(String label) {
        searchForField(label);
        click(linkFirstFieldInList);
        checkTrue(switchToFrame(iframe), "Iframe not present");
        checkEquals(getText(titleFieldLabel), label, "Incorrect field chosen for opening");
        EditCustomFieldPage editCustomFieldPage = new EditCustomFieldPage(driver);
        editCustomFieldPage.isOpened();
        return editCustomFieldPage;
    }

    @Step("Delete new field. Parametes: label - {0}")
    public EditCustomObjectPage deleteCustomField(String label) {
        openCustomField(label);
        searchForField(label);
        click(linkFastActions);
        click(btnDeleteFastAction);
        click(btnDeleteInPopup);
        EditCustomObjectPage editCustomObjectPage = new EditCustomObjectPage(driver);
        editCustomObjectPage.isOpened();
        return editCustomObjectPage;
    }

    @Step("Edit field, change name or length. Parametes: oldFieldName - {0}, newFieldName - {1}, newLabel - {2}, length - {3}, decimalPlaces - {4}")
    public EditCustomObjectPage editCustomField(String oldFieldName, String newFieldName, String newLabel, String length, String decimalPlaces) {
        openCustomField(oldFieldName);
        click(btnEdit);
        if (!isElementPresentAndDisplayed(inputLabel, 5)) {
            switchToDefaultContent();
            checkTrue(switchToFrame(iframe), "Iframe not present");
        }
        if (!newFieldName.isEmpty()) {
            setValue(inputFieldName, newFieldName);
        }
        if (!newLabel.isEmpty()) {
            setValue(inputLabel, newLabel);
        }
        if (!length.isEmpty()) {
            hoverOverAndClick(inputLength);
            type(inputLength, length);
        }
        if (!decimalPlaces.isEmpty()) {
            hoverOverAndClick(inputDecimalPlaces);
            type(inputDecimalPlaces, decimalPlaces);
        }
        click(btnSave);
        switchToDefaultContent();
        EditCustomObjectPage editCustomObjectPage = new EditCustomObjectPage(driver);
        editCustomObjectPage.isOpened();
        return editCustomObjectPage;
    }

    @Step("Set value using js code. Parameters: locator - {0}, value - {1}")
    private void setValue(By locator, String value) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement element = driver.findElement(locator);
        js.executeScript("arguments[0].setAttribute('value', '" + value + "')", element);
    }
}
