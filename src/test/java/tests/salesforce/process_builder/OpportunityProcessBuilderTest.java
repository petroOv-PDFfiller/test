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
import pages.pdffiller.editors.newJSF.constructor.ConstructorJSFiller;
import pages.pdffiller.editors.newJSF.constructor.enums.ConstructorTool;
import pages.pdffiller.workflow.send_to_sign.SendToSignSuccessPage;
import pages.salesforce.app.AppLauncherPopUp;
import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.DaDaDocs.editor.constructor.ConstructorDropDownSaleForce;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.DaDaDocs.full_app.main_tabs.TemplatesPage;
import pages.salesforce.app.DaDaDocs.full_app.templates.CreateTemplateWizardPage;
import pages.salesforce.app.DaDaDocs.full_app.templates.entities.Record;
import pages.salesforce.app.DaDaDocs.full_app.templates.tabs.*;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.SalesAppHomePage;
import pages.salesforce.app.sf_objects.contacts.ContactsPage;
import pages.salesforce.app.sf_objects.opportunities.ManageContactRolesPopUp;
import pages.salesforce.app.sf_objects.opportunities.OpportunitiesConcretePage;
import pages.salesforce.app.sf_objects.opportunities.OpportunitiesPage;
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
import static api.salesforce.entities.SalesforceObject.OPPORTUNITY;
import static core.check.Check.checkTrue;
import static data.salesforce.SalesforceTestData.DocumentsActions.DELETE;
import static data.salesforce.SalesforceTestData.SalesforceApexClasses.OPPORTUNITY_AUTOMATED_TEMPLATES_SENDER;
import static data.salesforce.SalesforceTestData.SalesforceApexVariables.FIELD_REFERENCE;
import static data.salesforce.SalesforceTestData.SalesforceOpportunityStages.PROSPECTING;
import static data.salesforce.SalesforceTestData.SalesforceOpportunityStages.QUALIFICATION;
import static data.salesforce.SalesforceTestData.SalesforceProcessBuilderActionTypes.APEX;
import static data.salesforce.SalesforceTestData.TemplatesActions.DELETE_TEMPLATE;
import static org.testng.Assert.assertTrue;
import static pages.salesforce.enums.V3.TemplateCreateTabs.TEMPLATE_INFO;

@Feature("Opportunity process builder")
@Listeners({WebTestListener.class, ImapListener.class})
public class OpportunityProcessBuilderTest extends SalesforceBaseTest {

