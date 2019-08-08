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
import pages.pdffiller.editors.newJSF.TextContentElement;
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
import pages.salesforce.app.sf_objects.cases.CasesPage;
import pages.salesforce.app.sf_objects.contacts.ContactConcretePage;
import pages.salesforce.app.sf_objects.contacts.ContactsPage;
import pages.salesforce.enums.SaleforceMyDocsTab;
import pages.salesforce.enums.SalesTab;
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
import static utils.ImapMan.getSignLinkFromS2SEmail;

@Feature("Check prefill template")
@Listeners({WebTestListener.class, ImapListener.class})
public class ComplexTemplateTest extends SalesforceBaseTest {

    private WebDriver driver;
    private WebDriver pdfFillerDriver;
    private String contactFirstName = "Old";
    private String contactLastName = "Template";
    private DaDaDocsFullApp daDaDocsFullApp;
    private String templateName = "Complex";
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
        setActiveDriver(driver);
        getUrl(daDaDocsFullAppUrl);
        refreshPage();
        daDaDocsFullApp.isOpened();
    }

    @Story("Fill prefill template test")
    @Test
    public void fillComplexTemplate() {
        String caseName = "caseName" + StringMan.getRandomString(4);
        int numberFilesBeforeUpload = daDaDocsFullApp.getNumberOfItemsInCurrentTab();
        TemplatesPage templatesPage = daDaDocsFullApp.openTab(SaleforceMyDocsTab.TEMPLATES);
        DaDaDocsEditor daDaDocsEditor = templatesPage.fillTemplate(templateName);
        daDaDocsEditor.fitPage();
        daDaDocsEditor.newJSFiller.initExistingElements();
        TextContentElement textMappedName = (TextContentElement) NewJSFiller.fillableFields.get(0);
        TextContentElement textCases = (TextContentElement) NewJSFiller.fillableFields.get(2);
        textCases.typeText(caseName);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(textMappedName.text, contactLastName, "Wrong last name prefilled");
        daDaDocsEditor.clickDoneButton();
        daDaDocsFullApp.isOpened();
        DocumentsPage documentsPage = daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        softAssert.assertEquals(documentsPage.getNumberOfItemsInCurrentTab(), numberFilesBeforeUpload + 1, "Number of documents wasn't changed");
        documentsPage.selectAnAction(DELETE);
        documentsPage.deleteFile();
        CasesPage casesPage = daDaDocsFullApp.openTab(SalesTab.CASES);
        softAssert.assertTrue(casesPage.isObjectPresent(caseName), "New cas is not present");
        softAssert.assertAll();
    }

    @Story("Should publish LinkToFill link for prefill template")
    @Test
    public void linkToFillComplexTemplate() {
        String caseName = "caseName" + StringMan.getRandomString(4);
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

        TextContentElement lastNameTextField = (TextContentElement) NewJSFiller.fillableFields.get(0);
        TextContentElement textCases = (TextContentElement) NewJSFiller.fillableFields.get(2);
        textCases.typeText(caseName);
        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        checkTrue(newJSFiller.waitForUrlContains(testData.url, 10), "Pdffiller main page is not opened");
        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");
        driverWinMan.keepOnlyWindow(salesforceWindow);
        softAssert.assertEquals(lastNameTextField.text, contactLastName, "Wrong last name prefilled");
        daDaDocsFullApp.isOpened();
        CasesPage casesPage = daDaDocsFullApp.openTab(SalesTab.CASES);
        softAssert.assertTrue(casesPage.isObjectPresent(caseName), "New cas is not present");
        softAssert.assertAll();
    }

    @Story("Sending to user prefiill template")
    @Test
    public void sendToEachComplexTemplate() {
        String caseName = "caseName" + StringMan.getRandomString(4);
        TemplatesPage templatesPage = daDaDocsFullApp.openTab(SaleforceMyDocsTab.TEMPLATES);
        templatesPage.selectDadadocsItemByFullName(templateName);
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
        TextContentElement textCases = (TextContentElement) NewJSFiller.fillableFields.get(2);
        textCases.typeText(caseName);
        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        SendToSignSuccessPage sendToSignSuccessPage = new SendToSignSuccessPage(driver);
        sendToSignSuccessPage.isOpened();
        setActiveDriver(driver);

        SoftAssert softAssert = new SoftAssert();
        refreshPage();
        daDaDocsFullApp.isOpened();
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        String modifiedName = daDaDocsFullApp.getGeneratedFileName(new String[]{templateName}, pdffillerEmail);
        imap.closeFolders();
        ImapClient.closeStore();
        softAssert.assertTrue(daDaDocsFullApp.isDadaDocsItemPresent(modifiedName), "Item with email is not presented");
        CasesPage casesPage = daDaDocsFullApp.openTab(SalesTab.CASES);
        softAssert.assertTrue(casesPage.isObjectPresent(caseName), "New cas is not present");
        softAssert.assertAll();
    }
}
