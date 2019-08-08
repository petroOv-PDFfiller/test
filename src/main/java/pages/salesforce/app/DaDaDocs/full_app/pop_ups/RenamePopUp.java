package pages.salesforce.app.DaDaDocs.full_app.pop_ups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.SalesforceBasePage;
import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;

import static core.check.Check.checkTrue;
import static data.salesforce.SalesforceTestData.DaDaDocsAlerts.RENAMED_SUCCESSFULLY;

public class RenamePopUp extends DaDaDocsFullApp {

    private By newNameField = By.xpath("//input[contains(@class,'popup__input-name')]");
    private By btnOK = By.xpath("//button[text()='Ok']");

    public RenamePopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(waitUntilPageLoaded(), "RenamePopUp isn`t loaded");
        skipLoader();
        checkTrue(isElementPresent(newNameField, 60), "Rename pop-up wasn't appeared");
    }

    @Step
    public void enterNewFilename(String text) {
        checkTrue(isElementDisplayed(newNameField, 5), "Rename field is not displaying");
        type(newNameField, text);
    }

    @Step
    public void clickOkButton() {
        checkTrue(isElementDisplayed(btnOK, 5), "Ok button is not displaying");
        click(btnOK);
    }

    @Step
    public <T extends SalesforceBasePage> T renameFile(String text, boolean isEditDocument) {
        SalesforceBasePage page = null;
        enterNewFilename(text);
        clickOkButton();
        if (isEditDocument) {
            page = new DaDaDocsEditor(driver);
        } else {
            page = new DaDaDocsFullApp(driver);
            checkDaDaDocsSuccessAlert(RENAMED_SUCCESSFULLY);
        }
        page.isOpened();

        return (T) page;
    }
}
