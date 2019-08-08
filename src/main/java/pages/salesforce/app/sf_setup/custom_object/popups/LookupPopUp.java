package pages.salesforce.app.sf_setup.custom_object.popups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class LookupPopUp extends SchemaBuilderPopupBase {

    private String fieldLabel;
    private By fieldLabelLocator = By.id("masterLabel");
    private String fieldName;
    private By fieldNameLocator = By.id("developerName");
    private String relatedTo;
    private By relatedToLocator = By.id("lookupDomainEnumOrId");
    private String childName;
    private By childNameLocator = By.id("lookupAggregateRelationshipName");
    private String relatedListLabel;
    private By relatedListLabelLocator = By.id("lookupRelationshipLabel");

    public LookupPopUp(WebDriver driver) {
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
    public String getRelatedTo() {
        checkTrue(isElementPresentAndDisplayed(relatedToLocator, 5), "startsWith is not displayed");
        relatedTo = getAttribute(relatedToLocator, "value");
        return relatedTo;
    }

    @Step
    public void setRelatedTo(String relatedTo) {
        checkTrue(isElementPresentAndDisplayed(relatedToLocator, 5), "relatedTo is not displayed");
        Select startWithSelect = new Select(driver.findElement(relatedToLocator));
        startWithSelect.selectByValue(relatedTo);
        checkEquals(getRelatedTo(), relatedTo, "relatedTo is not set");
    }

    @Step
    public String getChildName() {
        checkTrue(isElementPresentAndDisplayed(childNameLocator, 5), "childName is not displayed");
        childName = getAttribute(childNameLocator, "value");
        return childName;
    }

    @Step
    public void setChildName(String childName) {
        checkTrue(isElementPresentAndDisplayed(childNameLocator, 5), "childName is not displayed");
        type(childNameLocator, childName);
    }

    @Step
    public String getRelatedListLabel() {
        checkTrue(isElementPresentAndDisplayed(relatedListLabelLocator, 5), "relatedListLabel is not displayed");
        relatedListLabel = getAttribute(relatedListLabelLocator, "value");
        return relatedListLabel;
    }

    @Step
    public void setRelatedListLabel(String relatedListLabel) {
        checkTrue(isElementPresentAndDisplayed(relatedListLabelLocator, 5), "relatedListLabel is not displayed");
        type(relatedListLabelLocator, relatedListLabel);
        checkEquals(getRelatedListLabel(), relatedListLabel, "relatedListLabel is not set");
    }
}
