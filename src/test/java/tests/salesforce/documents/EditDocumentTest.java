package tests.salesforce.documents;

import core.AllureAttachments;
import core.DriverWinMan;
import core.DriverWindow;
import data.TestData;
import imap.ImapClient;
import imap.MessageContent;
import imap.With;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.pdffiller.editors.newJSF.NewJSFiller;
import pages.pdffiller.editors.newJSF.TextContentElement;
import pages.pdffiller.my_docs.MyDocsBasicPage;
import pages.pdffiller.payment.PaidPlan;
import pages.pdffiller.workflow.send_to_sign.SendToSignSuccessPage;
import pages.salesforce.app.AppLauncherPopUp;
import pages.salesforce.app.DaDaDocs.CustomButtonS2SPage;
import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.SalesAppHomePage;
import pages.salesforce.app.sf_objects.contacts.ContactConcretePage;
import pages.salesforce.app.sf_objects.contacts.ContactsPage;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import tests.salesforce.SalesforceBaseTest;
import utils.AShotMan;
import utils.StringMan;
import utils.TimeMan;

import javax.imageio.ImageIO;
import javax.mail.Message;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static core.check.Check.checkTrue;
import static data.TestData.PATH_TO_ACCESSORY_TEST_RESOURCES_FOLDER;
import static pages.salesforce.enums.lightning_component.DaDaDocsLightningComponentActions.EDIT_DOCUMENT;

@Feature("EditDocumentTest")
@Listeners({WebTestListener.class, ImapListener.class})
public class EditDocumentTest extends SalesforceBaseTest {

    private final String orderFormPreview = "/order_form_preview.png";
    private final String editedDocument = "/edited_document.png";
    private String contactFirstName = "Tim";
    private String contactLastName = "Barr";
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

    @Parameters({"takeNewScreenshot", "recipientEmail"})
    @BeforeTest
    public void setUp(@Optional("true") boolean takeNewScreenshot,
                      @Optional("pd@support.pdffiller.com") String recipientEmail) throws IOException, URISyntaxException {
        driver = getDriver();

        driverWinMan = new DriverWinMan(driver);

        salesforceWindow = driverWinMan.getCurrentWindow();
        checkTrue(driverWinMan.switchToNewWindow(), "New window is not created or cannot switch to it");
        pdffillerWindow = driverWinMan.getCurrentWindow();

        pdffillerEmail = StringMan.makeUniqueEmail(recipientEmail);
        registerUser(pdffillerEmail, pdffillerPassword);

        this.takeNewScreenshot = takeNewScreenshot;

        initPathToImages();
        setupDirectories();
    }


    @Story("editDocumentAndCheckDifference")
    @Test
    public void editDocumentAndCheckDifference() throws IOException {
        ImapClient imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        imap.deleteAllMessages();
        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "Cannot switch to window");
        MyDocsBasicPage myDocsBasicPage = autoLogin(pdffillerEmail, testData.url);
        autoPay(PaidPlan.BUSINESS_ANNUAL);
        myDocsBasicPage.goToMyDocsPage(testData.url);
        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
        SalesAppHomePage salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        ContactsPage contactsPage = salesAppHomePage.openContactsPage();
        ContactConcretePage contactConcretePage = contactsPage.openObjectByName(contactFirstName + " " + contactLastName);
        CustomButtonS2SPage orderFormPage = contactConcretePage.clickOrderFormButton();

        Screenshot preview = orderFormPage.switchToPreview().takeScreenShotPagePreview();
        AShotMan aShot = new AShotMan(driver);
        SoftAssert softAssert = new SoftAssert();

        if (takeNewScreenshot) {
            TimeMan.sleep(1);
            aShot.saveImage(preview, orderFormPreviewExpected);
        }

        ImageDiff previewDiff = aShot.makeDiff(aShot.getImageAsScreenshot(orderFormPreviewExpected), preview);
        softAssert.assertEquals(previewDiff.getDiffSize(), 0, "Ordered form preview have broken fields");
        aShot.saveImage(preview, orderFormPreviewActual);
        ImageIO.write(previewDiff.getMarkedImage(), "png", new File(orderFormPreviewDiff));
        AllureAttachments.imageAttachment("order_form_preview_actual", orderFormPreviewActual);
        AllureAttachments.imageAttachment("order_form_preview_expected", orderFormPreviewExpected);
        AllureAttachments.imageAttachment("order_form_preview_diff", orderFormPreviewDiff);

        orderFormPage.previewTab.switchToDefaultContent();
        orderFormPage.isOpened();


