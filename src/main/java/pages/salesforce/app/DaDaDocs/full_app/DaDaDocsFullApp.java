package pages.salesforce.app.DaDaDocs.full_app;

import data.salesforce.SalesforceTestData;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import pages.salesforce.SalesforceBasePage;
import pages.salesforce.app.DaDaDocs.PreviewTab;
import pages.salesforce.app.DaDaDocs.full_app.main_tabs.DocumentsPage;
import pages.salesforce.app.DaDaDocs.full_app.main_tabs.DocxTemplatePage;
import pages.salesforce.app.DaDaDocs.full_app.main_tabs.FileStatusPage;
import pages.salesforce.app.DaDaDocs.full_app.main_tabs.TemplatesPage;
import pages.salesforce.app.DaDaDocs.full_app.pop_ups.DocumentPackagesPopUp;
import pages.salesforce.app.DaDaDocs.full_app.pop_ups.RenamePopUp;
import pages.salesforce.app.DaDaDocs.full_app.pop_ups.SharePopUp;
import pages.salesforce.app.DaDaDocs.full_app.templates.CreateTemplateWizardPage;
import pages.salesforce.app.DaDaDocs.link_to_fill.LinkToFillCustomizeTab;
import pages.salesforce.app.DaDaDocs.send_to_sign.SendToSignPage;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.enums.SaleforceMyDocsTab;
import utils.Logger;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;
import static data.salesforce.SalesforceTestData.DaDaDocsAlerts.*;

public class DaDaDocsFullApp extends SalesAppBasePage {

    public RenamePopUp renamePopUp;
    public PreviewTab previewTab;
    public DocumentPackagesPopUp selectFilesTab;
    private By uploadDocumentButton = By.id("upload_button");
    private By filesInActiveTab = By.cssSelector(".dadadocs_tab.dadadocs_tab_active div[data-name]");
    private By tabLoader = By.xpath("//div[contains(@class, 'g-wrap-loading_tab')]");
    private By templateFrame = By.xpath("//div[contains(@class, 'slds-template_iframe')]");

