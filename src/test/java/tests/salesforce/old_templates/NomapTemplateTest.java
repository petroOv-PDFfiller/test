package tests.salesforce.old_templates;

import core.Browser;
import core.DriverWinMan;
import core.DriverWindow;
import data.TestData;
import imap.ImapClient;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.pdffiller.editors.newJSF.NewJSFiller;
import pages.pdffiller.workflow.send_to_sign.SendToSignSuccessPage;
import pages.salesforce.app.AppLauncherPopUp;
import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.DaDaDocs.full_app.main_tabs.DocumentsPage;
import pages.salesforce.app.DaDaDocs.full_app.main_tabs.TemplatesPage;
import pages.salesforce.app.DaDaDocs.full_app.pop_ups.LinkToFillTemplatePopUp;
import pages.salesforce.app.DaDaDocs.send_to_sign.SendToSignPage;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.SalesAppHomePage;
import pages.salesforce.app.sf_objects.contacts.ContactConcretePage;
import pages.salesforce.app.sf_objects.contacts.ContactsPage;
import pages.salesforce.enums.SaleforceMyDocsTab;
import tests.salesforce.SalesforceBaseTest;
import utils.StringMan;
import utils.SystemMan;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static core.check.Check.checkTrue;
import static data.salesforce.SalesforceTestData.DocumentsActions.DELETE;
import static data.salesforce.SalesforceTestData.TemplatesActions.SEND_TO_SIGN;
import static org.testng.Assert.assertTrue;
import static utils.ImapMan.getSignLinkFromS2SEmail;

@Feature("Check nomap template")
@Listeners({WebTestListener.class, ImapListener.class})
public class NomapTemplateTest extends SalesforceBaseTest {

    private WebDriver driver;
    private WebDriver pdfFillerDriver;
    private String contactFirstName = "Old";
    private String contactLastName = "Template";
    private DaDaDocsFullApp daDaDocsFullApp;
    private String templateName = "Nomap";
    private SalesAppHomePage salesAppHomePage;
    private String daDaDocsFullAppUrl;
    private String recipientEmail;
    private String pdffillerPassword = TestData.defaultPassword;
    private String subject = "signature request";

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        this.recipientEmail = testData.emails.get(1);
        driver = getDriver();
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
        salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        ContactsPage contactsPage = salesAppHomePage.openContactsPage();
        ContactConcretePage singleContactPage = contactsPage.openObjectByName(contactFirstName + " " + contactLastName);
        daDaDocsFullApp = singleContactPage.switchToDaDaDocsLightningComponent().openDadadocsFullApp();
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        daDaDocsFullAppUrl = driver.getCurrentUrl();
    }

    @Step
    @BeforeMethod
    public void openFullApp() {
        getUrl(daDaDocsFullAppUrl);
        refreshPage();
        daDaDocsFullApp.isOpened();
    }

    @Story("Fill nomap template test")
    @Test
    public void fillNomapTemplateTest() {
        int numberFilesBeforeUpload = daDaDocsFullApp.getNumberOfItemsInCurrentTab();
        TemplatesPage templatesPage = daDaDocsFullApp.openTab(SaleforceMyDocsTab.TEMPLATES);
        DaDaDocsEditor daDaDocsEditor = templatesPage.fillTemplate(templateName);
        daDaDocsEditor.fitPage();
        daDaDocsEditor.newJSFiller.initExistingElements();
        SoftAssert softAssert = new SoftAssert();
        daDaDocsEditor.clickDoneButton();
        daDaDocsFullApp.isOpened();
        DocumentsPage documentsPage = daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        softAssert.assertEquals(documentsPage.getNumberOfItemsInCurrentTab(), numberFilesBeforeUpload + 1, "Number of documents wasn't changed");
        documentsPage.selectAnAction(DELETE);
        documentsPage.deleteFile();
        softAssert.assertAll();
    }

    @Story("Should publish LinkToFill link for nomap template")
    @Test
    public void linkToFillNomapTemplate() {
        SoftAssert softAssert = new SoftAssert();
        TemplatesPage templatesPage = daDaDocsFullApp.openTab(SaleforceMyDocsTab.TEMPLATES);
        LinkToFillTemplatePopUp linkToFillTemplatePopUp = templatesPage.linkToFillTemplate(templateName);
        linkToFillTemplatePopUp.generateLinkToFill().isOpened();
        linkToFillTemplatePopUp.copyLinkToFill();
        daDaDocsFullApp.isOpened();
        DriverWinMan driverWinMan = new DriverWinMan(driver);
        DriverWindow salesforceWindow = driverWinMan.getCurrentWindow();
        checkTrue(driverWinMan.switchToNewWindow(), "New window is not created or cannot switch to it");
        getUrl(SystemMan.getClipboardValue());
        NewJSFiller newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.initExistingElements();
        softAssert.assertEquals(NewJSFiller.fillableFields.size(), 3, "Wrong number of fields");
        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        checkTrue(newJSFiller.waitForUrlContains(testData.url, 10), "Pdffiller main page is not opened");
        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
        softAssert.assertAll();
    }

    @Story("Sending to user nomap template")
    @Test
    public void sendToEachNomapTemplate() {
        TemplatesPage templatesPage = daDaDocsFullApp.openTab(SaleforceMyDocsTab.TEMPLATES);
        SendToSignPage sendToSignPage = templatesPage.selectAnAction(SEND_TO_SIGN);

        String sendType = "SendToEach";
        String pdffillerEmail = StringMan.makeUniqueEmail(recipientEmail);
        List<List<String>> recipient = Arrays.asList(Arrays.asList(pdffillerEmail, "name"));

        ImapClient imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        imap.deleteAllMessages();

        sendToSignPage.chooseSendType(sendType);
        sendToSignPage.fillRecipients(recipient);
        sendToSignPage.sendToSign();
        pdfFillerDriver = getDriver(Browser.CHROME, false);
        setActiveDriver(pdfFillerDriver);
        getUrl(getSignLinkFromS2SEmail(imap, subject));
        NewJSFiller newJSFiller = new NewJSFiller(driver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        SendToSignSuccessPage sendToSignSuccessPage = new SendToSignSuccessPage(driver);
        sendToSignSuccessPage.isOpened();
        setActiveDriver(driver);
        pdfFillerDriver.quit();
        refreshPage();
        daDaDocsFullApp.isOpened();
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        String modifiedName = daDaDocsFullApp.getGeneratedFileName(new String[]{templateName}, pdffillerEmail);
        imap.closeFolders();
        ImapClient.closeStore();
        assertTrue(daDaDocsFullApp.isDadaDocsItemPresent(modifiedName), "Item with email is not presented");
    }
}
