package tests.salesforce.V3;

import api.salesforce.responses.CreateRecordResponse;
import core.DriverWinMan;
import core.DriverWindow;
import core.SessionInfo;
import data.TestData;
import imap.ImapClient;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.pdffiller.editors.IDocument;
import pages.pdffiller.editors.newJSF.NewJSFiller;
import pages.pdffiller.editors.newJSF.TextContentElement;
import pages.pdffiller.editors.newJSF.ToolsJs;
import pages.pdffiller.editors.newJSF.constructor.ConstructorJSFiller;
import pages.pdffiller.editors.newJSF.constructor.enums.ConstructorTool;
import pages.pdffiller.workflow.send_to_sign.SendToSignSuccessPage;
import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.DaDaDocs.editor.constructor.ConstructorTextSaleForce;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsFullAppV3;
import pages.salesforce.app.DaDaDocs.full_app.V3.NewTemplatePage;
import pages.salesforce.app.DaDaDocs.full_app.V3.PrintTemplatePage;
import pages.salesforce.app.DaDaDocs.full_app.V3.entities.Document;
import pages.salesforce.app.DaDaDocs.full_app.V3.entities.Template;
import pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups.*;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.DocumentsTab;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.TemplatesTab;
import pages.salesforce.app.DaDaDocs.full_app.templates.CreateTemplateWizardPage;
import pages.salesforce.app.DaDaDocs.full_app.templates.popups.CancelTemplateCreationPopUp;
import pages.salesforce.app.DaDaDocs.full_app.templates.popups.DiscardTemplateCreationPopUp;
import pages.salesforce.app.DaDaDocs.full_app.templates.tabs.*;
import pages.salesforce.app.DaDaDocs.full_app.templates.tabs.x_templates.*;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.accounts.AccountConcretePage;
import pages.salesforce.app.sf_objects.accounts.AccountsPage;
import pages.salesforce.enums.V3.*;
import tests.salesforce.SalesforceBaseTest;
import utils.StringMan;
import utils.SystemMan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static api.salesforce.entities.SalesforceObject.ACCOUNT;
import static core.check.Check.checkTrue;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static pages.salesforce.enums.V3.Tags.SEND_TO_SIGN;
import static pages.salesforce.enums.V3.Tags.SIGNED;
import static pages.salesforce.enums.V3.TemplateTabV3Actions.*;
import static utils.ImapMan.getSignLinkFromS2SEmail;

@Feature("Template actions v3 test")
@Listeners({WebTestListener.class, ImapListener.class})
public class TemplateActionsV3Test extends SalesforceBaseTest {

    private final String SIGNED_TEMPLATE_NAME = "_Signed";
    private WebDriver driver;
    private String fileToUpload = new File("src/test/resources/uploaders/Constructor.pdf").getAbsolutePath();
    private String templateToUpload = new File("src/test/resources/pptx/Empty.pptx").getAbsolutePath();
    private String dadadocsApp;
    private String recipientEmail;
    private String complexTemplateName = "complexTemplate" + StringMan.getRandomString(5);
    private String complexTemplateFileName = "ctFilename" + StringMan.getRandomString(5);
    private String accountName = ACCOUNT.getUniquePrefix() + StringMan.getRandomString(5);
    private String NEW = "New";
    private String pdffillerPassword = TestData.defaultPassword;
    private String subject = "signature request";
    private DriverWinMan driverWinMan;
    private DriverWindow salesforceWindow;

