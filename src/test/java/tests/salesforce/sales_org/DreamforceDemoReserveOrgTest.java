package tests.salesforce.sales_org;

import api.salesforce.entities.SalesforceObject;
import core.DriverWinMan;
import core.DriverWindow;
import data.TestData;
import imap.ImapClient;
import imap.MessageContent;
import imap.With;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.pdffiller.editors.newJSF.*;
import pages.pdffiller.workflow.send_to_sign.SendToSignSuccessPage;
import pages.salesforce.app.DaDaDocs.CustomButtonS2SPage;
import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.SalesAppHomePage;
import pages.salesforce.app.sf_objects.contacts.ContactConcretePage;
import pages.salesforce.app.sf_objects.contacts.ContactsPage;
import pages.salesforce.app.sf_objects.opportunities.OpportunitiesConcretePage;
import pages.salesforce.app.sf_objects.opportunities.OpportunitiesPage;
import pages.salesforce.enums.SaleforceMyDocsTab;
import tests.salesforce.SalesforceBaseTest;
import utils.Logger;
import utils.StringMan;
import utils.TimeMan;

import javax.mail.Message;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static core.check.Check.checkTrue;
import static core.drivers.SelenoidDriverBuilder.getSelenoidDriver;
import static data.TestData.PATH_TO_ACCESSORY_TEST_RESOURCES_FOLDER;
import static data.salesforce.SalesforceTestData.DocumentsActions.DELETE;
import static pages.salesforce.enums.lightning_component.DaDaDocsLightningComponentActions.EDIT_DOCUMENT;

@Feature("EditDocumentTest")
@Listeners({WebTestListener.class, ImapListener.class})
public class DreamforceDemoReserveOrgTest extends SalesforceBaseTest {

    private final String orderFormPreview = "/order_form_preview.png";
    private final String editedDocument = "/edited_document.png";
    private String contactFirstName = "AutoTest";
    private String contactLastName = getUniqueRecordName(SalesforceObject.CONTACT);
    private String editedFirstName = "Timofey";//hardcoded to sikuli image
    private WebDriver driver;
    private String pdffillerEmail;
    private String pdffillerPassword = TestData.defaultPassword;
    private String subject = "signature request";
    private String pathToExpectedLayouts;
    private String orderFormPreviewActual;
    private String orderFormPreviewExpected;
    private String orderFormPreviewDiff;
    private String editedDocumentActual;
    private String editedDocumentExpected;
    private String editedDocumentDiff;
    private boolean takeNewScreenshot;
    private DriverWindow salesforceWindow;
    private DriverWindow pdffillerWindow;
    private DriverWinMan driverWinMan;
    private String pathToActualLayouts;
    private String pathToDiffLayouts;
    private ContactsPage contactsPage;
    private String contactURL;
    private String opportunitiesURL;
    private SalesAppHomePage salesAppHomePage;
    private OpportunitiesPage opportunitiesPage;
    private ImapClient imap;
    private String docPartialName;
    private String documentStatus = "_Signed";
    private String contactId;

    @Parameters({"takeNewScreenshot", "recipientEmail"})
    @BeforeTest
    public void setUp(@Optional("true") boolean takeNewScreenshot,
                      @Optional("pdf_sf_recipient@support.pdffiller.com") String recipientEmail) throws IOException, URISyntaxException {
        driver = getDriver();
        driver.manage().window().setSize(new Dimension(1600, 900));
        driverWinMan = new DriverWinMan(driver);

        salesforceWindow = driverWinMan.getCurrentWindow();
//        checkTrue(driverWinMan.switchToNewWindow(), "New window is not created or cannot switch to it");
//        pdffillerWindow = driverWinMan.getCurrentWindow();

        pdffillerEmail = StringMan.makeUniqueEmail(recipientEmail);
        registerUser(pdffillerEmail);

        this.takeNewScreenshot = takeNewScreenshot;

//        initPathToImages();
//        setupDirectories();

        imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        imap.deleteAllMessagesWithSubject(subject);
        contactId = createContactObject(contactFirstName, contactLastName);
    }


