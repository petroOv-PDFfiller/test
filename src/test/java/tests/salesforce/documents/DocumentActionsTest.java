package tests.salesforce.documents;

import api.salesforce.entities.SalesforceObject;
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
import pages.pdffiller.editors.Editor;
import pages.pdffiller.editors.newJSF.NewJSFiller;
import pages.pdffiller.my_docs.MyDocsBasicPage;
import pages.pdffiller.payment.PaidPlan;
import pages.pdffiller.workflow.link_to_fill.LinkToFillWelcomePage;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.DaDaDocs.full_app.pop_ups.SharePopUp;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.accounts.AccountConcretePage;
import pages.salesforce.enums.SaleforceMyDocsTab;
import tests.salesforce.SalesforceBaseTest;
import utils.StringMan;
import utils.TimeMan;

import javax.mail.Message;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static core.check.Check.checkTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


@Feature("Document actions test")
@Listeners({WebTestListener.class, ImapListener.class})
public class DocumentActionsTest extends SalesforceBaseTest {

    private WebDriver driver;
    private String fileToUpload = new File("src/test/resources/uploaders/Constructor.pdf").getAbsolutePath();
    private String dadadocsApp;
    private WebDriver pdffillerDriver;
    private String pdffillerEmail;
    private String recipientEmail;
    private String pdffillerPassword = TestData.defaultPassword;
    private int filesBeforeTest;

    @Parameters({"recipientEmail"})
    @BeforeTest
    public void setUp(@Optional("pd@support.pdffiller.com") String recipientEmail) throws IOException, URISyntaxException {
        driver = getDriver();
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        String accountId = createAccountRecord();
        AccountConcretePage contactConcretePage = salesAppBasePage.openRecordPageById(SalesforceObject.ACCOUNT, accountId);
        DaDaDocsFullApp daDaDocsFullApp = contactConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullApp();
        dadadocsApp = driver.getCurrentUrl();
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        filesBeforeTest = daDaDocsFullApp.getNumberOfItemsInCurrentTab();
        daDaDocsFullApp.uploadFile(fileToUpload);
        this.recipientEmail = recipientEmail;
    }