    @Parameters({"recipientEmail"})
    @BeforeTest
    public void setUp(@Optional("pdf_sf_rnt@support.pdffiller.com") String recipientEmail) throws IOException, URISyntaxException {
        this.recipientEmail = recipientEmail;

        Map<String, String> accountParameters = new HashMap<>();
        accountParameters.put("Name", accountName);
        CreateRecordResponse response = salesforceApi.recordsService().createRecord(ACCOUNT, accountParameters);
        String accountId = response.id;

        driver = getDriver();
        driverWinMan = new DriverWinMan(driver);
        salesforceWindow = driverWinMan.getCurrentWindow();
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        AccountConcretePage contactConcretePage = salesAppBasePage.openRecordPageById(ACCOUNT, accountId);
        DaDaDocsFullAppV3 docsFullAppV3 = contactConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullAppV3();
        dadadocsApp = driver.getCurrentUrl();
        createAccountComplexTemplate(docsFullAppV3, complexTemplateFileName, complexTemplateName);
    }

    @BeforeMethod
    public void windowsSetUp() {
        driverWinMan.keepOnlyWindow(salesforceWindow);
    }

    @Story("Template discard creation")
    @Test
    public void discardCreation() {
        String fileName = "name" + StringMan.getRandomString(5);
        String templateName = "template" + StringMan.getRandomString(5);

        getUrl(dadadocsApp);
        DaDaDocsFullAppV3 daDaDocsFullApp = new DaDaDocsFullAppV3(driver);
        daDaDocsFullApp.isOpened();
        DocumentsTab documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        documentsTab.uploadFile(fileToUpload);
        documentsTab.selectLastDocument();
        DocumentRenamePopUp renamePopUp = documentsTab.selectAnAction(DocumentTabV3Actions.RENAME);
        documentsTab = renamePopUp.enterNewDocumentName(fileName).renameDocument();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(documentsTab.isDocumentPresent(fileName), "Renamed document is not present");
        TemplatesTab templatesTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.TEMPLATES_TAB);
        NewTemplatePage newTemplatePage = templatesTab.selectAnAction(NEW_TEMPLATE);
        softAssert.assertTrue(newTemplatePage.isActionsChanged(), "Actions in sidebar are not changed");

        templatesTab = newTemplatePage.selectAnAction(NewTemplateV3Actions.BACK_TO_TEMPLATES_LIST);
        newTemplatePage = templatesTab.selectAnAction(NEW_TEMPLATE);
        CreateTemplateWizardPage wizardPage = newTemplatePage.selectAnAction(NewTemplateV3Actions.NEW_PPTX_TEMPLATE);
        XTemplateInfoPage infoPage = wizardPage.openTab(TemplateCreateTabs.TEMPLATE_X_INFO);
        infoPage.setTemplateName(templateName);
        CancelTemplateCreationPopUp cancelTemplateCreationPopUp = infoPage.cancelTemplateCreation();
        cancelTemplateCreationPopUp.cancelCreation(true);
        templatesTab.isOpened();
        softAssert.assertFalse(templatesTab.isTemplatePresent(templateName), "Template created after cancel");

