package pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsV3BasePopUp;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.DocumentsTab;
import utils.Logger;

import java.util.ArrayList;
import java.util.List;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class MergePopUp extends DaDaDocsV3BasePopUp {

    private By inputMergedDocumentName = By.xpath("//*[contains(@class, 'popup__container__')]//input[@name='documentName']");

    public MergePopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(popUpBody, 4), "link2fill template popup is not displayed");
        checkEquals(getText(popUpHeader), "Document merge order", "Wrong header for link2fill template popup");
    }

    @Step
    public DocumentsTab generateMergedFile() {
        By btnGenerate = By.xpath("//div[contains(@class, 'popup__container__')]//button[.='Generate']");

        Logger.info("Generate merged document");
        checkTrue(isElementPresentAndDisplayed(btnGenerate, 2), "Generate button is not displayed");
        click(btnGenerate);
        checkTrue(isElementNotDisplayed(btnGenerate, 5), "Generate button is still displayed");
        skipLoader();
        DocumentsTab documentsTab = new DocumentsTab(driver);
        documentsTab.isOpened();
        return documentsTab;
    }

    @Step
    public MergePopUp setMergedFileName(String filename) {
        checkTrue(isElementPresentAndDisplayed(inputMergedDocumentName, 2), "Field for merged document name is not displayed");
        clear(inputMergedDocumentName);
        type(inputMergedDocumentName, filename);
        return this;
    }

    @Step
    public String getMergedDocumentName() {
        checkTrue(isElementPresentAndDisplayed(inputMergedDocumentName, 2), "Field for merged document name is not displayed");
        return getAttribute(inputMergedDocumentName, "value");
    }

    @Step
    public List<String> getListOfDocumentsForMerge() {
        By documentTitle = By.xpath("//*[contains(@class, 'itemTitle__')]");

        List<WebElement> elementList = driver.findElements(documentTitle);
        List<String> result = new ArrayList<>();
        elementList.forEach(element -> {
            result.add(element.getText());
        });
        return result;
    }

    @Step
    public MergePopUp swapFirstAndSecondDocument(String firstElementName, String secondElementName) {
        By firstDocument = By.xpath("//*[contains(@class, 'itemTitle__') and contains(text(), '" + firstElementName + "')]/ancestor::div[contains(@class, 'item__')]");
        By divider = By.xpath("//*[contains(@class, 'itemTitle__') and contains(text(), '" + secondElementName + "')]/ancestor::div[contains(@class, 'item__')]");

        checkTrue(getListOfDocumentsForMerge().size() >= 2, "Cannot swap documents, count less than 2");
        checkTrue(isElementPresentAndDisplayed(firstDocument, 2), "First document is not present");
        checkTrue(isElementPresentAndDisplayed(divider, 3), "Divider is not present");

        dragAndDrop(firstDocument, divider);
        return this;
    }
}
