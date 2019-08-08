package tests.salesforce.V3;

import api.salesforce.entities.SalesforceObject;
import api.salesforce.responses.CreateRecordResponse;
import core.DriverWinMan;
import core.DriverWindow;
import core.SessionInfo;
import data.TestData;
import imap.ImapClient;
import imap.With;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.pdffiller.editors.newJSF.NewJSFiller;
import pages.pdffiller.workflow.send_to_sign.SendToSignSuccessPage;
import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsFullAppV3;
import pages.salesforce.app.DaDaDocs.full_app.V3.PrintDocumentPage;
import pages.salesforce.app.DaDaDocs.full_app.V3.entities.Document;
import pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups.*;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.DocumentsTab;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.TemplatesTab;
import pages.salesforce.app.DaDaDocs.full_app.templates.CreateTemplateWizardPage;
import pages.salesforce.app.DaDaDocs.full_app.templates.popups.DiscardTemplateCreationPopUp;
import pages.salesforce.app.DaDaDocs.full_app.templates.tabs.TemplateAccessSettingsPage;
import pages.salesforce.app.DaDaDocs.full_app.templates.tabs.TemplateInfoPage;
import pages.salesforce.app.DaDaDocs.send_to_sign.SendToSignPage;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.accounts.AccountConcretePage;
import pages.salesforce.enums.V3.DaDaDocsV3Tabs;
import pages.salesforce.enums.V3.DocumentTabV3Actions;
import pages.salesforce.enums.V3.Tags;
import pages.salesforce.enums.V3.TemplateCreateTabs;
import tests.salesforce.SalesforceBaseTest;
import utils.StringMan;
import utils.SystemMan;
import utils.TimeMan;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static core.check.Check.checkFail;
import static core.check.Check.checkTrue;
import static org.testng.Assert.assertTrue;
import static pages.salesforce.enums.V3.DocumentTabV3Actions.LINK_TO_FILL;
import static pages.salesforce.enums.V3.DocumentTabV3Actions.*;
import static pages.salesforce.enums.V3.PrintDocumentV3Actions.BACK_TO_DOCUMENTS;
import static pages.salesforce.enums.V3.Tags.SEND_TO_SIGN;
import static pages.salesforce.enums.V3.Tags.*;
import static utils.ImapMan.getSignLinkFromS2SEmail;
import static utils.StringMan.getResponseBody;


@Feature("Document actions v3 test")
@Listeners({WebTestListener.class, ImapListener.class})
public class DocumentActionsV3Test extends SalesforceBaseTest {

    private WebDriver driver;
    private String fileToUpload = new File("src/test/resources/uploaders/Constructor.pdf").getAbsolutePath();
    private String longNameFile = new File("src/test/resources/uploaders/LongFilename-Ug1qG3u3IpC9trzy0CPtSjaW60VxZUq9rdfstqcBMzTxx3zUuRXyjqGjLWzMlXCO6djP.pdf").getAbsolutePath();
    private String dadadocsApp;
    private String recipientEmail;
    private String pdffillerPassword = TestData.defaultPassword;
    private String subject = "signature request";
    private DriverWinMan driverWinMan;
    private DriverWindow salesforceWindow;
    private String accountName = "ATAcc" + StringMan.getRandomString(5);

    @Parameters({"recipientEmail"})
    @BeforeTest
    public void setUp(@Optional("pdf_sf_recipient@support.pdffiller.com") String recipientEmail) throws IOException, URISyntaxException {
        this.recipientEmail = recipientEmail;

        Map<String, String> accountParameters = new HashMap<>();
        accountParameters.put("Name", accountName);
        CreateRecordResponse response = salesforceApi.recordsService().createRecord(SalesforceObject.ACCOUNT, accountParameters);
        String accountId = response.id;

        driver = getDriver();
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        AccountConcretePage contactConcretePage = salesAppBasePage.openRecordPageById(SalesforceObject.ACCOUNT, accountId);
        contactConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullAppV3();
        driverWinMan = new DriverWinMan(driver);
        salesforceWindow = driverWinMan.getCurrentWindow();
        dadadocsApp = driver.getCurrentUrl();
    }

    @BeforeMethod
    public void windowsSetUp() {
        driverWinMan.keepOnlyWindow(salesforceWindow);
    }

