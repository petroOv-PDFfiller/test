package pages.salesforce.app.sf_setup.custom_object.popups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class PicklistPopUp extends SchemaBuilderPopupBase {

    private String fieldLabel;
    private By fieldLabelLocator = By.id("masterLabel");
    private String fieldName;
    private By fieldNameLocator = By.id("developerName");
    private String values;
    private By valuesLocator = By.id("picklistValues");

    public PicklistPopUp(WebDriver driver) {
        super(driver);
    }

    @Step
    public String getFieldLabel() {
        checkTrue(isElementPresentAndDisplayed(fieldLabelLocator, 5), "fieldLabel is not displayed");
        fieldLabel = getAttribute(fieldLabelLocator, "value");
        return fieldLabel;
    }

    @Step
    public void setFieldLabel(String fieldLabel) {
        checkTrue(isElementPresentAndDisplayed(fieldLabelLocator, 5), "fieldLabel is not displayed");
        type(fieldLabelLocator, fieldLabel);
        checkEquals(getFieldLabel(), fieldLabel, "fieldLabel is not set");
    }

    @Step
    public String getFieldName() {
        checkTrue(isElementPresentAndDisplayed(fieldNameLocator, 5), "fieldName is not displayed");
        fieldName = getAttribute(fieldNameLocator, "value");
        return fieldName;
    }

    @Step
    public void setFieldName(String fieldName) {
        checkTrue(isElementPresentAndDisplayed(fieldNameLocator, 5), "fieldName is not displayed");
        type(fieldNameLocator, fieldName);
        checkEquals(getFieldName(), fieldName, "fieldName is not set");
    }

    @Step
    public String getValues() {
        checkTrue(isElementPresentAndDisplayed(valuesLocator, 5), "relatedListLabel is not displayed");
        values = getAttribute(valuesLocator, "value");
        return values;
    }

    @Step
    public void setValues(String values) {
        checkTrue(isElementPresentAndDisplayed(valuesLocator, 5), "values is not displayed");
        type(valuesLocator, values);
        checkEquals(getValues(), values, "values is not set");
    }
}
