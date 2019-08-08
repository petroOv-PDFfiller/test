package pages.salesforce.app.sf_setup.custom_object.popups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.SalesforceBasePage;

import static core.check.Check.checkTrue;

public abstract class SchemaBuilderPopupBase extends SalesforceBasePage {

    private By popupFieldEditor = By.id("fieldEditorOverlay");

    public SchemaBuilderPopupBase(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(popupFieldEditor, 10), "create objectPopup is not opened");
    }

    @Step
    public void saveObject() {
        By btnSave = By.id("FieldSaveBtn");

        checkTrue(isElementPresentAndDisplayed(btnSave, 5), "Save button is not present");
        click(btnSave);
        checkTrue(isElementNotDisplayed(popupFieldEditor, 10), "Object create popup is still displayed");
    }
}
