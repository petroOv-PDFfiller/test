package tests.salesforce.documents;

import core.Browser;
import data.TestData;
import data.salesforce.SalesforceTestData;
import imap.ImapClient;
import imap.With;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.pdffiller.editors.newJSF.NewJSFiller;
import pages.pdffiller.my_docs.MyDocsBasicPage;
import pages.pdffiller.payment.PaidPlan;
import pages.salesforce.app.AppLauncherPopUp;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.DaDaDocs.full_app.pop_ups.SharePopUp;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.SalesAppHomePage;
import pages.salesforce.app.sf_objects.contacts.ContactConcretePage;
import pages.salesforce.app.sf_objects.contacts.ContactsPage;
import tests.salesforce.SalesforceBaseTest;
import utils.Logger;
import utils.StringMan;

import javax.mail.Message;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static core.check.Check.checkTrue;
import static org.testng.Assert.assertEquals;

@Feature("Sharing file by email")
@Listeners({WebTestListener.class, ImapListener.class})
public class ShareByEmailTest extends SalesforceBaseTest {

    private String contactName = "Tim Barr";
    private String subject = "has shared the document";
    private WebDriver driver;
    private WebDriver pdffillerDriver;
    private String pdffillerEmail;
    private String pdffillerPassword = TestData.defaultPassword;
    private DaDaDocsFullApp daDaDocsFullApp;

    @BeforeMethod
    public void setUp() throws IOException, URISyntaxException {
        driver = getDriver();

        pdffillerDriver = getDriver(Browser.CHROME, false);
        pdffillerEmail = StringMan.makeUniqueEmail(testData.emails.get(0));
        setActiveDriver(pdffillerDriver);
        registerUser(pdffillerEmail, pdffillerPassword);
        MyDocsBasicPage myDocsBasicPage = autoLogin(pdffillerEmail, testData.url);
        autoPay(PaidPlan.BUSINESS_ANNUAL);
        myDocsBasicPage.goToMyDocsPage(testData.url);


        setActiveDriver(driver);
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
        SalesAppHomePage salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        ContactsPage contactsPage = salesAppHomePage.openContactsPage();
        ContactConcretePage contactConcretePage = contactsPage.openObjectByName(contactName);
        daDaDocsFullApp = contactConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullApp();
    }

    @Story("Share by email")
    @Test
    public void shareByEMailTest() {
        ImapClient imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        imap.deleteAllMessages();
        daDaDocsFullApp.selectDaDaDocsLastFile();
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

    private String getSignLinkFromEmail(ImapClient imap) {
        List<Message> messages = imap.findMessages(With.subject(subject));

        checkTrue(messages.size() > 0, "No messages in mailbox");
        String messageContent = imap.getContent(messages.get(0));
        int a = messageContent.indexOf("<a href=3Dhttps://pdf.ac") +
                "<a href=3D".length();
        int b = messageContent.indexOf(" target=3D", a);

        String urlToSign = messageContent.substring(a, b);
        Logger.info("Url to sign " + urlToSign);
        checkTrue(!urlToSign.equals(""), "No needful link in message");

        return urlToSign;
    }
}