    @Story("Merge files")
    @Test
    public void mergeFilesInOneTest() {
        String fileName = "name" + StringMan.getRandomString(5);
        setActiveDriver(driver);
        getUrl(dadadocsApp);
        DaDaDocsFullApp daDaDocsFullApp = new DaDaDocsFullApp(driver);
        daDaDocsFullApp.isOpened();
        daDaDocsFullApp.uploadFile(fileToUpload);
        daDaDocsFullApp.selectDaDaDocsLastFile();
        daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.DOCUMENT_PACKAGES);
        daDaDocsFullApp.selectFilesTab.selectNFirstFiles(2);
        daDaDocsFullApp.selectFilesTab.selectAnAction(SalesforceTestData.DaDaDocsSelectFilesActions.MERGE_PDFS);
        daDaDocsFullApp.selectFilesTab.enterNewFilename(fileName);
        daDaDocsFullApp.selectFilesTab.clickOkButton();
        daDaDocsFullApp.isOpened();
        TimeMan.sleep(15);
        refreshPage();
        daDaDocsFullApp.isOpened();
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        assertTrue(daDaDocsFullApp.isDadaDocsItemPresent(fileName), fileName + " is not presented in DaDaDocs");
    }

    @Story("Rename file")
    @Test
    public void renameFileTest() {
        String fileName = "name" + StringMan.getRandomString(5);
        setActiveDriver(driver);
        getUrl(dadadocsApp);
        DaDaDocsFullApp daDaDocsFullApp = new DaDaDocsFullApp(driver);
        daDaDocsFullApp.isOpened();
        daDaDocsFullApp.uploadFile(fileToUpload);
        daDaDocsFullApp.selectDaDaDocsLastFile();
        daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.RENAME);
        daDaDocsFullApp.renamePopUp.renameFile(fileName, false);
        assertTrue(daDaDocsFullApp.isDadaDocsItemPresent(fileName), "Renamed file wasn't appear");
    }

    @Story("Deleting the dadadadocs file")
    @Test
    public void deleteFileTest() {
        String fileName = "name" + StringMan.getRandomString(5);
        setActiveDriver(driver);
        getUrl(dadadocsApp);
        DaDaDocsFullApp daDaDocsFullApp = new DaDaDocsFullApp(driver);
        daDaDocsFullApp.isOpened();
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        daDaDocsFullApp.uploadFile(fileToUpload);
        int numberOfFiles = daDaDocsFullApp.getNumberOfItemsInCurrentTab();
        daDaDocsFullApp.selectDaDaDocsLastFile();
        daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.RENAME);
        daDaDocsFullApp.renamePopUp.renameFile(fileName, false);
        daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.DELETE);
        daDaDocsFullApp.deleteFile();
        assertTrue(!daDaDocsFullApp.isDadaDocsItemPresent(fileName), fileName + " doc was not deleted");
        assertEquals(daDaDocsFullApp.getNumberOfItemsInCurrentTab(), numberOfFiles - 1, fileName + " doc was not deleted");
    }

    @Story("Share by email")
    @Test(enabled = false)
    public void shareByEMailTest() {
        pdffillerDriver = getDriver(Browser.CHROME, false);
        pdffillerEmail = StringMan.makeUniqueEmail(recipientEmail);
        setActiveDriver(pdffillerDriver);
        registerUser(pdffillerEmail, pdffillerPassword);
        MyDocsBasicPage myDocsBasicPage = autoLogin(pdffillerEmail, testData.url);
        autoPay(PaidPlan.BUSINESS_ANNUAL);
        myDocsBasicPage.goToMyDocsPage(testData.url);
        ImapClient imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        imap.deleteAllMessages();

        setActiveDriver(driver);
        getUrl(dadadocsApp);
        DaDaDocsFullApp daDaDocsFullApp = new DaDaDocsFullApp(driver);
        daDaDocsFullApp.isOpened();
        daDaDocsFullApp.uploadFile(fileToUpload);
        int numberOfFiles = daDaDocsFullApp.getNumberOfItems();
        SharePopUp sharePopUp = daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.SHARE);
        sharePopUp.removeAllRecipients();
        sharePopUp.addRecipient(pdffillerEmail, "name");
        sharePopUp.shareViaEmail();
        imap.setMessagesSearchTime(5);
        String shareUrl = getSignLinkFromEmail(imap);
        setActiveDriver(pdffillerDriver);
        getUrl(shareUrl);
        NewJSFiller newJSFiller = new NewJSFiller(pdffillerDriver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickDoneButton();

        pdffillerDriver.quit();

        setActiveDriver(driver);
        refreshPage();
        daDaDocsFullApp.isOpened();
        assertEquals(numberOfFiles, daDaDocsFullApp.getNumberOfItems(), "Doc was returned to salesforce");
        imap.closeFolders();
        ImapClient.closeStore();
    }

    @Story("Share salesforce item by link")
    @Test(enabled = false)
    public void shareLinkTest() {
        pdffillerEmail = StringMan.makeUniqueEmail(recipientEmail);
        pdffillerDriver = getDriver(Browser.CHROME, false);
        setActiveDriver(pdffillerDriver);
        registerUser(pdffillerEmail, pdffillerPassword);
        MyDocsBasicPage myDocsBasicPage = autoLogin(pdffillerEmail, testData.url);
        autoPay(PaidPlan.BUSINESS_ANNUAL);
        myDocsBasicPage.goToMyDocsPage(testData.url);

        setActiveDriver(driver);
        getUrl(dadadocsApp);
        DaDaDocsFullApp daDaDocsFullApp = new DaDaDocsFullApp(driver);
        daDaDocsFullApp.isOpened();
        daDaDocsFullApp.uploadFile(fileToUpload);
        int numberOfFiles = daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS).getNumberOfItemsInCurrentTab();
        daDaDocsFullApp.selectDaDaDocsLastFile();
        SharePopUp sharePopUp = daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.SHARE);
        String shareUrl = sharePopUp.copyLink();
        setActiveDriver(pdffillerDriver);
        getUrl(shareUrl);
        Editor editor = new LinkToFillWelcomePage(pdffillerDriver).getEditor();// infinite check
        editor.clickDoneButton();

        pdffillerDriver.quit();

        setActiveDriver(driver);
        refreshPage();
        daDaDocsFullApp.isOpened();
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        assertEquals(daDaDocsFullApp.getNumberOfItemsInCurrentTab(), numberOfFiles, "Doc was returned to salesforce");
    }

    private String getSignLinkFromEmail(ImapClient imap) {
        String subject = "has shared the document";
        List<Message> messages = imap.findMessages(With.subject(subject));

        checkTrue(messages.size() > 0, "No messages in mailbox");
        MessageContent messageContent = new MessageContent(imap, messages.get(0));
        List<String> urls = messageContent.getLinkUrls();
        checkTrue(urls.size() > 2, "Wrong links count present in message");
        String urlToSign = urls.get(2);
        checkTrue(urlToSign != null && !urlToSign.isEmpty(), "No needful link in message");

        return urlToSign;
    }
}
