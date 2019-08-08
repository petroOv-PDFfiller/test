package tests.salesforce.process_builder;

import core.Browser;
import data.TestData;
import data.salesforce.SalesforceTestData;
import imap.ImapClient;
import imap.MessageContent;
import imap.With;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import pages.pdffiller.editors.IDocument;
import pages.pdffiller.editors.newJSF.NewJSFiller;
import pages.pdffiller.editors.newJSF.TextContentElement;
import pages.pdffiller.editors.newJSF.constructor.ConstructorJSFiller;
import pages.pdffiller.editors.newJSF.constructor.enums.ConstructorTool;
import pages.pdffiller.workflow.send_to_sign.SendToSignSuccessPage;
import pages.salesforce.app.AppLauncherPopUp;
import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.DaDaDocs.editor.constructor.ConstructorTextSaleForce;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.DaDaDocs.full_app.main_tabs.TemplatesPage;
import pages.salesforce.app.DaDaDocs.full_app.templates.CreateTemplateWizardPage;
import pages.salesforce.app.DaDaDocs.full_app.templates.entities.Record;
import pages.salesforce.app.DaDaDocs.full_app.templates.tabs.*;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.SalesAppHomePage;
import pages.salesforce.app.sf_objects.contacts.ContactConcretePage;
import pages.salesforce.app.sf_objects.contacts.ContactsPage;
import pages.salesforce.app.sf_setup.SetupSalesforcePage;
import pages.salesforce.app.sf_setup.process_builder.ProcessBuilderEditorPage;
import pages.salesforce.app.sf_setup.process_builder.ProcessBuilderPage;
import pages.salesforce.app.sf_setup.process_builder.popups.NewProcessPopUp;
import pages.salesforce.app.sf_setup.process_builder.popups.SelectFieldPopUp;
import pages.salesforce.enums.SaleforceMyDocsTab;
import pages.salesforce.enums.SalesTab;
import tests.salesforce.SalesforceBaseTest;
import utils.Logger;
import utils.StringMan;

import javax.mail.Message;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static api.salesforce.entities.SalesforceObject.CONTACT;
import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;
import static data.salesforce.SalesforceTestData.DocumentsActions.DELETE;
import static data.salesforce.SalesforceTestData.SalesforceApexClasses.AUTOMATED_TEMPLATES_SENDER;
import static data.salesforce.SalesforceTestData.SalesforceApexVariables.FIELD_REFERENCE;
import static data.salesforce.SalesforceTestData.SalesforceProcessBuilderActionTypes.APEX;
import static data.salesforce.SalesforceTestData.TemplatesActions.DELETE_TEMPLATE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static pages.salesforce.enums.V3.TemplateCreateTabs.TEMPLATE_INFO;

@Feature("Process builder")
@Listeners({WebTestListener.class, ImapListener.class})
public class ProcessBuilderTest extends SalesforceBaseTest {

    private WebDriver driver;
    private DaDaDocsFullApp daDaDocsFullApp;
    private String templateName = "name" + StringMan.getRandomString(4);
    private SalesAppHomePage salesAppHomePage;
    private String fileToUpload = new File("src/test/resources/uploaders/Constructor.pdf").getAbsolutePath();
    private String homePageURL;
    private String processName = "process" + StringMan.getRandomString(5);
    private ProcessBuilderPage processBuilderPage;
    private SalesAppBasePage salesAppBasePage;
    private String templateID;
    private String adminEmail;
    private String adminPassword = TestData.defaultPassword;
    private String recipientEmail;

