package pages.salesforce.app.DaDaDocs.editor.constructor;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.pdffiller.editors.newJSF.NewJSFiller;
import pages.pdffiller.editors.newJSF.constructor.ConstructorCheck;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class ConstructorCheckSaleForce extends ConstructorCheck {

    private By selectObjectName = By.xpath("(//*[@class='jsf-settings__main']//*[@class='Select-arrow-zone'])[1]");
    private By selectSalesForceFieldName = By.xpath("(//*[@class='jsf-settings__main']//*[@class='Select-arrow-zone'])[2]");
    private By fieldName = By.xpath("//*[@class='Select Select--single Select Select--theme--flat-white Select--size--small has-value is-searchable Select--single']//descendant::*[@role='option']");

    public ConstructorCheckSaleForce(WebDriver driver) {
        super(driver);
    }

    public ConstructorCheckSaleForce(WebDriver driver, NewJSFiller jsFiller) {
        super(driver, jsFiller);
    }

    public String getFieldName() {
        checkTrue(isElementPresent(fieldName, 4), "fieldName is not present");
        return getText(fieldName);
    }

    @Step("Set field name to ({0})")
    public void setFieldName(String newValue) {
        checkTrue(isElementDisplayed(fieldName, 4), "fieldName is not present");
        type(fieldName, newValue);
        checkEquals(getFieldName(), newValue, "Filed name was not changed");
    }

    public String getObjectName() {
        return getText(selectObjectName);
    }

    public String getSalesForceFieldName() {
        return getText(selectSalesForceFieldName);
    }

    @Step("Set salesforce field name to ({0})")
    public ConstructorCheckSaleForce selectSalesForceFieldName(String name) {
        By textFieldName = By.xpath("//*[@aria-label='" + name + "']");
        checkTrue(isElementPresent(selectSalesForceFieldName, 4), "selectSalesForceFieldName is not present");
        hoverOverAndClick(selectSalesForceFieldName);
        checkTrue(isElementPresent(textFieldName, 4), "textFieldName is not present");
        hoverOverAndClick(textFieldName);

        return this;
    }
}
