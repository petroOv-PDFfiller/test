package tests.salesforce.documents;

import core.DriverWinMan;
import core.DriverWindow;
import data.TestData;
import data.salesforce.SalesforceTestData;
import imap.ImapClient;
import imap.MessageContent;
import imap.With;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import pages.pdffiller.editors.newJSF.NewJSFiller;
import pages.pdffiller.workflow.send_to_sign.SendToSignSuccessPage;
import pages.salesforce.app.AppLauncherPopUp;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.DaDaDocs.send_to_sign.SendToSignPage;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.SalesAppHomePage;
import pages.salesforce.app.sf_objects.contacts.ContactConcretePage;
import pages.salesforce.app.sf_objects.contacts.ContactsPage;
import pages.salesforce.enums.SaleforceMyDocsTab;
import tests.salesforce.SalesforceBaseTest;
import utils.Logger;
import utils.StringMan;

import javax.mail.Message;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static core.check.Check.checkTrue;
import static org.testng.Assert.assertTrue;

@Feature("Check SendToGroup and SendToEach")
@Listeners({WebTestListener.class, ImapListener.class})
public class SendToSignTests extends SalesforceBaseTest {

    private String subject = "signature request";
    private String shortName;
    private WebDriver driver;
    private String pdffillerEmail;
    private String fileToUpload = new File("src/test/resources/uploaders/Constructor.pdf").getAbsolutePath();
    private String pdffillerPassword = TestData.defaultPassword;
    private DaDaDocsFullApp daDaDocsFullApp;
    private DriverWindow salesforceWindow;
    private DriverWindow pdffillerWindow;
    private DriverWinMan driverWinMan;
    private int filesBeforeTests;
    private String recipientEmail;
    private String secondRecipientEmail;
    private String contactURL;