    public DaDaDocsFullApp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "Dadadocs v2 full app is not loaded");
        if (isElementPresent(btnAppLauncher)) {
            checkTrue(isElementPresentAndDisplayed(templateFrame, 25), "Template frame is not displayed");
            checkTrue(isElementPresentAndDisplayed(iframe, 20), "DDD frame is not present");
            Logger.info("Switch to DDD full app frame");
            checkTrue(switchToFrame(iframe, 5), "Cannot switch to DDD fullApp frame");
        }
        skipGlobalLoaderInMyDaDaDocs();
        skipTabLoader();
        checkTrue(isElementPresentAndDisplayed(uploadDocumentButton, 35), "DaDaDocs full application was not loaded");
    }

    public <T extends DaDaDocsFullApp> T getActiveTab() {
        By tab;
        DaDaDocsFullApp daDaDocsFullApp = null;
        tab = By.xpath("//*[@class='dadadocs_header-tabs__item dadadocs_header-tabs__item--documents dadadocs_header-tabs__item_active']//*[@id]");
        checkTrue(isElementPresent(tab, 16), "DaDadocsFullApp is not opened");
        for (int i = 0; i < SaleforceMyDocsTab.values().length; i++) {
            if (getAttribute(tab, "id").equals(SaleforceMyDocsTab.values()[i].getName())) {
                daDaDocsFullApp = PageFactory.initElements(driver, SaleforceMyDocsTab.values()[i].getExpectedPage());
                break;
            }
        }

        return (T) daDaDocsFullApp;
    }

    @Step("Open tab: {0}")
    public <T extends DaDaDocsFullApp> T openTab(SaleforceMyDocsTab tab) {
        DaDaDocsFullApp sale = null;
        By tabMyDocs = By.xpath("//*[@id='" + tab.getName() + "']");
        checkTrue(isElementDisplayed(tabMyDocs, 16), "tab " + tab.getName() + " is not present");
        hoverOverAndClick(tabMyDocs);

        switch (tab) {
            case DOCUMENTS: {
                sale = new DocumentsPage(driver);
                break;
            }
            case TEMPLATES: {
                sale = new TemplatesPage(driver);
                break;
            }
            case DOCX_TEMPLATE: {
                sale = new DocxTemplatePage(driver);
                break;
            }
            case FILE_STATUS: {
                sale = new FileStatusPage(driver);
                break;
            }
        }
        sale.isOpened();

        return (T) sale;
    }

    @Step
    public DaDaDocsFullApp selectDadadocsItemByFullName(String itemName) {
        By dadadadocsItem = By.cssSelector(".dadadocs_tab.dadadocs_tab_active div[data-name='" + itemName + "']");

        checkTrue(isDadaDocsItemPresent(itemName), "Item with name " + itemName + " is not presented");
        scrollTo(dadadadocsItem);
        click(dadadadocsItem);
        checkTrue(isElementContainsStringInClass(dadadadocsItem, "active"), "Item was not selected");

        return this;
    }

    @Step
    public DaDaDocsFullApp selectDaDaDocsLastFile() {
        openTab(SaleforceMyDocsTab.DOCUMENTS);
        By lastFileLocator = By.xpath("//*[contains(@class,'dadadocs_document__row')][descendant::p[contains(@class,'subject')][text()='File']][1]");
        checkTrue(isElementPresent(lastFileLocator, 5), "Needful file is not presented");
        scrollTo(lastFileLocator);
        click(lastFileLocator);

        checkTrue(isElementContainsStringInClass(lastFileLocator, "active"), "Item was not selected");

        return this;
    }

    @Step
    public <T extends SalesforceBasePage> T selectAnAction(String actionName) {
        skipLoader();
        SalesforceBasePage page = null;
        By actionLocator = By.xpath("//button[contains(text(),'" + actionName + "')]");

        checkTrue(isElementDisplayed(actionLocator), actionName + " action is not presented");
        click(actionLocator);

        switch (actionName) {
            case SalesforceTestData.DocumentsActions.EDIT_DOCUMENT: {
                renamePopUp = new RenamePopUp(driver);
                page = renamePopUp;
                break;
            }
            case SalesforceTestData.DocumentsActions.CREATE_TEMPLATE: {
                page = new CreateTemplateWizardPage(driver);
                skipGlobalLoaderInMyDaDaDocs();
                break;
            }
            case SalesforceTestData.DocumentsActions.SEND_TO_SIGN: {
                page = new SendToSignPage(driver);
                skipGlobalLoaderInMyDaDaDocs();
                break;
            }
            case SalesforceTestData.DocumentsActions.LINK_TO_FILL: {
                page = new LinkToFillCustomizeTab(driver);
                skipGlobalLoaderInMyDaDaDocs();
                break;
            }
            case SalesforceTestData.DocumentsActions.SHARE: {
                page = new SharePopUp(driver);
                break;
            }
            case SalesforceTestData.DocumentsActions.PRINT_DOCUMENT: {
                previewTab = new PreviewTab(driver);
                page = previewTab;
                break;
            }
            case SalesforceTestData.DocumentsActions.RENAME: {
                renamePopUp = new RenamePopUp(driver);
                page = renamePopUp;
                break;
            }
            case SalesforceTestData.DocumentsActions.DELETE: {
                page = this;
                break;
            }
            case SalesforceTestData.DocumentsActions.DOCUMENT_PACKAGES: {
                selectFilesTab = new DocumentPackagesPopUp(driver);
                page = selectFilesTab;
                break;
            }
        }

        page.isOpened();
        return (T) page;
    }

    @Step
    public boolean isDadaDocsItemPresent(String itemName) {
        By dadadocsItem = By.cssSelector(".dadadocs_tab.dadadocs_tab_active div[data-name='" + itemName + "']");

        return isElementPresent(dadadocsItem, 10);
    }

    @Step
    public boolean waitForDadaDocsItemPresentAfterSign(String itemName) {
        By dadadocsItem = By.cssSelector(".dadadocs_tab.dadadocs_tab_active div[data-name='" + itemName + "']");
        boolean result;
        for (int i = 0; i < 6; i++) {
            result = isElementPresentAndDisplayed(dadadocsItem, 10);
            if (result)
                return true;
        }
        return false;
    }

    @Step
    public int getNumberOfItems() {
        By dadadocsItem = By.xpath("//*[@data-name]");

        return driver.findElements(dadadocsItem).size();
    }

    @Step
    public void deleteFile() {
        By deleteInscription = By.xpath("//*[@class='popup__body'][text()='Are you sure you want to delete this document?']");
        By yesButton = By.xpath("//button[text()='Yes']");
        checkTrue(isElementDisplayed(deleteInscription, 5), "Delete pop-up is not displaying");
        click(yesButton);
        checkDaDaDocsSuccessAlert(DELETED_SUCCESSFULLY);
        isOpened();
    }

    @Step
    public void deleteTemplate() {
        By deleteInscription = By.xpath("//*[@class='popup__body'][text()='Are you sure you want to delete this template?']");
        By yesButton = By.xpath("//button[text()='Yes']");
        checkTrue(isElementDisplayed(deleteInscription, 5), "Delete pop-up is not displaying");
        click(yesButton);
        checkDaDaDocsSuccessAlert(DELETED_SUCCESSFULLY_TEMPLATE);
        isOpened();
    }

    @Step
    public void uploadFile(String pathToFile) {
        By uploadInput = By.id("chatterFile");
        By alert = By.xpath("//div[@role='alert']");
        skipGlobalLoaderInMyDaDaDocs();
        skipTabLoader();
        checkTrue(isElementPresent(uploadInput, 5), "Upload input is not presented on the page");
        sendFileInput(uploadInput, pathToFile);
        skipGlobalLoaderInMyDaDaDocs();
        skipTabLoader();
        checkTrue(isElementPresentAndDisplayed(alert, 10), "Downloaded file alert is not displayed");
        checkTrue(getText(alert).contains("File was uploaded successfully."), "Wrong alert text");
    }

    @Step
    public boolean isSelectFilesModPresented() {
        By selectHeader = By.xpath("//h3[text()='Select PDF Documents For Merging']");
        return isElementDisplayed(selectHeader, 5);
    }

    @Step
    public DaDaDocsFullApp selectFileByOrder(int number) {
        By file = By.xpath("(//*[@data-name][descendant::p[text()='File']])[" + number + "]");
        checkTrue(isElementPresent(file, 5), "Needful checkbox is not presented");
        click(file);
        checkTrue(isElementContainsStringInClass(file, "active"), "Item was not selected");

        return this;
    }

    @Step
    public String getNameOfFileWIthOrderNumber(int number) {
        By file = By.xpath("(//*[@data-name][descendant::p[text()='File']])[" + number + "]");
        checkTrue(isElementPresent(file, 5), number + " file is not presented");
        return getAttribute(file, "data-name");
    }

    protected void checkDaDaDocsSuccessAlert(String action) {
        By daDaDocsAlert = By.cssSelector("div.dadadocs-alert-wrapper h4.dadadocs-alert__title");
        By daDaDocsAlertMessage = By.cssSelector("p.dadadocs-alert__description");

        checkTrue(isElementDisplayed(daDaDocsAlert, 5), "Alert wasn't appear");
        checkEquals(getText(daDaDocsAlert), SUCCESS_OPERATION_MESSAGE, "It isn`t successful alert");
        checkEquals(getText(daDaDocsAlertMessage), action, "Alert shows wrong action");
        checkTrue(isElementNotDisplayed(daDaDocsAlert, 10), "Alert wasn't disappear");
        skipLoader();
    }

    public int getNumberOfItemsInCurrentTab() {
        return driver.findElements(filesInActiveTab).size();
    }

    public void skipTabLoader() {
        checkTrue(isElementNotDisplayed(tabLoader, 15), "Tab loader is displayed");
    }
}
