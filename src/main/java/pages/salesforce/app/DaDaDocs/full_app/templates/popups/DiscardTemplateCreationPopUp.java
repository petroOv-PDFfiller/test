package pages.salesforce.app.DaDaDocs.full_app.templates.popups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsFullAppV3;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class DiscardTemplateCreationPopUp extends SalesforceBasePopUp {

    private By popUpHeader = By.xpath("//div[contains(@class, 'popup__header__')]//*[contains(@class, 'popup__title__')]");

    public DiscardTemplateCreationPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        popUpBody = By.xpath("//div[contains(@class, 'popup__container__')]");
        checkTrue(isElementPresentAndDisplayed(popUpBody, 4), "Document delete popup is not displayed");
        checkEquals(getText(popUpHeader), "Save changes?", "Wrong header for document delete popup");
    }

    @Step
    public <T extends SalesAppBasePage> T saveTemplateAndExit(boolean isV3) {
        By btnSave = By.xpath("//div[contains(@class, 'popup__container__')]//button[contains(@class, 'btn__accent__')]");
        checkTrue(isElementDisplayed(btnSave, 2), "Save button is not displayed");
        return clickOnPopUpButton(btnSave, isV3);
    }

    @Step
    public <T extends SalesAppBasePage> T deleteTemplateEndExit(boolean isV3) {
        By btnDelete = By.xpath("//div[contains(@class, 'popup__container__')]//button[contains(@class, 'btn__secondary__')]");
        checkTrue(isElementDisplayed(btnDelete, 2), "Delete button is not displayed");
        return clickOnPopUpButton(btnDelete, isV3);
    }

    private <T extends SalesAppBasePage> T clickOnPopUpButton(By button, boolean isV3) {
        click(button);
        SalesAppBasePage page = null;
        if (isV3) {
            page = new DaDaDocsFullAppV3(driver);
        } else {
            page = new DaDaDocsFullApp(driver);
        }
        page.isOpened();
        return (T) page;
    }
}
