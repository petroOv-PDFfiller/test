package pages.airslate.addons;

import data.airslate.SalesforceAddonsConfig;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

import static core.check.Check.checkTrue;

public abstract class SalesforceBaseAddonsPage extends AddonsBasePage {
    protected String addonName;
    private By title;
    private By btnSelectSFConnect = By.xpath("//button[.='Connect']");
    private By btnSFAccount = By.xpath("//h5[.='Salesforce account']");
    private By selectOption = By.xpath("//*[contains(text(),'Select an option')]");
    private By selectInput = By.xpath("//input[@placeholder='Search']");
    private By btnMatchAnotherField = By.xpath("//button[.='Match another field']");
    private By btnMapAnotherField = By.xpath("//button[.='Map another field']");
    private By btnDefineAnotherTransfer = By.xpath("//button[.='Define another transfer']");
    private By settingsLoader = By.xpath("//div[@class='sl-loader']");

    public SalesforceBaseAddonsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        title = By.xpath("//h5[.='" + addonName + "']");
        checkTrue(waitUntilPageLoaded(), "Page [" + addonName + "] is not loaded");
        checkTrue(isElementPresent(title, 10), "Page [" + addonName + "] is not present");
    }

    @Step
    public SalesforceBaseAddonsPage selectConnectionToSf() {
        checkTrue(isElementPresent(btnSelectSFConnect, 5), "Btn [Connect to SF] is not present");
        click(btnSelectSFConnect);
        checkTrue(isElementDisappeared(settingsLoader, 30), "Settings loader isn't disappeared");
        checkTrue(isElementPresent(btnSFAccount, 10), "[SF Account] is not present");
        click(btnSFAccount);
        checkTrue(isElementDisappeared(settingsLoader, 30), "Settings loader isn't disappeared");
        isOpened();
        return this;
    }

    @Step
    public SalesforceBaseAddonsPage selectObjectValue(String... values) {
        for (String value : values) {
            selectOption(value);
            checkTrue(isElementDisappeared(settingsLoader, 30), "Settings loader isn't disappeared");
        }
        isOpened();
        return this;
    }

    @Step
    public SalesforceBaseAddonsPage setPairs(SalesforceAddonsConfig addonConfig) {
        for (SalesforceAddonsConfig.FieldsPair addonFieldsPair : addonConfig.getFieldPairs()) {
            By btnAddAnotherField;

            switch (addonFieldsPair.getPairType()) {
                case TRANSFER:
                    btnAddAnotherField = btnDefineAnotherTransfer;
                    break;
                case MAPPING:
                    btnAddAnotherField = btnMapAnotherField;
                    break;
                case MATCHING:
                default:
                    btnAddAnotherField = btnMatchAnotherField;
                    break;
            }
            addAnotherFieldsPair(btnAddAnotherField, addonFieldsPair.getObjFieldName(), addonFieldsPair.getDocumentFieldName());
        }
        isOpened();
        return this;
    }

    private SalesforceBaseAddonsPage addAnotherFieldsPair(By btnAddField, String objField, String docField) {
        return clickAddAnotherField(btnAddField)
                .selectOption(objField)
                .selectOption(docField);
    }

    @Step
    private SalesforceBaseAddonsPage clickAddAnotherField(By btnAddAnotherField) {
        checkTrue(isElementPresent(btnAddAnotherField, 10), "Btn [" + btnAddAnotherField + "] is not present");
        click(btnAddAnotherField);
        isOpened();
        return this;
    }

    @Step
    private SalesforceBaseAddonsPage selectOption(String value) {
        checkTrue(isElementEnabled(selectOption, 10), "[Select option] is not enabled");
        click(selectOption);
        checkTrue(isElementEnabled(selectInput, 5), "[Select input] is not enabled");
        type(selectInput, value);

        By selectValue = By.xpath("//span[contains(text(),'" + value + "')]/ancestor::button");
        checkTrue(isElementEnabled(selectValue, 10), "[Select value] is not enabled");
        click(selectValue);
        isOpened();
        return this;
    }

    @Step
    public SalesforceBaseAddonsPage addConditions(List<SalesforceAddonsConfig.Condition> conditions) {
        for (SalesforceAddonsConfig.Condition condition : conditions) {
            addDocumentConditions().selectFields(condition.getFields()).operator(condition.getOperator());
        }
        isOpened();
        return this;
    }
}