        String docPartialName = orderFormPage.getDocumentName();
        orderFormPage.removeAllRecipients();
        orderFormPage.addRecipient(pdffillerEmail, "Recipient");
        orderFormPage.clickSendButton();
        contactConcretePage.isOpened();

        List<Message> messages = imap.findMessages(With.subject(subject));

        checkTrue(messages.size() > 0, "No messages in mailbox");
        MessageContent messageContent = new MessageContent(imap, messages.get(0));
        List<String> urls = messageContent.getLinkUrls();
        String urlToSign = null;
        for (String url : urls) {
            if (url.contains("useNew")) {
                urlToSign = url;
                break;
            }
        }
        checkTrue(urlToSign != null, "No needful link in message");
        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "Cannot switch to window");
        getUrl(urlToSign);
        NewJSFiller newJSFiller = new NewJSFiller(driver);

        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.initExistingElements();

        TextContentElement firstNameTextField = (TextContentElement) NewJSFiller.fillableFields.get(0);
        TextContentElement lastNameTextField = (TextContentElement) NewJSFiller.fillableFields.get(1);
        firstNameTextField.makeActive(); // click on 1st fillable field
        firstNameTextField.fetchText();
        softAssert.assertEquals(firstNameTextField.text, contactFirstName, "Contact first name is not valid");

        for (int i = 0; i < contactLastName.length(); i++) {
            firstNameTextField.deleteContentFromField();
        }
        firstNameTextField.typeText(editedFirstName);

        lastNameTextField.makeActive(); // click on 2nd fillable field
        lastNameTextField.fetchText();
        softAssert.assertEquals(lastNameTextField.text, contactLastName, "Contact last name is not valid");

        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        SendToSignSuccessPage sendToSignSuccessPage = new SendToSignSuccessPage(driver);
        sendToSignSuccessPage.isOpened();
        driver.close();
        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
        TimeMan.sleep(5);
        refreshPage();
        contactConcretePage.isOpened();
        contactConcretePage.switchToDaDaDocsLightningComponent();
        contactConcretePage.daDaDocsLightningComponent.selectDadadocsItemByFullName(docPartialName);
        DaDaDocsEditor daDaDocsEditor = contactConcretePage.daDaDocsLightningComponent.selectAnAction(EDIT_DOCUMENT);
        daDaDocsEditor.fitPage();
        Screenshot editedName = daDaDocsEditor.takeScreenShotEditor();

        if (takeNewScreenshot) {
            TimeMan.sleep(1);
            aShot.saveImage(editedName, editedDocumentExpected);
        }

        ImageDiff diff = aShot.makeDiff(aShot.getImageAsScreenshot(editedDocumentExpected), editedName);
        softAssert.assertEquals(diff.getDiffSize(), 0, "Edited form in editor have broken fields");
        aShot.saveImage(editedName, editedDocumentActual);

        ImageIO.write(diff.getMarkedImage(), "png", new File(editedDocumentDiff));
        AllureAttachments.imageAttachment("edited_doc_in_editor_actual", editedDocumentActual);
        AllureAttachments.imageAttachment("edited_doc_in_editor_expected", editedDocumentExpected);
        AllureAttachments.imageAttachment("edited_doc_diff", editedDocumentDiff);
        salesAppHomePage.isOpened();
        contactsPage = salesAppHomePage.openContactsPage();
        contactsPage.editContactFirstName(editedFirstName + " " + contactLastName, contactFirstName);
        softAssert.assertAll();
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
        pathToExpectedLayouts = new File(PATH_TO_ACCESSORY_TEST_RESOURCES_FOLDER + "/layout/edit_document/expected/").getAbsolutePath();
        pathToActualLayouts = new File(PATH_TO_ACCESSORY_TEST_RESOURCES_FOLDER + "/layout/edit_document/actual/").getAbsolutePath();
        pathToDiffLayouts = new File(PATH_TO_ACCESSORY_TEST_RESOURCES_FOLDER + "/layout/edit_document/diff/").getAbsolutePath();

        orderFormPreviewActual = pathToActualLayouts + orderFormPreview;
        orderFormPreviewExpected = pathToExpectedLayouts + orderFormPreview;
        orderFormPreviewDiff = pathToDiffLayouts + orderFormPreview;
        editedDocumentActual = pathToActualLayouts + editedDocument;
        editedDocumentExpected = pathToExpectedLayouts + editedDocument;
        editedDocumentDiff = pathToDiffLayouts + editedDocument;
    }
}