    @Parameters({"recipientEmail"})
    @BeforeTest
    public void setUp(@Optional("pdf_sf_recipient@support.pdffiller.com") String recipientEmail) throws IOException, URISyntaxException {
        if (!dxOwnerEmail.isEmpty()) {
            adminEmail = dxOwnerEmail;
        } else {
            adminEmail = salesforceDefaultLogin;
        }
        this.recipientEmail = recipientEmail;
        driver = getDriver();
        salesAppBasePage = loginWithDefaultCredentials();
        homePageURL = driver.getCurrentUrl();
        AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
        salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        ContactsPage contactsPage = salesAppBasePage.openTab(SalesTab.CONTACTS);
        ContactConcretePage contactConcretePage = contactsPage.openObjectByNumber(1);
        daDaDocsFullApp = contactConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullApp();
        daDaDocsFullApp.uploadFile(fileToUpload);
        daDaDocsFullApp.selectDaDaDocsLastFile();

        CreateTemplateWizardPage template = daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.CREATE_TEMPLATE);
        TemplateInfoPage infoPage = template.openTab(TEMPLATE_INFO);
        infoPage.setTemplateName(templateName);
        TemplateAddFieldsPage addFieldsPage = infoPage.navigateToNextTab();
        DaDaDocsEditor daDaDocsEditor = addFieldsPage.workWithEditor();
        IDocument<ConstructorJSFiller, ConstructorTool> document = new ConstructorJSFiller(driver, daDaDocsEditor.newJSFiller);
        ((ConstructorJSFiller) document).isOpened();
        daDaDocsEditor.fitPage();
        int pageHeight = (int) daDaDocsEditor.newJSFiller.getPageHeight(1);
        int pageWidth = (int) daDaDocsEditor.newJSFiller.getPageWidth(1);
        ConstructorTextSaleForce contactName = (ConstructorTextSaleForce) document.putContent(ConstructorTool.TEXT, pageWidth / 4, pageHeight / 13);
        String contactNameFieldName = contactName.getFieldName();
        TemplateMapToPrefillPage prefillPage = addFieldsPage.saveTemplate();
        prefillPage.isOpened();
        prefillPage.addRecord(CONTACT.getObjectName());
        prefillPage.addField();
        prefillPage.mapField(0, "FirstName (String)", contactNameFieldName);
        prefillPage.saveRecord();
        List<Record> prefillMapping = prefillPage.initRecords();
        TemplateMapToUpdatePage mapToUpdatePage = prefillPage.navigateToNextTab();
        mapToUpdatePage.addRecord(CONTACT.getObjectName());
        mapToUpdatePage.addField();
        mapToUpdatePage.mapField(0, "FirstName (String)", contactNameFieldName);
        mapToUpdatePage.saveRecord();
        TemplateMapToCreatePage mapToCreatePage = mapToUpdatePage.navigateToNextTab();
        TemplateAccessSettingsPage accessSettingsPage = mapToCreatePage.navigateToNextTab();
        accessSettingsPage.navigateToNextTab();
        TemplatesPage templatesPage = daDaDocsFullApp.openTab(SaleforceMyDocsTab.TEMPLATES);
        assertTrue(daDaDocsFullApp.isDadaDocsItemPresent(templateName), "Complex template is not present");
        templatesPage.selectDadadocsItemByFullName(templateName);
        templateID = templatesPage.getTemplateID(templateName);
        salesAppHomePage.isOpened();

    }

    @Test
    @Story("Complex template contact process builder")
    public void sendComplexTemplateForContact() {
        getUrl(homePageURL);
        salesAppBasePage.isOpened();
        SetupSalesforcePage setupSalesforcePage = salesAppBasePage.openSetupPage();
        processBuilderPage = setupSalesforcePage.openProcessBuilder();
        NewProcessPopUp processPopUp = processBuilderPage.addNewProcess();
        ProcessBuilderEditorPage processBuilderEditorPage = processPopUp.setName(processName).setRecordChangesAsProcessTrigger().saveProcess();
        processBuilderEditorPage.addObject(CONTACT.getObjectName()).setProcessStartAlways().saveObject();
        processBuilderEditorPage.setCriteriaWithoutAction("criteria").saveCriteria();
        SelectFieldPopUp selectFieldPopUp = processBuilderEditorPage.addAction(APEX, "action", AUTOMATED_TEMPLATES_SENDER)
                .setAutomatedTemplateSenderVariables(recipientEmail, FIELD_REFERENCE, templateID);
        processBuilderEditorPage = selectFieldPopUp.setContactId().chooseField();
        processBuilderEditorPage.addRecipientField("test").saveAction();
        processBuilderEditorPage.activateProcess();
        getUrl(homePageURL);
        SalesAppBasePage salesAppBasePage = new SalesAppBasePage(driver);
        AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
        salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        ContactsPage contactsPage = salesAppBasePage.openTab(SalesTab.CONTACTS);
        String oldName = contactsPage.getContactFirstName(1);
        ImapClient imap = new ImapClient(recipientEmail, adminPassword);
        imap.deleteAllMessagesWithSubject("signature request");
        imap.setMessagesSearchTime(5);
        List<Message> messages = imap.findMessages(With.subject("signature request"));

        checkTrue(messages.size() > 0, "No messages in mailbox");
        MessageContent messageContent = new MessageContent(imap, messages.get(0));
        List<String> urls = messageContent.getLinkUrls();
        String urlToSign = urls.get(1);
        checkTrue(urlToSign != null, "No needful link in message");
        WebDriver pdfFillerDriver = getDriver(Browser.CHROME, false);
        setActiveDriver(pdfFillerDriver);
        getUrl(urlToSign);
        NewJSFiller newJSFiller = new NewJSFiller(pdfFillerDriver);

        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.initExistingElements();

        TextContentElement firstNameTextField = (TextContentElement) NewJSFiller.fillableFields.get(0);
        firstNameTextField.makeActive();
        firstNameTextField.fetchText();
        assertEquals(firstNameTextField.text, oldName, "wrong name");
        int firstNameLength = oldName.length();
        for (int i = 0; i < firstNameLength; i++) {
            firstNameTextField.deleteContentFromField();
        }
        firstNameTextField.typeText(oldName);
        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        SendToSignSuccessPage sendToSignSuccessPage = new SendToSignSuccessPage(pdfFillerDriver);
        sendToSignSuccessPage.isOpened();
        setActiveDriver(driver);
        pdfFillerDriver.quit();
        contactsPage.isOpened();
        String newName = contactsPage.getContactFirstName(1);
        checkEquals(newName, oldName, "Wrong contact name");
    }

    @AfterTest
    public void cleanUp() {
        deleteTemplate();
    }

    @AfterMethod
    public void cleanProcess() {
        deleteProcess();
    }

    private void deleteTemplate() {
        try {
            getUrl(homePageURL);
            salesAppBasePage.isOpened();
            AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
            salesAppHomePage = appLauncherPopUp.openSalesAppPage();
            ContactsPage contactsPage = salesAppHomePage.openTab(SalesTab.CONTACTS);
            ContactConcretePage contactConcretePage = contactsPage.openObjectByNumber(1);
            daDaDocsFullApp = contactConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullApp();
            TemplatesPage templatesPage = daDaDocsFullApp.openTab(SaleforceMyDocsTab.TEMPLATES);
            templatesPage.selectDadadocsItemByFullName(templateName);
            templatesPage.selectAnAction(DELETE_TEMPLATE);
            templatesPage.deleteTemplate();
            daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS).selectDaDaDocsLastFile().selectAnAction(DELETE);
            daDaDocsFullApp.deleteFile();
        } catch (Exception e) {
            Logger.warning("Template is not deleted");
        }
    }

    private void deleteProcess() {
        try {
            getUrl(homePageURL);
            salesAppBasePage.isOpened();
            SetupSalesforcePage setupSalesforcePage = salesAppBasePage.openSetupPage();
            processBuilderPage = setupSalesforcePage.openProcessBuilder();
            processBuilderPage.deactivateProcess(processName);
            processBuilderPage.deleteProcess(processName);
        } catch (Exception e) {
            Logger.warning("Process is not deleted");
        }
    }
}