    @Story("Rename file")
    @Test
    public void renameAndDeleteFile() {
        String fileName = "name" + StringMan.getRandomString(5);

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
        DocumentDeletePopUp deletePopUp = documentsTab.selectDocument(fileName).selectAnAction(DocumentTabV3Actions.DELETE);

        List<String> documentsToDelete = deletePopUp.getListOfElementsToDelete();
        softAssert.assertEquals(documentsToDelete.size(), 1, "Wrong number of files will be deleted");
        softAssert.assertTrue(documentsToDelete.contains("- " + fileName + ";"), "Document to delete list does not contains selected file");

        documentsTab = deletePopUp.deleteDocument();
        softAssert.assertFalse(documentsTab.isDocumentPresent(fileName), "Document " + fileName + " was not deleted");
        softAssert.assertAll();
    }

    @Story("Edit document")
    @Test
    public void editDocument() {
        String fileName = "name" + StringMan.getRandomString(5);

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
        Document docBeforeEdit = documentsTab.selectDocument(fileName).getSelectedDocument();
        DaDaDocsEditor editor = documentsTab.selectAnAction(DocumentTabV3Actions.EDIT_DOCUMENT);
        editor.clickDoneButton();

        documentsTab.isOpened();
        softAssert.assertTrue(documentsTab.isDocumentPresent(fileName), "Edited document is not present");
        documentsTab.selectDocument(fileName);
        Document docAfterEdit = documentsTab.getSelectedDocument();
        softAssert.assertEquals(docAfterEdit.getTags(), docBeforeEdit.getTags(), "Tags were changed");
        softAssert.assertAll();
    }

    @Story("Create template")
    @Test
    public void createTemplatePositiveCase() {
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
        CreateTemplateWizardPage wizardPage = documentsTab.selectDocument(fileName).selectAnAction(DocumentTabV3Actions.CREATE_TEMPLATE);
        TemplateInfoPage templateInfoPage = wizardPage.openTab(TemplateCreateTabs.TEMPLATE_INFO);
        templateInfoPage.setTemplateName(templateName);
        TemplateAccessSettingsPage settingsPage = templateInfoPage.openTab(TemplateCreateTabs.ACCESS_SETTINGS);

        DaDaDocsFullAppV3 fullAppV3 = settingsPage.navigateToNextTabV3();
        TemplatesTab templatesTab = fullAppV3.openTab(DaDaDocsV3Tabs.TEMPLATES_TAB);
        softAssert.assertTrue(templatesTab.isTemplatePresent(templateName), "Template is not created");
        documentsTab = templatesTab.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        softAssert.assertTrue(documentsTab.isDocumentPresent(fileName), "Edited document is not present");
        softAssert.assertAll();
    }

    @Story("Create template")
    @Test
    public void createTemplateNegativeCase() {
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
        CreateTemplateWizardPage wizardPage = documentsTab.selectDocument(fileName).selectAnAction(DocumentTabV3Actions.CREATE_TEMPLATE);
        TemplateInfoPage templateInfoPage = wizardPage.openTab(TemplateCreateTabs.TEMPLATE_INFO);
        templateInfoPage.setTemplateName(templateName);
        DiscardTemplateCreationPopUp discardTemplateCreationPopUp = templateInfoPage.navigateToPreviousTab();

        DaDaDocsFullAppV3 fullAppV3 = discardTemplateCreationPopUp.deleteTemplateEndExit(true);
        TemplatesTab templatesTab = fullAppV3.openTab(DaDaDocsV3Tabs.TEMPLATES_TAB);
        softAssert.assertFalse(templatesTab.isTemplatePresent(templateName), "Template is not created");
        documentsTab = templatesTab.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        softAssert.assertTrue(documentsTab.isDocumentPresent(fileName), "Edited document is not present");
        softAssert.assertAll();
    }