    @Step
    @Parameters({"recipientEmail", "secondRecipientEmail"})
    @BeforeTest
    public void setUp(@Optional("pdf_sendEmail2@support.pdffiller.com") String recipientEmail,
                      @Optional("pd@support.pdffiller.com") String secondRecipientEmail) throws IOException, URISyntaxException {
        this.recipientEmail = recipientEmail;
        this.secondRecipientEmail = secondRecipientEmail;
        driver = getDriver();
        driverWinMan = new DriverWinMan(driver);
        salesforceWindow = driverWinMan.getCurrentWindow();
        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
        SalesAppHomePage salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        ContactsPage contactsPage = salesAppHomePage.openContactsPage();
        ContactConcretePage contactConcretePage = contactsPage.openObjectByNumber(1);
        contactURL = driver.getCurrentUrl();
        daDaDocsFullApp = contactConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullApp();
        filesBeforeTests = daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS).getNumberOfItemsInCurrentTab();
        daDaDocsFullApp.uploadFile(fileToUpload);
        refreshPage();
        daDaDocsFullApp.isOpened();
        daDaDocsFullApp.uploadFile(fileToUpload);
    }

    @Step
    @BeforeMethod
    public void setupEmail() {
        driverWinMan.keepOnlyWindow(salesforceWindow);
        checkTrue(driverWinMan.switchToNewWindow(), "new window is not created or cannot switch to it");
        pdffillerWindow = driverWinMan.getCurrentWindow();
        getUrl(testData.url);
        deleteAllCookies();
        pdffillerEmail = StringMan.makeUniqueEmail(recipientEmail);
        registerUser(pdffillerEmail, pdffillerPassword);
        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
        getUrl(contactURL);
        ContactConcretePage contactConcretePage = new ContactConcretePage(driver);
        contactConcretePage.isOpened();
        daDaDocsFullApp = contactConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullApp();
        daDaDocsFullApp.isOpened();
        daDaDocsFullApp.selectDaDaDocsLastFile();
        daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.RENAME);
        shortName = "name" + StringMan.getRandomString(6);
        daDaDocsFullApp.renamePopUp.renameFile(shortName, false);
    }

    @Story("Sending to user")
    @Test
    public void sendToEachTest() {
        daDaDocsFullApp.selectFileByOrder(1);
        SendToSignPage sendToSignPage = daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.SEND_TO_SIGN);

        String sendType = "SendToEach";
        List<List<String>> recipient = Arrays.asList(Arrays.asList(pdffillerEmail, "name"));

        ImapClient imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        imap.deleteAllMessages();

        sendToSignPage.chooseSendType(sendType);
        sendToSignPage.fillRecipients(recipient);
        sendToSignPage.sendToSign();

        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "Cannot switch to window");
        getUrl(getSignLinkFromEmail(imap));
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
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        String modifiedName = daDaDocsFullApp.getGeneratedFileName(new String[]{shortName}, pdffillerEmail);
        imap.closeFolders();
        ImapClient.closeStore();
        assertTrue(daDaDocsFullApp.isDadaDocsItemPresent(modifiedName), "Item with email is not presented");
    }

    @Story("Sending to group")
    @Test(enabled = false)
    public void sendToGroupTest() {
        daDaDocsFullApp.selectFileByOrder(2);
        daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.RENAME);
        daDaDocsFullApp.renamePopUp.renameFile(shortName, false);
        SendToSignPage sendToSignPage = daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.SEND_TO_SIGN);

        String sendType = "SendToGroup";
        String pdffillerEmail2 = StringMan.makeUniqueEmail(secondRecipientEmail);
        List<List<String>> recipients = Arrays.asList(Arrays.asList(pdffillerEmail2, "name"), Arrays.asList(pdffillerEmail, "name"));

        ImapClient imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        imap.deleteAllMessages();

        ImapClient imap2 = new ImapClient(pdffillerEmail2, pdffillerPassword);
        imap2.deleteAllMessages();
        registerUser(pdffillerEmail2, pdffillerPassword);

        sendToSignPage.chooseSendType(sendType);
        sendToSignPage.clickAddAnotherRecipient();
        sendToSignPage.fillRecipients(recipients);
        sendToSignPage.fillEnvelopeName(shortName);
        sendToSignPage.sendToSign();

        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "Cannot switch to window");
        getUrl(getSignLinkFromEmail(imap));
        NewJSFiller newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        SendToSignSuccessPage sendToSignSuccessPage = new SendToSignSuccessPage(driver);
        sendToSignSuccessPage.isOpened();
        getUrl(testData.url);
        deleteAllCookies();

        getUrl(getSignLinkFromEmail(imap2));
        NewJSFiller newJSFiller2 = new NewJSFiller(driver);
        newJSFiller2.isOpened();
        newJSFiller2.isReady();
        newJSFiller2.clickDoneButton();

        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
        refreshPage();
        daDaDocsFullApp.isOpened();
        imap.closeFolders();
        imap2.closeFolders();
        ImapClient.closeStore();

        assertTrue(daDaDocsFullApp.isDadaDocsItemPresent(shortName + "_" + shortName), "Item with envelope name is not presented");
    }

    @Story("Sending to user merged files")
    @Test
    public void sendToEachMergedFilesTest() {
        daDaDocsFullApp.selectFileByOrder(2);
        daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.RENAME);
        daDaDocsFullApp.renamePopUp.renameFile(shortName, false);
        daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.DOCUMENT_PACKAGES);
        daDaDocsFullApp.selectFilesTab.selectNFirstFiles(2);
        SendToSignPage sendToSignPage = daDaDocsFullApp.selectFilesTab.selectAnAction(SalesforceTestData.DaDaDocsSelectFilesActions.SEND_TO_SIGN);

        String sendType = "SendToEach";
        List<List<String>> recipient = Arrays.asList(Arrays.asList(pdffillerEmail, "name"));

        ImapClient imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        imap.deleteAllMessages();

        sendToSignPage.chooseSendType(sendType);
        sendToSignPage.fillRecipients(recipient);
        sendToSignPage.sendToSign();

        checkTrue(driverWinMan.switchToWindow(pdffillerWindow), "Cannot switch to window");
        getUrl(getSignLinkFromEmail(imap));
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
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        String modifiedName = daDaDocsFullApp.getGeneratedFileName(new String[]{shortName, shortName}, pdffillerEmail);

        imap.closeFolders();
        ImapClient.closeStore();
        assertTrue(daDaDocsFullApp.isDadaDocsItemPresent(modifiedName), "Item with email is not presented");
    }

    @Step
    @AfterTest
    public void cleanUp() {
        try {
            setActiveDriver(driver);
            checkTrue(driverWinMan.switchToWindow(salesforceWindow), "can not switch to salesforce window");
            getUrl(contactURL);
            ContactConcretePage contactConcretePage = new ContactConcretePage(driver);
            contactConcretePage.isOpened();
            daDaDocsFullApp = contactConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullApp();
            DaDaDocsFullApp daDaDocsFullApp = new DaDaDocsFullApp(driver);
            daDaDocsFullApp.isOpened();
            daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
            while (daDaDocsFullApp.getNumberOfItemsInCurrentTab() > filesBeforeTests) {
                daDaDocsFullApp.selectDaDaDocsLastFile();
                daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.DELETE);
                daDaDocsFullApp.deleteFile();
            }
            Logger.warning("Clean up is completed");
        } catch (Exception e) {
            Logger.warning("Clean up is not completed");
        } finally {
            tearDown();
        }
    }

    private String getSignLinkFromEmail(ImapClient imap) {
        List<Message> messages = imap.findMessages(With.subject(subject));

        checkTrue(messages.size() > 0, "No messages in mailbox");
        MessageContent messageContent = new MessageContent(imap, messages.get(0));
        List<String> urls = messageContent.getLinkUrls();
        String urlToSign = urls.get(1);
        checkTrue(urlToSign != null, "No needful link in message");

        return urlToSign;
    }
}
