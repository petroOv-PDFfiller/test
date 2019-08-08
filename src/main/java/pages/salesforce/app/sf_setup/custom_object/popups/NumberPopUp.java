package pages.salesforce.app.sf_setup.custom_object.popups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class NumberPopUp extends SchemaBuilderPopupBase {

    private String fieldLabel;
    private By fieldLabelLocator = By.id("masterLabel");
    private String fieldName;
    private By fieldNameLocator = By.id("developerName");
    private String length;
    private By lengthLocator = By.id("currencyDigitsToLeft");
    private String decimalPlaces;
    private By decimalPlacesLocator = By.id("currencyScale");

    public NumberPopUp(WebDriver driver) {
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
    public String getLength() {
        checkTrue(isElementPresentAndDisplayed(lengthLocator, 5), "length is not displayed");
        length = getAttribute(lengthLocator, "value");
        return length;
    }

    @Step
    public void setLength(String length) {
        checkTrue(isElementPresentAndDisplayed(lengthLocator, 5), "length is not displayed");
        type(lengthLocator, length);
        checkEquals(getLength(), length, "length is not set");
    }

    @Step
    public String getDecimalPlaces() {
        checkTrue(isElementPresentAndDisplayed(decimalPlacesLocator, 5), "decimalPlaces is not displayed");
        decimalPlaces = getAttribute(decimalPlacesLocator, "value");
        return decimalPlaces;
    }

    @Step
    public void setDecimalPlaces(String decimalPlaces) {
        checkTrue(isElementPresentAndDisplayed(decimalPlacesLocator, 5), "decimalPlaces is not displayed");
        type(decimalPlacesLocator, decimalPlaces);
        checkEquals(getDecimalPlaces(), decimalPlaces, "decimalPlaces is not set");
    }
}