    @Story("Check 'send to each' sent via IFrame")
    @Test(enabled = false)
    public void sendToEachSentViaIFrame() {
        String fileName = "name" + StringMan.getRandomString(5);

        checkTrue(driverWinMan.switchToNewWindow(), "new window is not created or cannot switch to it");
        DriverWindow pdffillerWindow = driverWinMan.getCurrentWindow();
        getUrl(testData.url);
        deleteAllCookies();
        String pdffillerEmail = StringMan.makeUniqueEmail(recipientEmail);
        TimeMan.sleep(10L);
        String secondPdffillerEmail = StringMan.makeUniqueEmail("pdf_sf_autotest_at@support.pdffiller.com");
        registerUser(pdffillerEmail, pdffillerPassword);
        registerUser(secondPdffillerEmail, pdffillerPassword);

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
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

        SendToSignPopUp signPopUp = documentsTab.selectDocument(fileName).selectAnAction(DocumentTabV3Actions.SEND_TO_SIGN);
        SendToSignPage sendToSignPage = signPopUp.openSend2SignSettings();

        String sendType = "SendToEach";
        sendToSignPage.clickAddAnotherRecipient();
        List<List<String>> recipients = Arrays.asList(Arrays.asList(pdffillerEmail, "name"),
                Arrays.asList(secondPdffillerEmail, "name2"));

        ImapClient imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        imap.deleteAllMessagesWithSubject(subject);
        imap.closeFolders().closeStore();
        imap = new ImapClient(secondPdffillerEmail, pdffillerPassword);
        imap.deleteAllMessagesWithSubject(subject);
        imap.closeFolders().closeStore();

        sendToSignPage.chooseSendType(sendType);
        sendToSignPage.clickAddAnotherRecipient();
        sendToSignPage.fillRecipients(recipients);
        daDaDocsFullApp = sendToSignPage.sendToSignV3();

        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "Cannot switch to window");
        imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        getUrl(getSignLinkFromS2SEmail(imap, subject));
        imap.closeFolders().closeStore();
        NewJSFiller newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        SendToSignSuccessPage sendToSignSuccessPage = new SendToSignSuccessPage(driver);
        sendToSignSuccessPage.isOpened();

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
        refreshPage();
        daDaDocsFullApp.isOpened();
        documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        String signedDocumentName = documentsTab.getGeneratedSignedDocumentNameV3(fileName, pdffillerEmail);
        assertTrue(documentsTab.isDocumentReturnedToSf(signedDocumentName),
                "signed document " + signedDocumentName + " is not displayed");
        Document signedDoc = documentsTab.selectDocument(signedDocumentName).getSelectedDocument();
        Document downloadedDocument = documentsTab.selectDocument(fileName).getSelectedDocument();

