package pages.salesforce.app.sf_setup.custom_object.popups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class NewObjectFieldsPopUp extends SchemaBuilderPopupBase {

    private String label;
    private By labelLocator = By.id("CustomEntityLabel");
    private String pluralLabel;
    private By pluralLabelLocator = By.id("CustomEntityPluralLabel");
    private String startsWith;
    private By startsWithLocator = By.id("CustomEntityStartsWith");
    private String objectName;
    private By objectNameLocator = By.id("CustomEntityDeveloperName");
    private String recordName;
    private By recordNameLocator = By.id("CustomEntityRecordName");
    private String dataType;
    private By dataTypeLocator = By.id("CustomEntityDataType");

    public NewObjectFieldsPopUp(WebDriver driver) {
        super(driver);
    }

    @Step
    public String getLabel() {
        checkTrue(isElementPresentAndDisplayed(labelLocator, 5), "Label is not displayed");
        label = getAttribute(labelLocator, "value");
        return label;
    }

    @Step
    public void setLabel(String label) {
        checkTrue(isElementPresentAndDisplayed(labelLocator, 5), "Label is not displayed");
        type(labelLocator, label);
        checkEquals(getLabel(), label, "Label is not set");
    }

    @Step
    public String getPluralLabel() {
        checkTrue(isElementPresentAndDisplayed(pluralLabelLocator, 5), "pluralLabel is not displayed");
        pluralLabel = getAttribute(pluralLabelLocator, "value");
        return pluralLabel;
    }

    @Step
    public void setPluralLabel(String pluralLabel) {
        checkTrue(isElementPresentAndDisplayed(pluralLabelLocator, 5), "plural Label is not displayed");
        type(pluralLabelLocator, pluralLabel);
        checkEquals(getPluralLabel(), pluralLabel, "plural Label is not set");
    }

    @Step
    public String getStartsWith() {
        checkTrue(isElementPresentAndDisplayed(startsWithLocator, 5), "startsWith is not displayed");
        startsWith = getAttribute(startsWithLocator, "value");
        return startsWith;
    }

    @Step
    public void setStartsWith(String startsWith) {
        checkTrue(isElementPresentAndDisplayed(startsWithLocator, 5), "startsWith is not displayed");
        Select startWithSelect = new Select(driver.findElement(startsWithLocator));
        startWithSelect.selectByValue(startsWith);
        checkEquals(getStartsWith(), startsWith, "starts With is not set");
    }

    @Step
    public String getObjectName() {
        checkTrue(isElementPresentAndDisplayed(objectNameLocator, 5), "objectName is not displayed");
        objectName = getAttribute(objectNameLocator, "value");
        return objectName;
    }

    @Step
    public void setObjectName(String objectName) {
        checkTrue(isElementPresentAndDisplayed(objectNameLocator, 5), "objectName is not displayed");
        type(objectNameLocator, objectName);
        checkEquals(getObjectName(), objectName, "objectName is not set");
    }

    @Step
    public String getRecordName() {
        checkTrue(isElementPresentAndDisplayed(recordNameLocator, 5), "recordName is not displayed");
        recordName = getAttribute(recordNameLocator, "value");
        return recordName;
    }

    @Step
    public void setRecordName(String recordName) {
        checkTrue(isElementPresentAndDisplayed(recordNameLocator, 5), "recordName is not displayed");
        Select recordNameSelect = new Select(driver.findElement(recordNameLocator));
        recordNameSelect.selectByValue(recordName);
        checkEquals(getRecordName(), recordName, "recordName is not set");
    }

    @Step
    public String getDataType() {
        checkTrue(isElementPresentAndDisplayed(dataTypeLocator, 5), "dataType is not displayed");
        dataType = getAttribute(dataTypeLocator, "value");
        return dataType;
    }

    @Step
    public void setDataType(String dataType) {
        checkTrue(isElementPresentAndDisplayed(dataTypeLocator, 5), "dataType is not displayed");
        Select dataTypeSelect = new Select(driver.findElement(dataTypeLocator));
        dataTypeSelect.selectByValue(dataType);
        checkEquals(getDataType(), dataType, "recordName is not set");
    }
}