        newTemplatePage = templatesTab.selectAnAction(NEW_TEMPLATE);
        wizardPage = newTemplatePage.selectDocument(fileName).selectAnAction(NewTemplateV3Actions.CREATE_TEMPLATE);
        TemplateInfoPage templateInfoPage = wizardPage.openTab(TemplateCreateTabs.TEMPLATE_INFO);
        templateInfoPage.setTemplateName(templateName);
        DiscardTemplateCreationPopUp discardTemplateCreationPopUp = templateInfoPage.navigateToPreviousTab();
        discardTemplateCreationPopUp.deleteTemplateEndExit(true);
        templatesTab.isOpened();
        softAssert.assertFalse(templatesTab.isTemplatePresent(templateName), "Template created after delete");
        softAssert.assertAll();
    }

    @Story("Complex template creation")
    @Test
    public void createComplexTemplate() {
        String fileName = "name" + StringMan.getRandomString(5);
        String templateName = "template" + StringMan.getRandomString(5);

        getUrl(dadadocsApp);
        DaDaDocsFullAppV3 daDaDocsFullApp = new DaDaDocsFullAppV3(driver);
        daDaDocsFullApp.isOpened();
        DocumentsTab documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        documentsTab.uploadFile(fileToUpload);
        documentsTab.selectLastDocument();
        DocumentRenamePopUp renamePopUp = documentsTab.selectAnAction(DocumentTabV3Actions.RENAME);
        documentsTab = renamePopUp.enterNewDocumentName(fileName).renameDocument();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(documentsTab.isDocumentPresent(fileName), "Renamed document is not present");
        TemplatesTab templatesTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.TEMPLATES_TAB);
        NewTemplatePage newTemplatePage = templatesTab.selectAnAction(NEW_TEMPLATE);
        newTemplatePage.selectDocument(fileName).selectAnAction(NewTemplateV3Actions.CREATE_TEMPLATE);
        TemplateInfoPage infoPage = new TemplateInfoPage(driver);
        infoPage.isOpened();
        infoPage.setTemplateName(templateName);

        TemplateAccessSettingsPage accessSettingsPage = infoPage.openTab(TemplateCreateTabs.ACCESS_SETTINGS);
        accessSettingsPage.navigateToNextTabV3();
        templatesTab.isOpened();
        softAssert.assertTrue(templatesTab.isTemplatePresent(templateName), "Template is not created");

        Template template = templatesTab.selectTemplate(templateName).getSelectedTemplate();
        softAssert.assertNotEquals(template.getModifiedDate(), null, "Date is not set");

        DaDaDocsEditor editor = templatesTab.selectAnAction(GENERATE_DOCUMENT);
        editor.fitPage();
        editor.newJSFiller.initExistingElements();
        int numberOfElementsBeforeEdit = editor.newJSFiller.getAllContentElements();
        int textX1 = 65;
        int textY1 = 70;
        TextContentElement textElem = (TextContentElement) editor.newJSFiller.putContent(ToolsJs.TEXT, textX1, textY1);
        textElem.typeText("content testing");
        editor.clickDoneButton();

        daDaDocsFullApp.isOpened();
        documentsTab.isOpened();
        softAssert.assertTrue(documentsTab.isDocumentPresent(templateName + "_Filled"));
        editor = documentsTab.selectDocument(templateName + "_Filled").selectAnAction(DocumentTabV3Actions.EDIT_DOCUMENT);
        editor.newJSFiller.initExistingElements();
        int numberOfElementsAfterEdit = editor.newJSFiller.getAllContentElements();
        editor.clickDoneButton();
        softAssert.assertEquals(numberOfElementsAfterEdit, numberOfElementsBeforeEdit + 1, "Generated document" +
                " modification is not saved");
        softAssert.assertAll();
    }

    @Story("Pptx template creation")
    @Test
    public void createPptxTemplate() {
        String templateName = "template" + StringMan.getRandomString(5);

        getUrl(dadadocsApp);
        DaDaDocsFullAppV3 daDaDocsFullApp = new DaDaDocsFullAppV3(driver);
        daDaDocsFullApp.isOpened();
        TemplatesTab templatesTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.TEMPLATES_TAB);
        NewTemplatePage newTemplatePage = templatesTab.selectAnAction(NEW_TEMPLATE);
        SoftAssert softAssert = new SoftAssert();

        newTemplatePage.selectAnAction(NewTemplateV3Actions.NEW_PPTX_TEMPLATE);
        XTemplateInfoPage infoPage = new XTemplateInfoPage(driver);
        infoPage.isOpened();
        RelatedParentPage relatedParentPage = infoPage.setTemplateName(templateName)
                .selectStartingObject(ACCOUNT.getObjectName())
                .navigateToNextTab();

        RelatedChildPage relatedChildPage = relatedParentPage.navigateToNextTab();
        CopyTagsPage copyTagsPage = relatedChildPage.navigateToNextTab();
        TemplateUploadPage templateUploadPage = copyTagsPage.navigateToNextTab();
        XTemplateAccessSettingsPage accessSettingsPage = templateUploadPage.uploadTemplate(templateToUpload)
                .skipPreview()
                .navigateToNextTab();

        accessSettingsPage.navigateToNextTabV3();
        templatesTab.isOpened();
        softAssert.assertTrue(templatesTab.isTemplatePresent(templateName), "Template is not created");
        Template template = templatesTab.selectTemplate(templateName).getSelectedTemplate();
        softAssert.assertNotEquals(template.getModifiedDate(), null, "Date is not set");
        softAssert.assertAll();
    }

    @Story("LinkToFill complex template")
    @Test(enabled = false)
    public void linkToFillComplexTemplate() {
        DriverWinMan driverWinMan = new DriverWinMan(driver);
        DriverWindow salesforceWindow = driverWinMan.getCurrentWindow();
        driverWinMan.keepOnlyWindow(salesforceWindow);
        checkTrue(driverWinMan.switchToNewWindow(), "new window is not created or cannot switch to it");
        DriverWindow pdffillerWindow = driverWinMan.getCurrentWindow();

        getUrl(testData.url);
        deleteAllCookies();
        String pdffillerEmail = StringMan.makeUniqueEmail(recipientEmail);
        registerUser(pdffillerEmail, pdffillerPassword);
        autoLogin(pdffillerEmail, testData.url, pdffillerPassword);

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
        getUrl(dadadocsApp);
        DaDaDocsFullAppV3 daDaDocsFullApp = new DaDaDocsFullAppV3(driver);
        daDaDocsFullApp.isOpened();
        SoftAssert softAssert = new SoftAssert();
        TemplatesTab templatesTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.TEMPLATES_TAB);
        LinkToFillPopUp linkToFillPopUp = templatesTab.selectTemplate(complexTemplateName).selectAnAction(LINK_TO_FILL);
        linkToFillPopUp.copyLink();
        linkToFillPopUp.closePopUp(TemplatesTab.class);
        String link2fill = SystemMan.getClipboardValue();

        driverWinMan.switchToWindow(pdffillerWindow);
        getUrl(link2fill);
        NewJSFiller newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.initExistingElements();
        TextContentElement newAccountTextField = (TextContentElement) NewJSFiller.fillableFields.get(1);
        newAccountTextField.makeActive();
        newAccountTextField.typeText(accountName + NEW);
        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to salesforce window");
        refreshPage();
        daDaDocsFullApp.isOpened();
        DocumentsTab documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        assertTrue(documentsTab.isDocumentReturnedToSf(complexTemplateName + "_Filled"), "Filled document is not returned");
        Document signedDocument = documentsTab.selectDocument(complexTemplateName + "_Filled").getSelectedDocument();
        softAssert.assertTrue(signedDocument.getTags().contains(Tags.LINK_TO_FILL.getName()), "'LinkToFill' tag is not" +
                " added to signed document");
        softAssert.assertTrue(signedDocument.getTags().contains(Tags.FILLED.getName()), "'Filled'" +
                " tag is not added to signed document");

        checkTrue(driverWinMan.switchToNewWindow(), "new window is not created or cannot switch to it");
        getUrl(testData.url);
        deleteAllCookies();
        SessionInfo.logged = false;
        pdffillerEmail = StringMan.makeUniqueEmail(recipientEmail);
        registerUser(pdffillerEmail, pdffillerPassword);
        autoLogin(pdffillerEmail, testData.url, pdffillerPassword);
        getUrl(link2fill);
        newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.initExistingElements();
        newAccountTextField = (TextContentElement) NewJSFiller.fillableFields.get(1);
        newAccountTextField.makeActive();
        newAccountTextField.typeText(accountName + NEW + "2");
        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");

        daDaDocsFullApp.isOpened();
        AccountsPage accountsPage = daDaDocsFullApp.openObjectPage(ACCOUNT);
        softAssert.assertTrue(accountsPage.isObjectPresent(accountName + NEW), "First record is not created");
        softAssert.assertTrue(accountsPage.isObjectPresent(accountName + NEW + "2"), "Second record is not created");
        softAssert.assertAll();
    }

    @Story("Check accepted 's2s' CT sent via pop-up")
    @Test(priority = 1)
    public void sendToSignSentViaPopUp() {
        String createdAccountName = "CT_S2S";
        DriverWinMan driverWinMan = new DriverWinMan(driver);
        DriverWindow salesforceWindow = driverWinMan.getCurrentWindow();
        driverWinMan.keepOnlyWindow(salesforceWindow);

        checkTrue(driverWinMan.switchToNewWindow(), "new window is not created or cannot switch to it");
        DriverWindow pdffillerWindow = driverWinMan.getCurrentWindow();
        getUrl(testData.url);
        deleteAllCookies();
        String pdffillerEmail = StringMan.makeUniqueEmail(recipientEmail);

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
        getUrl(dadadocsApp);
        DaDaDocsFullAppV3 daDaDocsFullApp = new DaDaDocsFullAppV3(driver);
        daDaDocsFullApp.isOpened();
        TemplatesTab templatesTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.TEMPLATES_TAB);
        ImapClient imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        imap.deleteAllMessagesWithSubject(subject);
        imap.closeFolders().closeStore();

        SendToSignPopUp signPopUp = templatesTab.selectTemplate(complexTemplateName).selectAnAction(TemplateTabV3Actions.SEND_TO_SIGN);
        signPopUp.setRecipient(1, pdffillerEmail, "name1")
                .sendToSign(TemplatesTab.class);

        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "Cannot switch to window");
        imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        getUrl(getSignLinkFromS2SEmail(imap, subject));
        imap.closeFolders().closeStore();
        NewJSFiller newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.initExistingElements();
        TextContentElement newAccountTextField = (TextContentElement) NewJSFiller.fillableFields.get(1);
        newAccountTextField.makeActive();
        newAccountTextField.typeText(accountName + createdAccountName);
        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        SendToSignSuccessPage sendToSignSuccessPage = new SendToSignSuccessPage(driver);
        sendToSignSuccessPage.isOpened();

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
        getUrl(dadadocsApp);
        daDaDocsFullApp.isOpened();
        DocumentsTab documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        assertTrue(documentsTab.isDocumentReturnedToSf(complexTemplateName + SIGNED_TEMPLATE_NAME),
                complexTemplateName + SIGNED_TEMPLATE_NAME + " is not returned to sf");
        Document downloadedDocument = documentsTab.selectDocument(complexTemplateName + SIGNED_TEMPLATE_NAME).getSelectedDocument();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(downloadedDocument.getTags().contains(SEND_TO_SIGN.getName()), "'Send to sign' tag is " +
                "not added to s2s document");
        softAssert.assertTrue(downloadedDocument.getTags().contains(SIGNED.getName()), "'Signed' tag is " +
                "not added to s2s document");
        DocumentDeletePopUp deletePopUp = documentsTab.selectDocument(complexTemplateName + SIGNED_TEMPLATE_NAME).selectAnAction(DocumentTabV3Actions.DELETE);
        deletePopUp.deleteDocument();
        AccountsPage accountsPage = daDaDocsFullApp.openObjectPage(ACCOUNT);
        softAssert.assertTrue(accountsPage.isObjectPresent(accountName + createdAccountName), "Record is not created");
        softAssert.assertAll();
    }

    @Story("Check declined 's2s' CT sent via pop-up")
    @Test
    public void sendToSignSentViaPopUpDeclined() {
        DriverWinMan driverWinMan = new DriverWinMan(driver);
        DriverWindow salesforceWindow = driverWinMan.getCurrentWindow();
        driverWinMan.keepOnlyWindow(salesforceWindow);

        checkTrue(driverWinMan.switchToNewWindow(), "new window is not created or cannot switch to it");
        DriverWindow pdffillerWindow = driverWinMan.getCurrentWindow();
        getUrl(testData.url);
        deleteAllCookies();
        String pdffillerEmail = StringMan.makeUniqueEmail(recipientEmail);

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to sf window");
        getUrl(dadadocsApp);
        DaDaDocsFullAppV3 daDaDocsFullApp = new DaDaDocsFullAppV3(driver);
        daDaDocsFullApp.isOpened();
        TemplatesTab templatesTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.TEMPLATES_TAB);
        ImapClient imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        imap.deleteAllMessagesWithSubject(subject);
        imap.closeFolders().closeStore();

        SendToSignPopUp signPopUp = templatesTab.selectTemplate(complexTemplateName).selectAnAction(TemplateTabV3Actions.SEND_TO_SIGN);
        signPopUp.setRecipient(1, pdffillerEmail, "name1")
                .sendToSign(TemplatesTab.class);

        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "Cannot switch to window");
        imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        getUrl(getSignLinkFromS2SEmail(imap, subject));
        imap.closeFolders().closeStore();
        NewJSFiller newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.declineDocument().decline();

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
        getUrl(dadadocsApp);
        daDaDocsFullApp.isOpened();
        DocumentsTab documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        assertFalse(documentsTab.isDocumentPresent(complexTemplateName + SIGNED_TEMPLATE_NAME), "Signed template document present");
    }

    @Story("Print template")
    @Test
    public void printComplexTemplate() {
        getUrl(dadadocsApp);
        DaDaDocsFullAppV3 daDaDocsFullApp = new DaDaDocsFullAppV3(driver);
        daDaDocsFullApp.isOpened();
        TemplatesTab templatesTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.TEMPLATES_TAB);
        PrintTemplatePage printTemplatePage = templatesTab.selectTemplate(complexTemplateName).selectAnAction(PRINT);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(printTemplatePage.isActionsChanged(), "Actions are not changed");
        LinkToFillPopUp linkToFillPopUp = printTemplatePage.selectAnAction(PrintTemplateV3Actions.LINK_TO_FILL);
        printTemplatePage = linkToFillPopUp.closePopUp(PrintTemplatePage.class);
        templatesTab = printTemplatePage.selectAnAction(PrintTemplateV3Actions.BACK_TO_TEMPLATES);
        Template beforeEdit = templatesTab.selectTemplate(complexTemplateName).getSelectedTemplate();

        templatesTab.selectAnAction(EDIT_TEMPLATE);
        TemplateInfoPage infoPage = new TemplateInfoPage(driver);
        infoPage.isOpened();
        TemplateAccessSettingsPage accessSettingsPage = infoPage.openTab(TemplateCreateTabs.ACCESS_SETTINGS);
        accessSettingsPage.navigateToNextTabV3();
        templatesTab.isOpened();

        Template afterEdit = templatesTab.selectTemplate(complexTemplateName).getSelectedTemplate();
        softAssert.assertEquals(afterEdit.getTags(), beforeEdit.getTags(), "Template tags are changed");
        TemplateInfoV3PopUp infoPopUp = templatesTab.selectAnAction(TEMPLATE_INFO);
        infoPopUp.checkPopUpFields();
        softAssert.assertAll();
    }

    @Story("Rename and delete complex template")
    @Test(priority = 2)
    public void renameAndDeleteTemplate() {
        getUrl(dadadocsApp);
        refreshPage();
        DaDaDocsFullAppV3 daDaDocsFullApp = new DaDaDocsFullAppV3(driver);
        daDaDocsFullApp.isOpened();

        TemplatesTab templatesTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.TEMPLATES_TAB);
        RenameTemplatePopUp renameTemplatePopUp = templatesTab.selectTemplate(complexTemplateName).selectAnAction(RENAME);
        templatesTab = renameTemplatePopUp.setNewTemplateName(complexTemplateName + NEW).renameTemplate();
        assertTrue(templatesTab.isTemplatePresent(complexTemplateName + NEW), "Renamed template is not present");
        assertFalse(templatesTab.isTemplatePresent(complexTemplateName), "Old template is still present");

        complexTemplateName = complexTemplateName + NEW;
        SoftAssert softAssert = new SoftAssert();
        DeleteTemplatePopUp deleteTemplatePopUp = templatesTab.selectTemplate(complexTemplateName).selectAnAction(DELETE);
        List<String> templatesToDelete = deleteTemplatePopUp.getNameOfTemplates();
        softAssert.assertEquals(templatesToDelete.size(), 0, "Wrong size of templates to delete list");
        softAssert.assertEquals(templatesToDelete.get(0), complexTemplateName, "Wrong template will be deleted");
        templatesTab = deleteTemplatePopUp.deleteTemplate();
        softAssert.assertFalse(templatesTab.isTemplatePresent(complexTemplateName), "Template present after deletion");
    }

    private void createAccountComplexTemplate(DaDaDocsFullAppV3 daDaDocsFullApp, String fileName, String templateName) {
        DocumentsTab documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        documentsTab.uploadFile(fileToUpload);
        documentsTab.selectLastDocument();
        DocumentRenamePopUp renamePopUp = documentsTab.selectAnAction(DocumentTabV3Actions.RENAME);
        documentsTab = renamePopUp.enterNewDocumentName(fileName).renameDocument();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(documentsTab.isDocumentPresent(fileName), "Renamed document is not present");
        TemplatesTab templatesTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.TEMPLATES_TAB);
        NewTemplatePage newTemplatePage = templatesTab.selectAnAction(NEW_TEMPLATE);
        newTemplatePage.selectDocument(fileName).selectAnAction(NewTemplateV3Actions.CREATE_TEMPLATE);
        TemplateInfoPage infoPage = new TemplateInfoPage(driver);
        infoPage.isOpened();
        infoPage.setTemplateName(templateName);
        TemplateAddFieldsPage addFieldsPage = infoPage.navigateToNextTab();
        DaDaDocsEditor daDaDocsEditor = addFieldsPage.workWithEditor();
        IDocument<ConstructorJSFiller, ConstructorTool> document = new ConstructorJSFiller(driver, daDaDocsEditor.newJSFiller);
        ((ConstructorJSFiller) document).isOpened();
        daDaDocsEditor.fitPage();
        int pageHeight = (int) daDaDocsEditor.newJSFiller.getPageHeight(1);
        int pageWidth = (int) daDaDocsEditor.newJSFiller.getPageWidth(1);
        ConstructorTextSaleForce accountName = (ConstructorTextSaleForce) document.putContent(ConstructorTool.TEXT, pageWidth / 2, pageHeight / 13);
        String accountNameFieldName = accountName.getFieldName();
        ConstructorTextSaleForce createAccountName = (ConstructorTextSaleForce) document.putContent(ConstructorTool.TEXT, pageWidth / 2, pageHeight / 5);
        String createAccountNameFieldName = createAccountName.getFieldName();

        TemplateMapToPrefillPage prefillPage = addFieldsPage.saveTemplate();
        prefillPage.isOpened();
        String account = "Account ";
        prefillPage.addRecord(account);
        prefillPage.addField();
        prefillPage.mapField(0, "AccountName (String)", accountNameFieldName);
        prefillPage.saveRecord();
        TemplateMapToUpdatePage mapToUpdatePage = prefillPage.navigateToNextTab();
        TemplateMapToCreatePage mapToCreatePage = mapToUpdatePage.navigateToNextTab();
        mapToCreatePage.addRecord(account);
        mapToCreatePage.mapField(0, "AccountName (String)", createAccountNameFieldName);
        mapToCreatePage.saveRecord();
        TemplateAccessSettingsPage accessSettingsPage = infoPage.openTab(TemplateCreateTabs.ACCESS_SETTINGS);
        accessSettingsPage.navigateToNextTabV3();
        templatesTab = new TemplatesTab(driver);
        templatesTab.isOpened();
    }
}