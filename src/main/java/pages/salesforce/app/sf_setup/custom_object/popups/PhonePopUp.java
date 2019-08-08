package pages.salesforce.app.sf_setup.custom_object.popups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class PhonePopUp extends SchemaBuilderPopupBase {

    private String fieldLabel;
    private By fieldLabelLocator = By.id("masterLabel");
    private String fieldName;
    private By fieldNameLocator = By.id("developerName");

    public PhonePopUp(WebDriver driver) {
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
}