    @Story("demo test reserve org")
    @Test
    public void demoTest() throws IOException {
        setActiveDriver(driver);
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        ContactConcretePage contactConcretePage = salesAppBasePage.openRecordPageById(SalesforceObject.CONTACT, contactId);
        CustomButtonS2SPage orderFormPage = contactConcretePage.clickOrderFormButton("Order Form");

//        Screenshot preview = orderFormPage.switchToPreview().takeScreenShotPagePreview();
//        AShotMan aShot = new AShotMan(driver);
        SoftAssert softAssert = new SoftAssert();

//        if (takeNewScreenshot) {
//            TimeMan.sleep(1);
//            aShot.saveImage(preview, orderFormPreviewExpected);
//        }
//
//        ImageDiff previewDiff = aShot.makeDiff(aShot.getImageAsScreenshot(orderFormPreviewExpected), preview);
//        softAssert.assertEquals(previewDiff.getDiffSize(), 0, "Ordered form preview have broken fields");
//        aShot.saveImage(preview, orderFormPreviewActual);
//        ImageIO.write(previewDiff.getMarkedImage(), "png", new File(orderFormPreviewDiff));
//        AllureAttachments.imageAttachment("order_form_preview_actual", orderFormPreviewActual);
//        AllureAttachments.imageAttachment("order_form_preview_expected", orderFormPreviewExpected);
//        AllureAttachments.imageAttachment("order_form_preview_diff", orderFormPreviewDiff);

//        orderFormPage.previewTab.switchToDefaultContent();
//        orderFormPage.isOpened();


        docPartialName = orderFormPage.getDocumentName();
        orderFormPage.removeAllRecipients();
        orderFormPage.addRecipient(pdffillerEmail, "Recipient");
        orderFormPage.clickSendButton();
        contactConcretePage.isOpened();

        List<Message> messages = imap.findMessages(With.subject(subject));

        checkTrue(messages.size() > 0, "No messages in mailbox");
        MessageContent messageContent = new MessageContent(imap, messages.get(0));
        List<String> urls = messageContent.getLinkUrls();
        String urlToSign = urls.get(0);
        checkTrue(urlToSign != null, "No needful link in message");
//        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "Cannot switch to PDFfiller window");
        WebDriver pdfFillerDriver = getSelenoidDriver();
        setActiveDriver(pdfFillerDriver);
        getUrl(testData.url);
        deleteAllCookies();
        getUrl(urlToSign);
        NewJSFiller newJSFiller = new NewJSFiller(pdfFillerDriver);

        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.initExistingElements();

        TextContentElement firstNameTextField = (TextContentElement) NewJSFiller.fillableFields.get(0);
        TextContentElement lastNameTextField = (TextContentElement) NewJSFiller.fillableFields.get(1);
        TextContentElement companyTextField = (TextContentElement) NewJSFiller.fillableFields.get(2);
        TextContentElement positionTextField = (TextContentElement) NewJSFiller.fillableFields.get(3);
        TextContentElement emailTextField = (TextContentElement) NewJSFiller.fillableFields.get(4);
        TextContentElement phoneTextField = (TextContentElement) NewJSFiller.fillableFields.get(5);
        TextContentElement a5TextField = (TextContentElement) NewJSFiller.fillableFields.get(6);
        TextContentElement a4TextField = (TextContentElement) NewJSFiller.fillableFields.get(7);
        TextContentElement a3TextField = (TextContentElement) NewJSFiller.fillableFields.get(8);
        PictureContentElement pictureField = (PictureContentElement) NewJSFiller.fillableFields.get(9);
        SignContentElement signatureField = (SignContentElement) NewJSFiller.fillableFields.get(10);
        DateContentElement dateField = (DateContentElement) NewJSFiller.fillableFields.get(11);
        TextContentElement a5NumberField = (TextContentElement) NewJSFiller.fillableFields.get(12);
        TextContentElement a4NumberField = (TextContentElement) NewJSFiller.fillableFields.get(13);
        TextContentElement a3NumberField = (TextContentElement) NewJSFiller.fillableFields.get(14);

        firstNameTextField.makeActive(); // click on 1st fillable field
        firstNameTextField.fetchText();
        softAssert.assertEquals(firstNameTextField.text, contactFirstName, "Contact first name is not valid");
        int firstNameLength = contactFirstName.length();
        for (int i = 0; i < firstNameLength; i++) {
            firstNameTextField.deleteContentFromField();
        }

        firstNameTextField.typeText(editedFirstName);

        lastNameTextField.makeActive(); // click on 2nd fillable field
        lastNameTextField.fetchText();
        softAssert.assertEquals(lastNameTextField.text, contactLastName, "Contact last name is not valid");
        a5TextField.typeText("A5");
        a4TextField.typeText("A4");
        a3TextField.typeText("A3");
        a3NumberField.typeText("13");
        a4NumberField.typeText("14");
        a5NumberField.typeText("15");

        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        SendToSignSuccessPage sendToSignSuccessPage = new SendToSignSuccessPage(pdfFillerDriver);
        sendToSignSuccessPage.isOpened();
        pdfFillerDriver.close();
        setActiveDriver(driver);
        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to salesforce window");
        TimeMan.sleep(5);
        refreshPage();
        contactConcretePage.isOpened();
        softAssert.assertTrue(contactConcretePage.isObjectRecordPresent("A5", 15), "Object isn`t added");
        softAssert.assertTrue(contactConcretePage.isObjectRecordPresent("A4", 14), "Object isn`t added");
        softAssert.assertTrue(contactConcretePage.isObjectRecordPresent("A3", 13), "Object isn`t added");
        contactConcretePage.switchToDaDaDocsLightningComponent();
        String signedFile = docPartialName + documentStatus;
        checkTrue(contactConcretePage.daDaDocsLightningComponent.waitForDadaDocsItemPresentAfterSign(signedFile),
                "File " + signedFile + " is not returned after sign");
        contactConcretePage.daDaDocsLightningComponent.selectDadadocsItemByFullName(signedFile);
        DaDaDocsEditor daDaDocsEditor = contactConcretePage.daDaDocsLightningComponent.selectAnAction(EDIT_DOCUMENT);
        daDaDocsEditor.fitPage();
        TimeMan.sleep(5);
//        Screenshot editedDocument = daDaDocsEditor.takeScreenShotEditor();
//
//        if (takeNewScreenshot) {
//            aShot.saveImage(editedDocument, editedDocumentExpected);
//        }
//
//        ImageDiff diff = aShot.makeDiff(aShot.getImageAsScreenshot(editedDocumentExpected), editedDocument);
//        softAssert.assertEquals(diff.getDiffSize(), 0, "Edited form in editor have broken fields");
//        aShot.saveImage(editedDocument, editedDocumentActual);
//
//        ImageIO.write(diff.getMarkedImage(), "png", new File(editedDocumentDiff));
//        AllureAttachments.imageAttachment("edited_doc_in_editor_actual", editedDocumentActual);
//        AllureAttachments.imageAttachment("edited_doc_in_editor_expected", editedDocumentExpected);
//        AllureAttachments.imageAttachment("edited_doc_diff", editedDocumentDiff);

        softAssert.assertAll();
    }