    private WebDriver driver;
    private DaDaDocsFullApp daDaDocsFullApp;
    private String templateName = "name" + StringMan.getRandomString(4);
    private SalesAppHomePage salesAppHomePage;
    private String fileToUpload = new File("src/test/resources/uploaders/Constructor.pdf").getAbsolutePath();
    private String homePageURL;
    private String processName;
    private ProcessBuilderPage processBuilderPage;
    private SalesAppBasePage salesAppBasePage;
    private String templateID;
    private String opportunityName;
    private String contactFirstName;
    private String contactLastName;
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
        OpportunitiesPage opportunitiesPage = salesAppHomePage.openTab(SalesTab.OPPORTUNITIES);
        OpportunitiesConcretePage opportunitiesConcretePage = opportunitiesPage.openObjectByNumber(1);
        daDaDocsFullApp = opportunitiesConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullApp();
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
        ConstructorDropDownSaleForce opportunityStage = (ConstructorDropDownSaleForce) document.putContent(ConstructorTool.DROP_DOWN, pageWidth / 4, pageHeight / 13);
        opportunityStage.addDropDownItem(PROSPECTING);
        opportunityStage.addDropDownItem(QUALIFICATION);
        String opportunityStageFieldName = opportunityStage.getFieldName();
        TemplateMapToPrefillPage prefillPage = addFieldsPage.saveTemplate();
        prefillPage.isOpened();
        prefillPage.addRecord(OPPORTUNITY.getObjectName());
        prefillPage.addField();
        prefillPage.mapField(0, "Stage (Picklist)", opportunityStageFieldName);
        prefillPage.saveRecord();
        List<Record> prefillMapping = prefillPage.initRecords();
        TemplateMapToUpdatePage mapToUpdatePage = prefillPage.navigateToNextTab();
        mapToUpdatePage.addRecord(OPPORTUNITY.getObjectName());
        mapToUpdatePage.addField();
        mapToUpdatePage.mapField(0, "Stage (Picklist)", opportunityStageFieldName);
        mapToUpdatePage.saveRecord();
        TemplateMapToCreatePage mapToCreatePage = mapToUpdatePage.navigateToNextTab();
        TemplateAccessSettingsPage accessSettingsPage = mapToCreatePage.navigateToNextTab();
        accessSettingsPage.navigateToNextTab();
        TemplatesPage templatesPage = daDaDocsFullApp.openTab(SaleforceMyDocsTab.TEMPLATES);
        assertTrue(daDaDocsFullApp.isDadaDocsItemPresent(templateName), "Complex template is not present");
        templatesPage.selectDadadocsItemByFullName(templateName);
        templateID = templatesPage.getTemplateID(templateName);
    }

    @Test
    @Story("Send complex template for signature to Primary Contact Role on Opportunity.Stage changes.")
    public void sendComplexTemplateTest() {
        opportunityName = "opportunity" + StringMan.getRandomString(5);
        contactFirstName = "FN" + StringMan.getRandomString(5);
        contactLastName = "LN" + StringMan.getRandomString(5);
        processName = "process" + StringMan.getRandomString(5);
        getUrl(homePageURL);
        salesAppBasePage.isOpened();
        AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
        salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        OpportunitiesPage opportunitiesPage = salesAppHomePage.openTab(SalesTab.OPPORTUNITIES);
        OpportunitiesConcretePage opportunitiesConcretePage = opportunitiesPage.addNewOpportunity(opportunityName, PROSPECTING);
        ManageContactRolesPopUp manageContactRolesPopUp = opportunitiesConcretePage.openContactRolesManager();
        manageContactRolesPopUp.openAddNewContactPopUp().setFirstName(contactFirstName)
                .setLastName(contactLastName).setEmail(recipientEmail).saveContact();
        manageContactRolesPopUp.setPrimaryContact(contactFirstName + " " + contactLastName);
        manageContactRolesPopUp.saveContactRoles();
        getUrl(homePageURL);
        salesAppBasePage.isOpened();
        SetupSalesforcePage setupSalesforcePage = salesAppBasePage.openSetupPage();
        processBuilderPage = setupSalesforcePage.openProcessBuilder();
        NewProcessPopUp processPopUp = processBuilderPage.addNewProcess();
        ProcessBuilderEditorPage processBuilderEditorPage = processPopUp.setName(processName).setRecordChangesAsProcessTrigger().saveProcess();
        processBuilderEditorPage.addObject(OPPORTUNITY.getObjectName()).setProcessStartAlways().saveObject();
        processBuilderEditorPage.setCriteriaWithoutAction("criteria").saveCriteria();
        SelectFieldPopUp selectFieldPopUp = processBuilderEditorPage.addAction(APEX, "action", OPPORTUNITY_AUTOMATED_TEMPLATES_SENDER)
                .setOpportunityAutomatedTemplateSenderVariables(FIELD_REFERENCE, templateID);
        processBuilderEditorPage = selectFieldPopUp.setOpportunityId().chooseField();
        processBuilderEditorPage.saveAction();
        processBuilderEditorPage.activateProcess();

        getUrl(homePageURL);
        salesAppBasePage.isOpened();
        appLauncherPopUp = salesAppBasePage.openAppLauncher();
        salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        opportunitiesPage = salesAppBasePage.openTab(SalesTab.OPPORTUNITIES);
        ImapClient imap = new ImapClient(recipientEmail, adminPassword);
        imap.deleteAllMessagesWithSubject("signature request");
        opportunitiesConcretePage = opportunitiesPage.openObjectByName(opportunityName);
        opportunitiesConcretePage.setStage(QUALIFICATION);
        List<Message> messages = imap.findMessages(With.subject("signature request"));
        checkTrue(messages.size() > 0, "No messages in mailbox");
        MessageContent messageContent = new MessageContent(imap, messages.get(0));
        List<String> urls = messageContent.getLinkUrls();
        String urlToSign = urls.get(1);
        WebDriver pdfFillerDriver = getDriver(Browser.CHROME, false);
        setActiveDriver(pdfFillerDriver);
        checkTrue(urlToSign != null, "No needful link in message");
        getUrl(urlToSign);
        NewJSFiller newJSFiller = new NewJSFiller(pdfFillerDriver);

        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.initExistingElements();

        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        SendToSignSuccessPage sendToSignSuccessPage = new SendToSignSuccessPage(pdfFillerDriver);
        sendToSignSuccessPage.isOpened();
        setActiveDriver(driver);
        pdfFillerDriver.quit();
        opportunitiesConcretePage.isOpened();
        DaDaDocsFullApp daDaDocsFullApp = opportunitiesConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullApp();
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        assertTrue(daDaDocsFullApp.isDadaDocsItemPresent(templateName + "_Signed"), " signed document is not returned");
    }

    @Test
    @Story("Run Process Builder on Opportunity without Primary Contact Role.")
    public void sendComplexTemplateWOPrimaryCRTest() {
        opportunityName = "opportunity" + StringMan.getRandomString(5);
        contactFirstName = "FN" + StringMan.getRandomString(5);
        contactLastName = "LN" + StringMan.getRandomString(5);
        processName = "process" + StringMan.getRandomString(5);
        getUrl(homePageURL);
        salesAppBasePage.isOpened();
        AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
        salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        OpportunitiesPage opportunitiesPage = salesAppHomePage.openTab(SalesTab.OPPORTUNITIES);
        OpportunitiesConcretePage opportunitiesConcretePage = opportunitiesPage.addNewOpportunity(opportunityName, PROSPECTING);
        ManageContactRolesPopUp manageContactRolesPopUp = opportunitiesConcretePage.openContactRolesManager();
        manageContactRolesPopUp.openAddNewContactPopUp().setFirstName(contactFirstName)
                .setLastName(contactLastName).setEmail(recipientEmail).saveContact();
        manageContactRolesPopUp.saveContactRoles();
        getUrl(homePageURL);
        salesAppBasePage.isOpened();
        SetupSalesforcePage setupSalesforcePage = salesAppBasePage.openSetupPage();
        processBuilderPage = setupSalesforcePage.openProcessBuilder();
        NewProcessPopUp processPopUp = processBuilderPage.addNewProcess();
        ProcessBuilderEditorPage processBuilderEditorPage = processPopUp.setName(processName).setRecordChangesAsProcessTrigger().saveProcess();
        processBuilderEditorPage.addObject(OPPORTUNITY.getObjectName()).setProcessStartAlways().saveObject();
        processBuilderEditorPage.setCriteriaWithoutAction("criteria").saveCriteria();
        SelectFieldPopUp selectFieldPopUp = processBuilderEditorPage.addAction(APEX, "action", OPPORTUNITY_AUTOMATED_TEMPLATES_SENDER)
                .setOpportunityAutomatedTemplateSenderVariables(FIELD_REFERENCE, templateID);
        processBuilderEditorPage = selectFieldPopUp.setOpportunityId().chooseField();
        processBuilderEditorPage.saveAction();
        processBuilderEditorPage.activateProcess();

        getUrl(homePageURL);
        salesAppBasePage.isOpened();
        appLauncherPopUp = salesAppBasePage.openAppLauncher();
        salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        opportunitiesPage = salesAppBasePage.openTab(SalesTab.OPPORTUNITIES);
        opportunitiesConcretePage = opportunitiesPage.openObjectByName(opportunityName);
        ImapClient imap = new ImapClient(adminEmail, adminPassword);
        imap.deleteAllMessagesWithSubject("Opportunity Automated Templates Sender Errors");
        opportunitiesConcretePage.setStage(QUALIFICATION);
        List<Message> messages = imap.findMessages(With.subject("Opportunity Automated Templates Sender Errors"));

        checkTrue(messages.size() > 0, "No messages in mailbox");
        String messageContent = imap.getContent(messages.get(0));
        assertTrue(analyzeEmailMessage(messageContent).contains(
                "Sorry, the document wasn’t sent to a recipient because there is no Primary Contact for Opport" +
                        "unity (" + opportunityName + "). Please, specify a Primary Contact for your record and try again."),
                "Wrong message");
    }

    @Test
    @Story("Run Process Builder on some other object(not Opportunity).")
    public void sendComplexTemplateOnOtherObject() {
        opportunityName = "opportunity" + StringMan.getRandomString(5);
        contactFirstName = "FN" + StringMan.getRandomString(5);
        contactLastName = "LN" + StringMan.getRandomString(5);
        processName = "process" + StringMan.getRandomString(5);
        getUrl(homePageURL);
        salesAppBasePage.isOpened();
        SetupSalesforcePage setupSalesforcePage = salesAppBasePage.openSetupPage();
        processBuilderPage = setupSalesforcePage.openProcessBuilder();
        NewProcessPopUp processPopUp = processBuilderPage.addNewProcess();
        ProcessBuilderEditorPage processBuilderEditorPage = processPopUp.setName(processName).setRecordChangesAsProcessTrigger().saveProcess();
        processBuilderEditorPage.addObject(CONTACT.getObjectName()).setProcessStartAlways().saveObject();
        processBuilderEditorPage.setCriteriaWithoutAction("criteria").saveCriteria();
        SelectFieldPopUp selectFieldPopUp = processBuilderEditorPage.addAction(APEX, "action", OPPORTUNITY_AUTOMATED_TEMPLATES_SENDER)
                .setOpportunityAutomatedTemplateSenderVariables(FIELD_REFERENCE, templateID);
        processBuilderEditorPage = selectFieldPopUp.setContactId().chooseField();
        processBuilderEditorPage.saveAction();
        processBuilderEditorPage.activateProcess();

        getUrl(homePageURL);
        salesAppBasePage.isOpened();
        AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
        salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        ContactsPage contactsPage = salesAppBasePage.openTab(SalesTab.CONTACTS);
        ImapClient imap = new ImapClient(adminEmail, adminPassword);
        imap.deleteAllMessagesWithSubject("Opportunity Automated Templates Sender Errors");
        String oldName = contactsPage.getContactFirstName(1);
        List<Message> messages = imap.findMessages(With.subject("Opportunity Automated Templates Sender Errors"));
        checkTrue(messages.size() > 0, "No messages in mailbox");
        String messageContent = imap.getContent(messages.get(0));
        assertTrue(analyzeEmailMessage(messageContent).contains("Sorry, the document wasn’t sent to a recipient because" +
                        " you have chosen Parent ID other than Opportunity. Please, specify Opportunity ID and try again."),
                "Wrong message");
    }

    @Test
    @Story("Run Process Builder on Opportunity with Primary Contact Role without specified email.")
    public void sendComplexTemplateWOContactEmailTest() {
        opportunityName = "opportunity" + StringMan.getRandomString(5);
        contactFirstName = "FN" + StringMan.getRandomString(5);
        contactLastName = "LN" + StringMan.getRandomString(5);
        processName = "process" + StringMan.getRandomString(5);
        getUrl(homePageURL);
        salesAppBasePage.isOpened();
        AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
        salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        OpportunitiesPage opportunitiesPage = salesAppHomePage.openTab(SalesTab.OPPORTUNITIES);
        OpportunitiesConcretePage opportunitiesConcretePage = opportunitiesPage.addNewOpportunity(opportunityName, PROSPECTING);
        ManageContactRolesPopUp manageContactRolesPopUp = opportunitiesConcretePage.openContactRolesManager();
        manageContactRolesPopUp.openAddNewContactPopUp().setFirstName(contactFirstName)
                .setLastName(contactLastName).setEmail("").saveContact();
        manageContactRolesPopUp.setPrimaryContact(contactFirstName + " " + contactLastName);
        manageContactRolesPopUp.saveContactRoles();
        getUrl(homePageURL);
        salesAppBasePage.isOpened();
        SetupSalesforcePage setupSalesforcePage = salesAppBasePage.openSetupPage();
        processBuilderPage = setupSalesforcePage.openProcessBuilder();
        NewProcessPopUp processPopUp = processBuilderPage.addNewProcess();
        ProcessBuilderEditorPage processBuilderEditorPage = processPopUp.setName(processName).setRecordChangesAsProcessTrigger().saveProcess();
        processBuilderEditorPage.addObject(OPPORTUNITY.getObjectName()).setProcessStartAlways().saveObject();
        processBuilderEditorPage.setCriteriaWithoutAction("criteria").saveCriteria();
        SelectFieldPopUp selectFieldPopUp = processBuilderEditorPage.addAction(APEX, "action", OPPORTUNITY_AUTOMATED_TEMPLATES_SENDER)
                .setOpportunityAutomatedTemplateSenderVariables(FIELD_REFERENCE, templateID);
        processBuilderEditorPage = selectFieldPopUp.setOpportunityId().chooseField();
        processBuilderEditorPage.saveAction();
        processBuilderEditorPage.activateProcess();

        getUrl(homePageURL);
        salesAppBasePage.isOpened();
        appLauncherPopUp = salesAppBasePage.openAppLauncher();
        salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        opportunitiesPage = salesAppBasePage.openTab(SalesTab.OPPORTUNITIES);
        opportunitiesConcretePage = opportunitiesPage.openObjectByName(opportunityName);
        ImapClient imap = new ImapClient(adminEmail, adminPassword);
        imap.deleteAllMessagesWithSubject("Opportunity Automated Templates Sender Errors");
        opportunitiesConcretePage.setStage(QUALIFICATION);
        List<Message> messages = imap.findMessages(With.subject("Opportunity Automated Templates Sender Errors"));
        checkTrue(messages.size() > 0, "No messages in mailbox");
        String messageContent = imap.getContent(messages.get(0));
        assertTrue(analyzeEmailMessage(messageContent).contains("Sorry, the document wasn’t sent to a recipient, because" +
                        " there is no email specified for a Primary Contact for Opportunity (" + opportunityName + "). " +
                        "Please, specify an email for a Primary Contact and try again."),
                "Wrong message");
    }

    @Test
    @Story("Run Process Builder on Opportunity without any Contact Role specified.")
    public void sendComplexTemplateWOContactRolesTest() {
        opportunityName = "opportunity" + StringMan.getRandomString(5);
        contactFirstName = "FN" + StringMan.getRandomString(5);
        contactLastName = "LN" + StringMan.getRandomString(5);
        processName = "process" + StringMan.getRandomString(5);
        getUrl(homePageURL);
        salesAppBasePage.isOpened();
        AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
        salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        OpportunitiesPage opportunitiesPage = salesAppHomePage.openTab(SalesTab.OPPORTUNITIES);
        opportunitiesPage.addNewOpportunity(opportunityName, PROSPECTING);
        getUrl(homePageURL);
        salesAppBasePage.isOpened();
        SetupSalesforcePage setupSalesforcePage = salesAppBasePage.openSetupPage();
        processBuilderPage = setupSalesforcePage.openProcessBuilder();
        NewProcessPopUp processPopUp = processBuilderPage.addNewProcess();
        ProcessBuilderEditorPage processBuilderEditorPage = processPopUp.setName(processName).setRecordChangesAsProcessTrigger().saveProcess();
        processBuilderEditorPage.addObject(OPPORTUNITY.getObjectName()).setProcessStartAlways().saveObject();
        processBuilderEditorPage.setCriteriaWithoutAction("criteria").saveCriteria();
        SelectFieldPopUp selectFieldPopUp = processBuilderEditorPage.addAction(APEX, "action", OPPORTUNITY_AUTOMATED_TEMPLATES_SENDER)
                .setOpportunityAutomatedTemplateSenderVariables(FIELD_REFERENCE, templateID);
        processBuilderEditorPage = selectFieldPopUp.setOpportunityId().chooseField();
        processBuilderEditorPage.saveAction();
        processBuilderEditorPage.activateProcess();

        getUrl(homePageURL);
        salesAppBasePage.isOpened();
        SalesAppBasePage salesAppBasePage = new SalesAppBasePage(driver);
        appLauncherPopUp = salesAppBasePage.openAppLauncher();
        salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        opportunitiesPage = salesAppBasePage.openTab(SalesTab.OPPORTUNITIES);
        OpportunitiesConcretePage opportunitiesConcretePage = opportunitiesPage.openObjectByName(opportunityName);
        ImapClient imap = new ImapClient(adminEmail, adminPassword);
        imap.deleteAllMessagesWithSubject("Opportunity Automated Templates Sender Errors");
        opportunitiesConcretePage.setStage(QUALIFICATION);
        List<Message> messages = imap.findMessages(With.subject("Opportunity Automated Templates Sender Errors"));
        checkTrue(messages.size() > 0, "No messages in mailbox");
        String messageContent = imap.getContent(messages.get(0));
        assertTrue(analyzeEmailMessage(messageContent).contains("Sorry, the document wasn’t sent to a recipient because" +
                        " there is no related Contact Roles for Opportunity (" + opportunityName + ")." +
                        " Please, add Contact Roles for your record and try again."),
                "Wrong message");
    }

    @AfterTest
    public void cleanUp() {
        deleteTemplate();
    }

    @AfterMethod
    public void clearResults() {
        deleteProcess();
        deleteRecords();
    }

    private void deleteTemplate() {
        try {
            if (templateID != null) {
                getUrl(homePageURL);
                salesAppBasePage.isOpened();
                AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
                salesAppHomePage = appLauncherPopUp.openSalesAppPage();
                OpportunitiesPage opportunitiesPage = salesAppHomePage.openTab(SalesTab.OPPORTUNITIES);
                OpportunitiesConcretePage opportunitiesConcretePage = opportunitiesPage.openObjectByNumber(1);
                daDaDocsFullApp = opportunitiesConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullApp();
                TemplatesPage templatesPage = daDaDocsFullApp.openTab(SaleforceMyDocsTab.TEMPLATES);
                templatesPage.selectDadadocsItemByFullName(templateName);
                templatesPage.selectAnAction(DELETE_TEMPLATE);
                templatesPage.deleteTemplate();
                daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS).selectDaDaDocsLastFile().selectAnAction(DELETE);
                daDaDocsFullApp.deleteFile();
            }
        } catch (Exception e) {
            Logger.error("Template is not deleted");
        }
    }

    private void deleteRecords() {
        try {
            if (opportunityName != null) {
                getUrl(homePageURL);
                salesAppBasePage.isOpened();
                AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
                salesAppHomePage = appLauncherPopUp.openSalesAppPage();
                OpportunitiesPage opportunitiesPage = salesAppHomePage.openTab(SalesTab.OPPORTUNITIES);
                opportunitiesPage.deleteObjectByName(opportunityName);
                salesAppHomePage.isOpened();
                ContactsPage contactsPage = salesAppHomePage.openTab(SalesTab.CONTACTS);
                contactsPage.deleteObjectByName(contactFirstName + " " + contactLastName);
            }
        } catch (Exception e) {
            Logger.error("Records are not deleted");
        }
    }

    private void deleteProcess() {
        try {
            if (processName != null) {
                getUrl(homePageURL);
                salesAppBasePage.isOpened();
                SetupSalesforcePage setupSalesforcePage = salesAppBasePage.openSetupPage();
                processBuilderPage = setupSalesforcePage.openProcessBuilder();
                processBuilderPage.deactivateProcess(processName);
                processBuilderPage.deleteProcess(processName);
            }
        } catch (Exception e) {
            Logger.error("Process is not deleted");
        }
    }

    private String analyzeEmailMessage(String emailMessage) {
        return emailMessage.replace("=92", "’").replace("=\r\n", "");
    }
}
