package pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsV3BasePopUp;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.DocumentsTab;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class DocumentRenamePopUp extends DaDaDocsV3BasePopUp {

    By popUpBodyText = By.xpath("//div[contains(@class, 'popup__body__')]//*[contains(@class, 'label__')]");

    public DocumentRenamePopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(popUpBody, 4), "Document rename popup is not displayed");
        checkEquals(getText(popUpHeader), "Document rename", "Wrong header for document rename popup");
        checkEquals(getText(popUpBodyText), "Enter new document name:",
                "Wrong text in body for document rename popup");
    }

    @Step
    public DocumentRenamePopUp enterNewDocumentName(String documentName) {
        By inputDocumentName = By.xpath("//div[contains(@class, 'popup__container__')]//input[@name='name']");

        checkTrue(isElementDisplayed(inputDocumentName, 2), "Input for new name is not displayed");
        type(inputDocumentName, documentName);
        isOpened();
        return this;
    }

    @Step
    public DocumentsTab renameDocument() {
        By btnRename = By.xpath("//div[contains(@class, 'popup__container__')]//button[contains(@class, 'btn__accent__')]");

        checkTrue(isElementDisplayed(btnRename, 2), "Rename button is not displayed");
        click(btnRename);
        DocumentsTab documentsTab = new DocumentsTab(driver);
        documentsTab.isOpened();
        return documentsTab;
    }
}