    @Story("Docx template test reserve org")
    @Test
    public void docxTemplate() {
        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to salesforce window");
        salesAppHomePage = new SalesAppHomePage(driver);
        opportunitiesPage = salesAppHomePage.openObjectPage(SalesforceObject.OPPORTUNITY);
        opportunitiesURL = driver.getCurrentUrl();
        OpportunitiesConcretePage opportunitiesConcretePage = opportunitiesPage.openObjectByNumber(1);
        DaDaDocsEditor daDaDocsEditor = opportunitiesConcretePage.clickOrderFormButtonAndRedirectToEditor("SOW");
        daDaDocsEditor.clickDoneButton();
    }

    @Step
    @AfterTest
    public void returnOrgToInitialState() {
        clearContacts();
        clearOpportunities();
    }

    @Step
    private void clearContacts() {
        try {
            ContactConcretePage concretePage = salesAppHomePage.openRecordPageById(SalesforceObject.CONTACT, contactId);
            concretePage.deleteObjectRecord("A5", 15);
            concretePage.deleteObjectRecord("A4", 14);
            concretePage.deleteObjectRecord("A3", 13);
            DaDaDocsFullApp daDaDocsFullApp = concretePage.switchToDaDaDocsLightningComponent().openDadadocsFullApp();
            daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS).selectDadadocsItemByFullName(docPartialName + documentStatus).selectAnAction(DELETE);
            daDaDocsFullApp.deleteFile();
        } catch (Exception e) {
            Logger.warning("clearContacts is not completed");
        }
    }

    @Step
    private void clearOpportunities() {
        try {
            getUrl(opportunitiesURL);
            opportunitiesPage.isOpened();
            OpportunitiesConcretePage opportunitiesConcretePage = opportunitiesPage.openObjectByNumber(1);
            DaDaDocsFullApp daDaDocsFullApp = opportunitiesConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullApp();
            daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS).selectDadadocsItemByFullName("SOW template").selectAnAction(DELETE);
            daDaDocsFullApp.deleteFile();
        } catch (Exception e) {
            Logger.warning("clearOpportunities is not completed");
        }
    }

    private void setupDirectories() throws IOException {
        if (!new File(pathToExpectedLayouts).exists()) {
            checkTrue(new File(pathToExpectedLayouts).mkdirs(), "Folder for expected layouts was not created");
        }
        if (!new File(pathToActualLayouts).exists()) {
            checkTrue(new File(pathToActualLayouts).mkdirs(), "Folder for actual layouts was not created");
        }
        if (!new File(pathToDiffLayouts).exists()) {
            checkTrue(new File(pathToDiffLayouts).mkdirs(), "Folder for diff layouts was not created");
        }

        if (Files.exists(Paths.get(pathToActualLayouts))) {
            FileUtils.cleanDirectory(new File(pathToActualLayouts));
        }
        if (Files.exists(Paths.get(pathToDiffLayouts))) {
            FileUtils.cleanDirectory(new File(pathToDiffLayouts));
        }


        if (takeNewScreenshot) {
            if (Files.exists(Paths.get((pathToExpectedLayouts)))) {
                FileUtils.cleanDirectory(new File((pathToExpectedLayouts)));
            }
        }
    }

    private void initPathToImages() {
        pathToExpectedLayouts = new File(PATH_TO_ACCESSORY_TEST_RESOURCES_FOLDER + "/layout/dreamforce_reserve/expected/").getAbsolutePath();
        pathToActualLayouts = new File(PATH_TO_ACCESSORY_TEST_RESOURCES_FOLDER + "/layout/dreamforce_reserve/actual/").getAbsolutePath();
        pathToDiffLayouts = new File(PATH_TO_ACCESSORY_TEST_RESOURCES_FOLDER + "/layout/dreamforce_reserve/diff/").getAbsolutePath();

        orderFormPreviewActual = pathToActualLayouts + orderFormPreview;
        orderFormPreviewExpected = pathToExpectedLayouts + orderFormPreview;
        orderFormPreviewDiff = pathToDiffLayouts + orderFormPreview;
        editedDocumentActual = pathToActualLayouts + editedDocument;
        editedDocumentExpected = pathToExpectedLayouts + editedDocument;
        editedDocumentDiff = pathToDiffLayouts + editedDocument;
    }
}