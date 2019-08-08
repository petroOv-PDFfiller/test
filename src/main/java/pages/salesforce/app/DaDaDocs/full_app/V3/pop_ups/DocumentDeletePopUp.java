package pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsV3BasePopUp;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.DocumentsTab;

import java.util.ArrayList;
import java.util.List;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class DocumentDeletePopUp extends DaDaDocsV3BasePopUp {

    public DocumentDeletePopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(popUpBody, 4), "Document delete popup is not displayed");
        checkEquals(getText(popUpHeader), "Delete Document?", "Wrong header for document delete popup");
    }

    @Step
    public List<String> getListOfElementsToDelete() {
        By documentsToDelete = By.xpath("//div[contains(@class, 'popup__container__')]//*[@data-test='documentName']");

        checkTrue(getNumberOfElements(documentsToDelete) > 0, "No one document name is present");
        List<WebElement> elementList = driver.findElements(documentsToDelete);
        List<String> result = new ArrayList<>();
        elementList.forEach(element -> result.add(element.getAttribute("textContent")));
        return result;
    }

    @Step
    public DocumentsTab deleteDocument() {
        By btnDelete = By.xpath("//div[contains(@class, 'popup__container__')]//button[contains(@class, 'btn__accent__')]");

        checkTrue(isElementDisplayed(btnDelete, 2), "Delete button is not displayed");
        click(btnDelete);
        DocumentsTab documentsTab = new DocumentsTab(driver);
        documentsTab.isOpened();
        return documentsTab;
    }
}