        softAssert.assertTrue(downloadedDocument.getTags().contains(SEND_TO_SIGN.getName()), "'Send to sign'tag is " +
                "not added to uploaded document");
        softAssert.assertTrue(signedDoc.getTags().contains(SEND_TO_SIGN.getName()), "'Send to sign'tag is not added" +
                " to signed document");
        softAssert.assertTrue(signedDoc.getTags().contains(SIGNED.getName()), "'Signed' tag is not added to signed " +
                "document");
        softAssert.assertFalse(signedDoc.getTags().contains(SIGN_PENDING.getName()), "'Sign pending' tag is" +
                " not deleted from signed document");

        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "Cannot switch to window");
        getUrl(testData.url);
        deleteAllCookies();
        SessionInfo.logged = false;
        autoLogin(secondPdffillerEmail, testData.url, pdffillerPassword);
        imap = new ImapClient(secondPdffillerEmail, pdffillerPassword);
        getUrl(getSignLinkFromS2SEmail(imap, subject));
        imap.closeFolders().closeStore();
        newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.declineDocument().decline();

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
        refreshPage();
        daDaDocsFullApp.isOpened();
        documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        String declinedDocumentName = documentsTab.getGeneratedSignedDocumentNameV3(fileName, secondPdffillerEmail);
        assertTrue(documentsTab.isDocumentReturnedToSf(declinedDocumentName),
                "signed document " + signedDocumentName + " is not displayed");
        Document declinedDoc = documentsTab.selectDocument(declinedDocumentName).getSelectedDocument();
        softAssert.assertTrue(declinedDoc.getTags().contains(SEND_TO_SIGN.getName()), "'Send to sign'tag is not added" +
                " to declined document");
        softAssert.assertTrue(declinedDoc.getTags().contains(DECLINED_SIGNING.getName()), "'Declined signing' tag is" +
                " not added to declined document");
        softAssert.assertFalse(declinedDoc.getTags().contains(SIGN_PENDING.getName()), "'Sign pending' tag is" +
                " not deleted from declined document");
        softAssert.assertAll();
    }

    @Story("Check 'send to group' sent via IFrame")
    @Test(enabled = false)
    public void sendToGroupSentViaIFrame() {
        String fileName = "name" + StringMan.getRandomString(5);
        String envelopeName = "Envelope" + StringMan.getRandomString(4);

        checkTrue(driverWinMan.switchToNewWindow(), "new window is not created or cannot switch to it");
        DriverWindow pdffillerWindow = driverWinMan.getCurrentWindow();
        getUrl(testData.url);
        deleteAllCookies();
        String pdffillerEmail = StringMan.makeUniqueEmail(recipientEmail);
        TimeMan.sleep(10L);
        String secondPdffillerEmail = StringMan.makeUniqueEmail("pdf_sf_autotest_at@support.pdffiller.com");
        registerUser(pdffillerEmail, pdffillerPassword);
        registerUser(secondPdffillerEmail, pdffillerPassword);

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
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

        SendToSignPopUp signPopUp = documentsTab.selectDocument(fileName).selectAnAction(DocumentTabV3Actions.SEND_TO_SIGN);
        SendToSignPage sendToSignPage = signPopUp.openSend2SignSettings();

        String sendType = "SendToGroup";
        sendToSignPage.clickAddAnotherRecipient();
        List<List<String>> recipients = Arrays.asList(Arrays.asList(pdffillerEmail, "name"),
                Arrays.asList(secondPdffillerEmail, "name2"));

        ImapClient imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        imap.deleteAllMessagesWithSubject(subject);
        imap.closeFolders().closeStore();
        imap = new ImapClient(secondPdffillerEmail, pdffillerPassword);
        imap.deleteAllMessagesWithSubject(subject);
        imap.closeFolders().closeStore();

        sendToSignPage.chooseSendType(sendType);
        sendToSignPage.fillEnvelopeName(envelopeName);
        sendToSignPage.clickAddAnotherRecipient();
        sendToSignPage.fillRecipients(recipients);
        daDaDocsFullApp = sendToSignPage.sendToSignV3();

        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "Cannot switch to window");
        imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        getUrl(getSignLinkFromS2SEmail(imap, subject));
        imap.closeFolders().closeStore();
        NewJSFiller newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        SendToSignSuccessPage sendToSignSuccessPage = new SendToSignSuccessPage(driver);
        sendToSignSuccessPage.isOpened();

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
        refreshPage();
        daDaDocsFullApp.isOpened();
        documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        Document downloadedDocument = documentsTab.selectDocument(fileName + " ").getSelectedDocument();
        softAssert.assertTrue(downloadedDocument.getTags().contains(SEND_TO_SIGN.getName()), "'Send to sign' tag is " +
                "not added to uploaded document");
        softAssert.assertTrue(downloadedDocument.getTags().contains(SIGN_PENDING.getName()), "'Sign pending' tag is " +
                "not added to uploaded document");

        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "Cannot switch to window");
        getUrl(testData.url);
        deleteAllCookies();
        SessionInfo.logged = false;
        autoLogin(secondPdffillerEmail, testData.url, pdffillerPassword);
        imap = new ImapClient(secondPdffillerEmail, pdffillerPassword);
        imap.setMessagesSearchTime(5);
        getUrl(getSignLinkFromS2SEmail(imap, subject));
        imap.closeFolders().closeStore();
        newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.declineDocument().decline();

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
        refreshPage();
        daDaDocsFullApp.isOpened();
        documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        assertTrue(documentsTab.isDocumentReturnedToSf(fileName + "_"), "Filled " +
                "document is not present");
        Document signedDocument = documentsTab.selectDocument(fileName + "_").getSelectedDocument();
        softAssert.assertTrue(signedDocument.getTags().contains(SEND_TO_SIGN.getName()), "'Send to sign' tag is not" +
                " added to signed document");
        softAssert.assertTrue(signedDocument.getTags().contains(DECLINED_SIGNING.getName()), "'Declined signing'" +
                " tag is not added to signed document");
        softAssert.assertAll();
    }

    @Story("Check accepted 's2s' sent via pop-up")
    @Test
    public void sendToSignSentViaPopUp() {
        String fileName = "name" + StringMan.getRandomString(5);

        checkTrue(driverWinMan.switchToNewWindow(), "new window is not created or cannot switch to it");
        DriverWindow pdffillerWindow = driverWinMan.getCurrentWindow();
        getUrl(testData.url);
        deleteAllCookies();
        String pdffillerEmail = recipientEmail;
        TimeMan.sleep(10L);
        String secondPdffillerEmail = "pdf_sf_autotest_at@support.pdffiller.com";

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
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
        ImapClient imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        imap.deleteAllMessagesWithSubject(subject);
        imap.closeFolders().closeStore();
        imap = new ImapClient(secondPdffillerEmail, pdffillerPassword);
        imap.deleteAllMessagesWithSubject(subject);
        imap.closeFolders().closeStore();

        SendToSignPopUp signPopUp = documentsTab.selectDocument(fileName).selectAnAction(DocumentTabV3Actions.SEND_TO_SIGN);
        signPopUp.setRecipient(1, pdffillerEmail, "name1")
                .addRecipient()
                .setRecipient(2, secondPdffillerEmail, "name2")
                .addSignMessage("", "").sendToSign(DocumentsTab.class);

        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "Cannot switch to window");
        imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        getUrl(getSignLinkFromS2SEmail(imap, subject));
        imap.closeFolders().closeStore();
        NewJSFiller newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        try {
            newJSFiller.clickLetsGoButton();
        } catch (Exception ignored) {
        }
        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        SendToSignSuccessPage sendToSignSuccessPage = new SendToSignSuccessPage(driver);
        sendToSignSuccessPage.isOpened();

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
        refreshPage();
        daDaDocsFullApp.isOpened();
        documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        Document downloadedDocument = documentsTab.selectDocumentByPartialName(fileName + " ").getSelectedDocument();
        softAssert.assertTrue(downloadedDocument.getTags().contains(SEND_TO_SIGN.getName()),
                "'Send to sign' tag is not added to uploaded document");
        softAssert.assertTrue(downloadedDocument.getTags().contains(SIGN_PENDING.getName()),
                "'Sign pending' tag is not added to uploaded document");

        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "Cannot switch to window");
        getUrl(testData.url);
        deleteAllCookies();
        imap = new ImapClient(secondPdffillerEmail, pdffillerPassword);
        imap.setMessagesSearchTime(5);
        getUrl(getSignLinkFromS2SEmail(imap, subject));
        imap.closeFolders().closeStore();
        newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        sendToSignSuccessPage = new SendToSignSuccessPage(driver);
        sendToSignSuccessPage.isOpened();

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
        refreshPage();
        daDaDocsFullApp.isOpened();
        documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        assertTrue(documentsTab.isDocumentWithPartialNameReturnedToSf(fileName + "_"), "Filled " +
                "document is not present");
        Document signedDocument = documentsTab.selectDocumentByPartialName(fileName + "_").getSelectedDocument();
        softAssert.assertTrue(signedDocument.getTags().contains(SEND_TO_SIGN.getName()),
                "'Send to sign' tag is not added to signed document");
        softAssert.assertTrue(signedDocument.getTags().contains(SIGNED.getName()),
                "'Signed signing' tag is not added to signed document");
        softAssert.assertAll();
    }

    @Story("Check declined 's2s' sent via pop-up")
    @Test
    public void sendToSignSentViaPopUpDeclined() {
        String fileName = "name" + StringMan.getRandomString(5);

        checkTrue(driverWinMan.switchToNewWindow(), "new window is not created or cannot switch to it");
        DriverWindow pdffillerWindow = driverWinMan.getCurrentWindow();
        getUrl(testData.url);
        deleteAllCookies();
        String pdffillerEmail = recipientEmail;
        String secondPdffillerEmail = "pdf_sf_autotest_at@support.pdffiller.com";

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
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
        ImapClient imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        imap.deleteAllMessagesWithSubject(subject);
        imap.closeFolders().closeStore();
        imap = new ImapClient(secondPdffillerEmail, pdffillerPassword);
        imap.deleteAllMessagesWithSubject(subject);
        imap.closeFolders().closeStore();

        SendToSignPopUp signPopUp = documentsTab.selectDocument(fileName).selectAnAction(DocumentTabV3Actions.SEND_TO_SIGN);
        signPopUp.setRecipient(1, pdffillerEmail, "name1")
                .addRecipient()
                .setRecipient(2, secondPdffillerEmail, "name2")
                .addSignMessage("", "").sendToSign(DocumentsTab.class);

        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "Cannot switch to window");
        imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        getUrl(getSignLinkFromS2SEmail(imap, subject));
        imap.closeFolders().closeStore();
        NewJSFiller newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        try {
            newJSFiller.clickLetsGoButton();
        } catch (Exception ignored) {
        }
        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        SendToSignSuccessPage sendToSignSuccessPage = new SendToSignSuccessPage(driver);
        sendToSignSuccessPage.isOpened();

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
        refreshPage();
        daDaDocsFullApp.isOpened();
        documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        Document downloadedDocument = documentsTab.selectDocumentByPartialName(fileName + " ").getSelectedDocument();
        softAssert.assertTrue(downloadedDocument.getTags().contains(SEND_TO_SIGN.getName()),
                "'Send to sign' tag is not added to uploaded document");
        softAssert.assertTrue(downloadedDocument.getTags().contains(SIGN_PENDING.getName()),
                "'Sign pending' tag is not added to uploaded document");

        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "Cannot switch to window");
        getUrl(testData.url);
        deleteAllCookies();
        imap = new ImapClient(secondPdffillerEmail, pdffillerPassword);
        imap.setMessagesSearchTime(5);
        getUrl(getSignLinkFromS2SEmail(imap, subject));
        imap.closeFolders().closeStore();
        newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.declineDocument().decline();

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
        TimeMan.sleep(5);
        refreshPage();
        daDaDocsFullApp.isOpened();
        documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        assertTrue(documentsTab.isDocumentWithPartialNameReturnedToSf(fileName + "_"), "Filled " +
                "document is not present");
        Document declinedDocument = documentsTab.selectDocumentByPartialName(fileName + "_").getSelectedDocument();
        softAssert.assertTrue(declinedDocument.getTags().contains(SEND_TO_SIGN.getName()),
                "'Send to sign' tag is not added to declined document");
        softAssert.assertTrue(declinedDocument.getTags().contains(DECLINED_SIGNING.getName()),
                "'Declined signing' tag is not added to declined document");
        softAssert.assertAll();
    }

    @Story("L2F behavior")
    @Test
    public void link2FillDocument() {
        String fileName = "name" + StringMan.getRandomString(5);

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
        DocumentsTab documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        documentsTab.uploadFile(fileToUpload);
        documentsTab.selectLastDocument();
        DocumentRenamePopUp renamePopUp = documentsTab.selectAnAction(DocumentTabV3Actions.RENAME);
        documentsTab = renamePopUp.enterNewDocumentName(fileName).renameDocument();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(documentsTab.isDocumentPresent(fileName), "Renamed document is not present");

        LinkToFillPopUp linkToFillPopUp = documentsTab.selectDocument(fileName).selectAnAction(LINK_TO_FILL);
        linkToFillPopUp.copyLink();
        documentsTab = linkToFillPopUp.closePopUp(DocumentsTab.class);
        softAssert.assertTrue(documentsTab.isDocumentWithPartialNameContainsTag(fileName + " ",
                Tags.LINK_TO_FILL), "Link to fill tag is not added");
        softAssert.assertTrue(documentsTab.isDocumentWithPartialNameContainsTag(fileName + " ",
                FILL_PENDING), "Fill pending tag is not added");

        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "Cannot switch to window");
        getUrl(getSelenoidClipboardValue());
        NewJSFiller newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to salesforce window");
        refreshPage();
        daDaDocsFullApp.isOpened();
        documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        assertTrue(documentsTab.isDocumentWithPartialNameReturnedToSf(fileName + "_"), "document is not returned");
        Document filledDocument = documentsTab.selectDocumentByPartialName(fileName + "_").getSelectedDocument();
        softAssert.assertTrue(filledDocument.getTags().contains(Tags.LINK_TO_FILL.getName()),
                "'LinkToFill' tag is not" + " added to signed document");
        softAssert.assertTrue(filledDocument.getTags().contains(Tags.FILLED.getName()),
                "'Filled'" + " tag is not added to signed document");
        softAssert.assertAll();
    }

    @Story("Document preview")
    @Test
    public void previewDocument() {
        getUrl(dadadocsApp);
        DaDaDocsFullAppV3 daDaDocsFullApp = new DaDaDocsFullAppV3(driver);
        daDaDocsFullApp.isOpened();
        DocumentsTab documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        documentsTab.uploadFile(longNameFile);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(documentsTab.isDocumentPresent("LongFilename-Ug1qG3u3IpC9trzy0CPtSjaW60VxZU" +
                "q9rdfstqcBMzTxx3zUuRXyjqGjLWzMlXCO6djP"), "Downloaded document is not present");

        PrintDocumentPage printDocumentPage = documentsTab.selectLastDocument().clickOnSelectedDocumentIcon();
        softAssert.assertTrue(printDocumentPage.isActionsChanged(), "Wrong list of action is enabled");
        softAssert.assertTrue(printDocumentPage.isBreadcrumbsFolderDisplayed(), "BreadCrumbs folder is not displayed");

        documentsTab = printDocumentPage.openPageByBreadcrumbsFolder("documents", documentsTab.getClass());
        List<String> breadcrumbs = documentsTab.initBreadcrumbs();

        softAssert.assertEquals(breadcrumbs.size(), 2, "Wrong breadcrumbs size");
        softAssert.assertTrue(breadcrumbs.contains("Account"), "Account is not present in breadcrumbs");
        softAssert.assertTrue(breadcrumbs.contains("Documents"), "Documents is not present in breadcrumbs");
        softAssert.assertAll();
    }

    @Story("Document list is displayed after preview was closed")
    @Test
    public void backFromPreviewToDocumentsTab() {
        getUrl(dadadocsApp);
        DaDaDocsFullAppV3 daDaDocsFullApp = new DaDaDocsFullAppV3(driver);
        daDaDocsFullApp.isOpened();
        DocumentsTab documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        documentsTab.uploadFile(longNameFile);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(documentsTab.isDocumentPresent("LongFilename-Ug1qG3u3IpC9trzy0CPtSjaW60VxZU" +
                "q9rdfstqcBMzTxx3zUuRXyjqGjLWzMlXCO6djP"), "Downloaded document is not present");
        PrintDocumentPage printDocumentPage = documentsTab.selectLastDocument().clickOnSelectedDocumentIcon();
        documentsTab = printDocumentPage.selectAnAction(BACK_TO_DOCUMENTS);

        List<String> breadcrumbs = documentsTab.initBreadcrumbs();
        softAssert.assertEquals(breadcrumbs.size(), 2, "Wrong breadcrumbs size");
        softAssert.assertTrue(breadcrumbs.contains("Account"), "Account is not present in breadcrumbs");
        softAssert.assertTrue(breadcrumbs.contains("Documents"), "Documents is not present in breadcrumbs");
        softAssert.assertAll();
    }

    @Story("Document list is displayed after preview was closed")
    @Test
    public void closePreviewTest() {
        getUrl(dadadocsApp);
        DaDaDocsFullAppV3 daDaDocsFullApp = new DaDaDocsFullAppV3(driver);
        daDaDocsFullApp.isOpened();
        DocumentsTab documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        documentsTab.uploadFile(longNameFile);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(documentsTab.isDocumentPresent("LongFilename-Ug1qG3u3IpC9trzy0CPtSjaW60VxZU" +
                "q9rdfstqcBMzTxx3zUuRXyjqGjLWzMlXCO6djP"), "Downloaded document is not present");
        PrintDocumentPage printDocumentPage = documentsTab.selectLastDocument().selectAnAction(PRINT);
        documentsTab = printDocumentPage.closePreview();
        List<String> breadcrumbs = documentsTab.initBreadcrumbs();

        softAssert.assertEquals(breadcrumbs.size(), 2, "Wrong breadcrumbs size");
        softAssert.assertTrue(breadcrumbs.contains("Account"), "Account is not present in breadcrumbs");
        softAssert.assertTrue(breadcrumbs.contains("Documents"), "Documents is not present in breadcrumbs");
        softAssert.assertAll();
    }

    @Story("Check Send by Email")
    @Test
    public void sendByEmail() {
        String fileName = "name" + StringMan.getRandomString(5);
        String emailSubject = "received a document via";
        String pdffillerEmail = recipientEmail;

        ImapClient imap = new ImapClient(pdffillerEmail, pdffillerPassword);
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
        imap.deleteAllMessagesWithSubject(emailSubject);
        SendByEmailPopUp sendByEmailPopUp = documentsTab.selectDocument(fileName).selectAnAction(SEND_BY_EMAIL);
        sendByEmailPopUp.setRecipient(1, pdffillerEmail, "recipient")
                .addSignMessage("", "")
                .sendByEmail();

        softAssert.assertTrue(imap.findMessages(With.subject(emailSubject)).size() > 0, "Message is not delivered");
        softAssert.assertAll();
    }

    @Story("Check share for editing")
    @Test(enabled = false)//flash editor
    public void shareForEditing() {
        String fileName = "name" + StringMan.getRandomString(5);

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
        DocumentsTab documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        documentsTab.uploadFile(fileToUpload);
        documentsTab.selectLastDocument();
        DocumentRenamePopUp renamePopUp = documentsTab.selectAnAction(DocumentTabV3Actions.RENAME);
        documentsTab = renamePopUp.enterNewDocumentName(fileName).renameDocument();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(documentsTab.isDocumentPresent(fileName), "Renamed document is not present");
        ShareForEditingPopUp shareForEditingPopUp = documentsTab.selectDocument(fileName).selectAnAction(SHARE_FOR_EDITING);
        shareForEditingPopUp.copyLink();

        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "Cannot switch to window");
        getUrl(SystemMan.getClipboardValue());
        NewJSFiller newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickDoneButton();

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to salesforce window");
        refreshPage();
        daDaDocsFullApp.isOpened();
        documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        softAssert.assertTrue(documentsTab.isDocumentPresent(fileName), "Changed document is not present");
        softAssert.assertAll();
    }

    @Story("Check share for editing")
    @Test(enabled = false)
    public void shareForEditingViaEmail() {
        String fileName = "name" + StringMan.getRandomString(5);
        String emailSubject = "has shared the document";

        checkTrue(driverWinMan.switchToNewWindow(), "new window is not created or cannot switch to it");
        DriverWindow pdffillerWindow = driverWinMan.getCurrentWindow();
        getUrl(testData.url);
        deleteAllCookies();
        String pdffillerEmail = recipientEmail;
        ImapClient imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        imap.deleteAllMessagesWithSubject(emailSubject);

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
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
        ShareForEditingPopUp shareForEditingPopUp = documentsTab.selectDocument(fileName).selectAnAction(SHARE_FOR_EDITING);
        shareForEditingPopUp.addRecipients(new String[]{pdffillerEmail})
                .addShareMessage("", "")
                .shareLinkViaEmail();

        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "Cannot switch to window");
        getUrl(getSignLinkFromS2SEmail(imap, emailSubject));
        NewJSFiller newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickDoneButton();

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to salesforce window");
        refreshPage();
        daDaDocsFullApp.isOpened();
        documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        softAssert.assertTrue(documentsTab.isDocumentPresent(fileName), "Changed document is not present");
        softAssert.assertAll();
    }

    @Story("Check documents merge")
    @Test
    public void checkMerge() {
        String fileName = "name" + StringMan.getRandomString(5);
        String secondFileName = "name" + StringMan.getRandomString(5);
        String mergeName = "merge" + StringMan.getRandomString(5);

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
        documentsTab.markDocument(fileName);
        softAssert.assertEquals(documentsTab.getNumberOfMarkedDocuments(), 1, "Wrong number of documents are marked");
        softAssert.assertTrue(documentsTab.isDocumentMarked(fileName), fileName + " is not marked");
        softAssert.assertFalse(documentsTab.isActionEnable(MERGE_DOCUMENTS), "Merge action is enabled");
        softAssert.assertTrue(documentsTab.isActionEnable(DELETE), "Delete action is not enabled");

        documentsTab.unmarkAllDocuments();
        documentsTab.uploadFile(fileToUpload);
        documentsTab.selectLastDocument();
        renamePopUp = documentsTab.selectAnAction(DocumentTabV3Actions.RENAME);
        documentsTab = renamePopUp.enterNewDocumentName(secondFileName).renameDocument();
        softAssert.assertTrue(documentsTab.isDocumentPresent(secondFileName), "Second renamed document is not present");

        documentsTab.markDocument(fileName);
        documentsTab.markDocument(secondFileName);
        MergePopUp mergePopUp = documentsTab.selectAnAction(MERGE_DOCUMENTS);
        softAssert.assertEquals(mergePopUp.getMergedDocumentName(), fileName + ", " + secondFileName, "Wrong" +
                " generated name");
        List<String> addedFiles = mergePopUp.getListOfDocumentsForMerge();

        softAssert.assertEquals(addedFiles.size(), 2, "Wrong numbers of added files");
        softAssert.assertEquals(addedFiles.get(0), "1. " + fileName, "Wrong position for first file");
        softAssert.assertEquals(addedFiles.get(1), "2. " + secondFileName, "Wrong position for second file");
        mergePopUp.setMergedFileName(mergeName).generateMergedFile();
        TimeMan.sleep(10);
        refreshPage();
        daDaDocsFullApp.isOpened();
        documentsTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        softAssert.assertTrue(documentsTab.isDocumentPresent(mergeName), "merged document is not present");
        softAssert.assertAll();
    }

    @Step
    private String getSelenoidClipboardValue() {
        String downloadLink;
        selenoidSessionId.set(((RemoteWebDriver) driver).getSessionId());
        try {
            downloadLink = new URL("http", sHub, 4444, "/clipboard/" + selenoidSessionId.get()).toString().replace(" ", "%20");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        try {
            HttpResponse httpResponse = Request.Get(downloadLink).execute().returnResponse();
            return getResponseBody(httpResponse);
        } catch (MalformedURLException e) {
            checkFail(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return downloadLink;
    }
}