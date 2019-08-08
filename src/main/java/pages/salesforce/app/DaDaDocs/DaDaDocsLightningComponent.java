package pages.salesforce.app.DaDaDocs;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import pages.salesforce.SalesforceBasePage;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsFullAppV3;
import pages.salesforce.app.DaDaDocs.full_app.V3.entities.Document;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_setup.SalesforceUploadDocumentPopUp;
import pages.salesforce.enums.FileTypes;
import pages.salesforce.enums.lightning_component.DaDaDocsLightningComponentActions;
import pages.salesforce.enums.lightning_component.DaDaDocsLightningComponentSortParameters;
import pages.salesforce.enums.lightning_component.DaDaDocsLightningComponentTabs;
import utils.Logger;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static core.check.Check.checkFail;
import static core.check.Check.checkTrue;

public class DaDaDocsLightningComponent extends SalesAppBasePage {

    private By mainLoader = By.xpath("//div[contains(@class,'g-wrap-loading')]");
    private By documentsLoader = By.xpath("//div[contains(@class, 'loaderWrapper__')]");
    private By fullSizeButton = By.xpath("//*[@aria-label='Full application']/span");

    public DaDaDocsLightningComponent(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementDisappeared(mainLoader, 25), "main loader is not disappeared");
        checkTrue(isElementDisappeared(documentsLoader, 15), "document loader is not disappeared");
        if (isElementPresent(dadadocsFrame))
            switchToFrame(dadadocsFrame);
        checkTrue(isElementPresentAndDisplayed(fullSizeButton, 20), "DaDaDocs lightning component was not loaded");
    }

    @Step
    public DaDaDocsLightningComponent selectDadadocsItemByFullName(String itemName) {
        By daDaDocsItem = By.xpath("//*[@title='" + itemName + "']");

        checkTrue(isElementPresent(daDaDocsItem, 5), "Item with name " + itemName + " is not presented");
        click(daDaDocsItem);
        By selectActionPanelOpened = By.xpath("//*[contains(text(),'" + itemName + "')]/following-sibling::*[text()='Change']");
        checkTrue(isElementPresentAndDisplayed(selectActionPanelOpened, 5), "Select action panel was not opened");

        return this;
    }

    @Step
    public boolean waitForDadaDocsItemPresentAfterSign(String itemName) {
        By daDaDocsItem = By.xpath("//*[@title='" + itemName + "']");

        boolean result;
        for (int i = 0; i < 6; i++) {
            result = isElementPresentAndDisplayed(daDaDocsItem, 10);
            if (result)
                return true;
        }
        return false;
    }

    @Step
    public SalesforceUploadDocumentPopUp uploadNewDocument(String pathToFile) {
        By inputUploadDocument = By.xpath("//*[@class = 'visible dddInput']//input[@lightning-input_input]");

        switchToDefaultContent();
        checkTrue(isElementPresent(inputUploadDocument, 15), "Document upload input is not present");
        sendFileInput(inputUploadDocument, pathToFile);
        SalesforceUploadDocumentPopUp uploadDocumentPopUp = new SalesforceUploadDocumentPopUp(driver);
        uploadDocumentPopUp.isOpened();
        return uploadDocumentPopUp;
    }

    @Step
    public <T extends SalesforceBasePage> T selectAnAction(DaDaDocsLightningComponentActions action) {
        By actionLocator = By.xpath("//*[text()='" + action.getActionName() + "']/parent::div");

        Logger.info("Select an action: " + action.name());
        checkTrue(isElementDisplayed(actionLocator), action.getActionName() + " action is not presented");
        click(actionLocator);

        SalesforceBasePage page = PageFactory.initElements(driver, action.getPage());
        if (action.equals(DaDaDocsLightningComponentActions.EDIT_DOCUMENT)) {
            try {
                isElementDisappeared(fullSizeButton, 20);
            } catch (WebDriverException e) {
                Logger.info("Iframe switch error");//temporary chrome v72 fix
            }
        }
        page.isOpened();
        return (T) page;
    }

    @Step
    public DaDaDocsFullApp openDadadocsFullApp() {
        int windowsNumber = driver.getWindowHandles().size();
        Logger.info("Opening DaDaDocs in full mode");
        checkTrue(isElementDisappeared(documentsLoader, 10), "document is not disappeared");
        checkTrue(isElementPresentAndDisplayed(fullSizeButton, 5), "Full size button is not presented");
        clickJS(fullSizeButton);
        switchToIfWindowsNumberIs(windowsNumber, "apex/ClassicSelectAttachment");
        DaDaDocsFullApp daDaDocsFullApp = new DaDaDocsFullApp(driver);
        daDaDocsFullApp.isOpened();

        return daDaDocsFullApp;
    }

    @Step
    public DaDaDocsFullAppV3 openDadadocsFullAppV3() {
        int windowsNumber = driver.getWindowHandles().size();
        Logger.info("Opening DaDaDocs in full mode");
        checkTrue(isElementDisappeared(documentsLoader, 10), "document is not disappeared");
        checkTrue(isElementPresentAndDisplayed(fullSizeButton, 5), "Full size button is not presented");
        clickJS(fullSizeButton);
        switchToIfWindowsNumberIs(windowsNumber, "apex/classicv3");
        DaDaDocsFullAppV3 daDaDocsFullApp = new DaDaDocsFullAppV3(driver);
        daDaDocsFullApp.isOpened();

        return daDaDocsFullApp;
    }

    @Step
    public DaDaDocsLightningComponent setRecipient(String email, String name, int recipientNumber) {
        By inputEmail = By.xpath("(//*[contains(@class, 'contactWrap__')])[" + recipientNumber + "]/input[@type='email']");
        By inputName = By.xpath("(//*[contains(@class, 'contactWrap__')])[" + recipientNumber + "]/input[@type='text']");

        Logger.info(String.format("Set recipient %s (name: %s with email: %S)", recipientNumber, name, email));
        checkTrue(isElementPresentAndDisplayed(inputEmail, 4), recipientNumber + " recipient email input" +
                " is not displayed");
        type(inputEmail, email);
        checkTrue(isElementPresentAndDisplayed(inputName, 4), recipientNumber + " recipient name input" +
                " is not displayed");
        type(inputName, name);
        return this;
    }

    @Step
    public DaDaDocsLightningComponent sendToSign() {
        By btnSendToSign = By.xpath("//*[contains(@class, 's2sBtnWrap__')]/button");

        Logger.info("Send to sign document");
        checkTrue(isElementPresentAndDisplayed(btnSendToSign, 5), "SendToSign button is not displayed");
        click(btnSendToSign);
        isOpened();
        return this;
    }

    @Step
    public String getLastDocumentName() {
        By document = By.xpath("//div[contains(@class, 'fileItem__')]");

        checkTrue(isElementPresentAndDisplayed(document, 5), "No one document is present");
        return getAttribute(document, "title");
    }

    @Step
    public DaDaDocsLightningComponent showInLayout(DaDaDocsLightningComponentTabs tab) {
        By btnToggleTab = By.xpath("//*[contains(@class, 'toggleBtn__')]");
        By tabRow = By.xpath("//*[contains(@class, 'modalLink__') and .='" + tab.getName() + "']");

        checkTrue(isElementPresentAndDisplayed(btnToggleTab, 4), "Toggle logo is not displayed");
        hoverOverAndClick(btnToggleTab);
        checkTrue(isElementPresentAndDisplayed(tabRow, 10), tab.getName() + " tab option is not displayed");
        click(tabRow);
        isOpened();
        return this;
    }

    @Step
    public DaDaDocsLightningComponent filterDocumentsByFileType(FileTypes type) {
        By typeFilterTrigger = By.xpath("//*[@data-toggleid='1']");
        By typeFilterOption = By.xpath("//*[contains(@class, 'modalLink__') and .='" + type.name() + "']");

        Logger.info("Filter document by type( " + type.name() + " )");
        checkTrue(isElementPresentAndDisplayed(typeFilterTrigger, 5), "File type filter trigger is not displayed");
        click(typeFilterTrigger);
        checkTrue(isElementPresentAndDisplayed(typeFilterOption, 5), type.name() + " filter option is not displayed");
        if (isElementEnabled(typeFilterOption)) {
            click(typeFilterOption);
        } else {
            Logger.info(type.name() + " filter is disabled");
        }
        return this;
    }

    @Step
    public List<Document> initDocuments() {
        List<Document> documents = new ArrayList<>();
        By docDocument = By.xpath(".//*[name()='svg']//*[name()='path' and @fill='#345893']");
        By pptDocument = By.xpath(".//*[name()='svg']//*[name()='path' and @fill='#C75736']");
        By documentFile = By.xpath("//div[contains(@class, 'fileItem__')]");

        Logger.info("Init all visible documents");
        List<WebElement> documentElements = driver.findElements(documentFile);
        documentElements.forEach(element -> {
            Document currentDocument = getDocument(element);
            if (element.findElements(pptDocument).size() > 0) {
                currentDocument.setType(FileTypes.PPT);
            } else if (element.findElements(docDocument).size() > 0) {
                currentDocument.setType(FileTypes.DOC);
            }
            documents.add(currentDocument);
        });
        return documents;
    }

    @Step
    public Document getDocument(WebElement documentElement) {
        By title = By.xpath(".//*[contains(@class, 'title')]");
        By date = By.xpath(".//time");

        checkTrue(documentElement.findElements(title).size() > 0, "Document title is not present");
        checkTrue(documentElement.findElements(date).size() > 0, "Date is not present");
        String documentName = documentElement.findElements(title).get(0).getText();
        String dateField = documentElement.findElements(date).get(0).getText();
        Date documentDate = null;
        try {
            documentDate = Document.dateFormat.parse(dateField.substring(0, dateField.indexOf(" |")));
        } catch (ParseException e) {
            checkFail("Cannot parse date");
        }
        return new Document(documentName, new ArrayList<>(), documentDate);
    }

    @Step
    public DaDaDocsLightningComponent sortBy(DaDaDocsLightningComponentSortParameters parameter) {
        By sortTrigger = By.xpath("//*[@data-toggleid='2']");
        By sortOption = By.xpath("//*[contains(@class, 'modalLink__') and .='" + parameter.name() + "']");
        By header = By.xpath("//*[contains(@class, 'title__')]");

        Logger.info("Sort document by: " + parameter.name());
        checkTrue(isElementPresentAndDisplayed(sortTrigger, 5), "Sort trigger is not displayed");
        click(sortTrigger);
        checkTrue(isElementPresentAndDisplayed(sortOption, 10), parameter.name() + " sort option is" +
                " not displayed");
        click(sortOption);
        checkTrue(isElementPresentAndDisplayed(header, 2), "header is displayed");
        click(header);
        return this;
    }

    @Step
    public DaDaDocsLightningComponent mergeTwoFirstDocuments() {
        By btnAdditionalAction = By.xpath("//*[@data-toggleid='3']");
        By btnMergeDocumentsOption = By.xpath("//*[contains(@class, 'modalLink__') and .=' Merge Documents']");
        By firstDocument = By.xpath("(//*[contains(@class, 'fileItem__')])[1]");
        By secondDocument = By.xpath("(//*[contains(@class, 'fileItem__')])[2]");
        By btnMergeSelected = By.xpath("//button[contains(., 'Merge Selected')]");

        checkTrue(isElementPresentAndDisplayed(btnAdditionalAction, 4), "Additional actions button is " +
                "not displayed");
        click(btnAdditionalAction);
        checkTrue(isElementPresentAndDisplayed(btnMergeDocumentsOption, 4), "Merge document options is " +
                "not displayed");
        click(btnMergeDocumentsOption);
        selectDocument(firstDocument);
        selectDocument(secondDocument);
        checkTrue(isElementPresentAndDisplayed(btnMergeSelected, 2), "Merge selected documents button" +
                " is not displayed");
        checkTrue(isElementEnabled(btnMergeSelected, 2), "Merge selected documents button is not enabled");
        click(btnMergeSelected);
        isOpened();
        return this;
    }

    @Step
    private DaDaDocsLightningComponent selectDocument(By document) {
        checkTrue(isElementPresentAndDisplayed(document, 2), "Document is not displayed");
        click(document);
        isOpened();
        return this;
    }
}
