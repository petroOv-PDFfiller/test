package pages.salesforce.app.DaDaDocs.full_app.V3.tabs;

import io.qameta.allure.Step;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import pages.salesforce.SalesforceBasePage;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsFullAppV3;
import pages.salesforce.app.DaDaDocs.full_app.V3.PrintDocumentPage;
import pages.salesforce.app.DaDaDocs.full_app.V3.entities.Document;
import pages.salesforce.enums.V3.DocumentTabV3Actions;
import pages.salesforce.enums.V3.Tags;
import utils.Logger;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static core.check.Check.*;
import static org.awaitility.Awaitility.await;
import static pages.salesforce.enums.V3.DaDaDocsV3Tabs.DOCUMENTS_TAB;

public class DocumentsTab extends DaDaDocsFullAppV3 {

    public DocumentsTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        By firstDocument = By.xpath("//*[contains(@class, 'tableBody__')]//div[contains(@class, 'wrap__')]");

        skipLoader();
        checkEquals(getActiveTabName(), DOCUMENTS_TAB.getName(), "Document tab page is not opened");
        if (!isElementDisappeared(firstDocument, 6)) {
            Logger.info("No one document is present");
        }
    }

    @Step
    public <T extends SalesforceBasePage> T selectAnAction(DocumentTabV3Actions action) {
        SalesforceBasePage page = null;
        By actionLocator = By.xpath("//button[.='" + action.getAction() + "']");

        Logger.info("Select an action: " + action.getAction());
        checkTrue(isElementDisplayed(actionLocator), action.getAction() + " action is not presented");
        click(actionLocator);

        page = PageFactory.initElements(driver, action.getExpectedPage());
        skipLoader();
        page.isOpened();
        return (T) page;
    }

    public boolean isActionEnable(DocumentTabV3Actions action) {
        By actionLocator = By.xpath("//button[.='" + action.getAction() + "']");
        return isElementEnabled(actionLocator, 2);
    }

    @Step
    public DocumentsTab uploadFile(String pathToFile) {
        By uploadInput = By.xpath("//input[contains(@class, 'inputTypeFile__')]");
        checkTrue(isElementPresent(uploadInput, 5), "Upload input is not presented on the page");
        sendFileInput(uploadInput, pathToFile);
        checkTrue(notificationMessageContainsText("Your document was successfully uploaded"), "Wrong notification message");
        isOpened();
        return this;
    }

    public boolean isDocumentPresent(String documentName) {
        By documentFile = By.xpath("//div[contains(@class, 'record__')]//*[contains(@class, 'title__') and text() ='" + documentName + "']");
        return isElementPresent(documentFile);
    }

    public Document getSelectedDocument() {
        By selectedDocument = By.xpath("//div[contains(@class, 'record--active__')]");
        By selectedDocumentName = By.xpath("//div[contains(@class, 'record--active__')]//*[contains(@class, 'title__')]");
        By selectedDocumentDate = By.xpath("//div[contains(@class, 'record--active__')]//*[contains(@class, 'date__')]");
        By selectedDocumentTags = By.xpath("//div[contains(@class, 'record--active__')]//*[contains(@class, 'tag__')]");

        checkTrue(isElementPresent(selectedDocument, 2), "No one document is selected");
        checkTrue(isElementPresent(selectedDocumentName, 2), "Selected document name is not present");
        checkTrue(isElementPresent(selectedDocumentDate, 2), "Selected document modified date is not present");
        String name = getAttribute(selectedDocumentName, "textContent");
        Date modifiedDate = null;
        try {
            modifiedDate = dateFormat.parse(getAttribute(selectedDocumentDate, "textContent"));
        } catch (ParseException e) {
            e.printStackTrace();
            checkFail("Wrong time format");
        }
        List<WebElement> tagElements = driver.findElements(selectedDocumentTags);
        List<String> tagsList = new ArrayList<>();
        if (tagElements.size() > 0) {
            tagElements.forEach(tag -> tagsList.add(tag.getText()));
        }
        return new Document(name, tagsList, modifiedDate);
    }

    @Step
    public DocumentsTab selectDocument(String documentName) {
        By documentFile = By.xpath("//div[contains(@class, 'record__')]//*[contains(@class, 'title__') and text() ='" + documentName + "']");
        By documentActiveFile = By.xpath("//div[contains(@class, 'record--active__')]//*[contains(@class, 'title__') and text() ='" + documentName + "']");

        checkTrue(isDocumentPresent(documentName), documentName + " document is not present");
        scrollTo(documentFile);
        click(documentFile);
        checkTrue(isElementPresent(documentActiveFile), documentName + " is not selected");
        isOpened();
        return this;
    }

    @Step
    public DocumentsTab selectDocument(By documentLocator) {
        checkTrue(isElementPresent(documentLocator), " document is not present");
        scrollTo(documentLocator);
        click(documentLocator);
        return this;
    }

    @Step
    public DocumentsTab selectDocumentByPartialName(String documentPartialName) {
        By documentFile = By.xpath("//div[contains(@class, 'record__')]//*[contains(@class, 'title__')  and contains(text(), '" + documentPartialName + "')]");
        By documentActiveFile = By.xpath("//div[contains(@class, 'record--active__')]//*[contains(@class, 'title__')  and contains(text(), '" + documentPartialName + "')]");

        checkTrue(isElementPresent(documentFile), documentPartialName + " document is not present");
        scrollTo(documentFile);
        click(documentFile);
        checkTrue(isElementPresent(documentActiveFile), documentPartialName + " is not selected");
        isOpened();
        return this;
    }

    @Step
    public DocumentsTab selectLastDocument() {
        By documentFile = By.xpath("//div[contains(@class, 'record__')]");

        checkTrue(isElementPresent(documentFile), "No one available document is present");
        scrollTo(documentFile);
        click(documentFile);
        checkTrue(isElementContainsStringInAttribute(documentFile, "class", "record--active__", 5), "Last added document is not selected");
        isOpened();
        return this;
    }

    @Step
    public PrintDocumentPage clickOnSelectedDocumentIcon() {
        By documentIcon = By.xpath("//div[contains(@class, 'record--active__')]//*[contains(@class, 'preview__')]");

        checkTrue(isElementPresent(documentIcon), "No one document is selected");
        scrollTo(documentIcon);
        hoverOverAndClick(documentIcon);

        PrintDocumentPage printDocumentPage = new PrintDocumentPage(driver);
        printDocumentPage.isOpened();
        return printDocumentPage;
    }

    @Step
    public DocumentsTab markDocuments(int number) {
        if (getNumberOfDocuments() >= number) {
            for (int i = 1; i <= number; i++) {
                markDocument(i);
            }
            checkEquals(getNumberOfMarkedDocuments(), number, "wrong number of documents was marked");
        } else {
            checkFail("Number of document less than " + number);
            return null;
        }
        isOpened();
        return this;
    }

    @Step
    public void markDocument(int documentNumber) {
        By labelDocumentToMarked = By.xpath("//div[contains(@class, 'record__')][" + documentNumber + "]//label");
        click(labelDocumentToMarked);
    }

    @Step
    public void markDocument(String documentName) {
        By labelDocument = By.xpath("//*[contains(@class, 'title__') and text() ='" + documentName + "']/ancestor::div[contains(@class, 'record__')]//label");

        checkTrue(isDocumentPresent(documentName), "Document is not present");
        checkTrue(isElementPresent(labelDocument), "Document label is not present");
        click(labelDocument);
    }

    @Step
    public boolean isDocumentMarked(String documentName) {
        By document = By.xpath("//*[contains(@class, 'title__') and text() ='" + documentName + "']/ancestor::div[contains(@class, 'record__')]");

        checkTrue(isDocumentPresent(documentName), "Document is not present");
        return isElementContainsStringInClass(document, "record--active__");
    }

    @Step
    public DocumentsTab unmarkAllDocuments() {
        By btnUnmarkDocuments = By.xpath("//*[contains(@class, 'tableHeader__')]//label[contains(@class, 'checkbox__')]");

        if (getNumberOfMarkedDocuments() > 0) {
            checkTrue(isElementPresentAndDisplayed(btnUnmarkDocuments, 2), "Unmark button is not displayed");
            click(btnUnmarkDocuments);
            checkEquals(getNumberOfMarkedDocuments(), 0, "Marked documents are still exist");
        }
        isOpened();
        return this;
    }

    @Step
    public boolean isDocumentReturnedToSf(String documentName) {
        By documentFile = By.xpath("//div[contains(@class, 'record__')]//*[contains(@class, 'title__') and text() ='" + documentName + "']");
        return isDocumentFilePresent(documentFile, 20);
    }

    @Step
    public boolean isDocumentWithPartialNameReturnedToSf(String partialDocumentName) {
        By documentFile = By.xpath("//div[contains(@class, 'record__')]//*[contains(@class, 'title__')  and contains(text(), '" + partialDocumentName + "')]");
        return isDocumentFilePresent(documentFile, 25);
    }

    @Step
    public boolean isDocumentContainsTag(String documentName, Tags tag) {
        By documentFile = By.xpath("//div[contains(@class, 'record__')]//*[contains(@class, 'title__') and text() ='" + documentName + "']");
        return isDocumentTagPresent(documentFile, 20, tag);
    }

    @Step
    public boolean isDocumentWithPartialNameContainsTag(String partialDocumentName, Tags tag) {
        By documentFile = By.xpath("//div[contains(@class, 'record__')]//*[contains(@class, 'title__')  and contains(text(), '" + partialDocumentName + "')]");
        return isDocumentTagPresent(documentFile, 20, tag);
    }

    private boolean isDocumentTagPresent(By documentLocator, int seconds, Tags tag) {
        try {
            await().atMost(seconds, TimeUnit.SECONDS).until(() -> {
                refreshPage();
                new DaDaDocsFullAppV3(driver).isOpened();
                isOpened();
                selectDocument(documentLocator);
                return getSelectedDocument().getTags().contains(tag.getName());
            });
            return true;
        } catch (ConditionTimeoutException exception) {
            return false;
        }
    }

    private boolean isDocumentFilePresent(By documentLocator, int seconds) {
        try {
            await().atMost(seconds, TimeUnit.SECONDS).until(() -> {
                refreshPage();
                new DaDaDocsFullAppV3(driver).isOpened();
                isOpened();
                return isElementPresent(documentLocator);
            });
            return true;
        } catch (ConditionTimeoutException exception) {
            return false;
        }
    }

    private int getNumberOfDocuments() {
        By document = By.xpath("//div[contains(@class, 'record__')]");
        return getNumberOfElements(document);
    }

    public int getNumberOfMarkedDocuments() {
        By markedDocument = By.xpath("//div[contains(@class, 'record--active__')]");
        return getNumberOfElements(markedDocument);
    }

    @Step
    public DocumentsTab uploadFileToS3(String fileToUpload) {
        By uploadDocumentSwitcher = By.xpath("//*[contains(@class, 'uploadDocumentDropDown__')]");
        By s3Option = By.xpath("//*[@data-test='listItem' and .='Amazon S3']");

        checkTrue(isElementPresentAndDisplayed(uploadDocumentSwitcher, 4), "Document upload trigger " +
                "is not displayed");
        click(uploadDocumentSwitcher);
        checkTrue(isElementPresentAndDisplayed(s3Option, 4), "S3 option is not displayed");
        click(s3Option);
        uploadFile(fileToUpload);
        isOpened();
        return this;
    }
}
