package tests.salesforce.templates;

import core.DriverWinMan;
import core.DriverWindow;
import io.qameta.allure.Feature;
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
import pages.salesforce.app.AppLauncherPopUp;
import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.DaDaDocs.full_app.main_tabs.DocumentsPage;
import pages.salesforce.app.DaDaDocs.full_app.main_tabs.TemplatesPage;
import pages.salesforce.app.DaDaDocs.full_app.pop_ups.LinkToFillTemplatePopUp;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.SalesAppHomePage;
import pages.salesforce.app.sf_objects.contacts.ContactConcretePage;
import pages.salesforce.app.sf_objects.contacts.ContactsPage;
import pages.salesforce.enums.SaleforceMyDocsTab;
import tests.salesforce.SalesforceBaseTest;
import utils.StringMan;
import utils.SystemMan;
import utils.TimeMan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static core.check.Check.checkTrue;
import static data.salesforce.SalesforceTestData.DocumentsActions.DELETE;

@Feature("Fill DaDaDoc`s templates test")
@Listeners({WebTestListener.class, ImapListener.class})
public class FillTemplateTest extends SalesforceBaseTest {

    private WebDriver driver;
    private String contactFirstName = "Tim";
    private String contactLastName = "Barr";
    private String contactMobile = "0502909345";
    private DaDaDocsFullApp daDaDocsFullApp;
    private String linkToFillTemplateName = "L2F(create template)";
    private String newContactFirstName = "John";
    private String newContactLastName = StringMan.getRandomString(6);
    private SalesAppHomePage salesAppHomePage;
    private String salesAppHomePageURL;
    private String fileToUpload = new File("src/test/resources/uploaders/Constructor.pdf").getAbsolutePath();

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        driver = getDriver();
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
        salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        salesAppHomePageURL = driver.getCurrentUrl();
    }

    @BeforeMethod
    public void openFullApp() {
        DriverWinMan driverWinMan = new DriverWinMan(driver);
        driverWinMan.keepOnlyWindow(driverWinMan.getCurrentWindow());
        getUrl(salesAppHomePageURL);
        salesAppHomePage.isOpened();
        ContactsPage contactsPage = salesAppHomePage.openContactsPage();
        ContactConcretePage singleContactPage = contactsPage.openObjectByName(contactFirstName + " " + contactLastName);
        daDaDocsFullApp = singleContactPage.switchToDaDaDocsLightningComponent().openDadadocsFullApp();
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
    }

    @Story("Should publish LinkToFill")
    @Test(enabled = false)
    public void fillCreateTemplateTest() {
        SoftAssert softAssert = new SoftAssert();
        TemplatesPage templatesPage = daDaDocsFullApp.openTab(SaleforceMyDocsTab.TEMPLATES);
        LinkToFillTemplatePopUp linkToFillTemplatePopUp = templatesPage.linkToFillTemplate(linkToFillTemplateName);
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
        TextContentElement firstNameTextField = (TextContentElement) NewJSFiller.fillableFields.get(1);

        lastNameTextField.makeActive();
        lastNameTextField.typeText(newContactLastName);

        firstNameTextField.makeActive();
        firstNameTextField.typeText(newContactFirstName);

        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        checkTrue(newJSFiller.waitForUrlContains(testData.url, 10), "Pdffiller main page is not opened");
        driver.close();
        //hardcoded wait to wait for salesforce records update
        TimeMan.sleep(5);
        checkTrue(driverWinMan.switchToWindow(salesforceWindow), "Cannot switch to window");

        ContactsPage contactsPage = new SalesAppHomePage(driver).openContactsPage();
        refreshPage();
        contactsPage.isOpened();
        softAssert.assertTrue(contactsPage.isObjectPresent(newContactFirstName + " " + newContactLastName), "contact isn`t added");
        contactsPage.deleteObjectByName(newContactFirstName + " " + newContactLastName);
        softAssert.assertFalse(contactsPage.isObjectPresent(newContactFirstName + " " + newContactLastName), "contact isn`t deleted");
        softAssert.assertAll();
    }

    @Story("Fill pdf template test")
    @Test(invocationCount = 10)
    public void fillPDFTemplateTest() {
        int numberFilesBeforeUpload = daDaDocsFullApp.getNumberOfItemsInCurrentTab();
        TemplatesPage templatesPage = daDaDocsFullApp.openTab(SaleforceMyDocsTab.TEMPLATES);
        DaDaDocsEditor daDaDocsEditor = templatesPage.fillTemplate("Document 2018-7-26 _ 18_06_07 (copy)");
        daDaDocsEditor.fitPage();
        daDaDocsEditor.newJSFiller.initExistingElements();
        TextContentElement textMappedName = (TextContentElement) NewJSFiller.fillableFields.get(0);
        TextContentElement textMappedPhone = (TextContentElement) NewJSFiller.fillableFields.get(1);
        daDaDocsEditor.clickDoneButton();
        daDaDocsFullApp.isOpened();
        DocumentsPage documentsPage = daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(documentsPage.getNumberOfItemsInCurrentTab(), numberFilesBeforeUpload + 1, "Number of documents wasn't changed");
        softAssert.assertEquals(textMappedName.text, contactFirstName + " " + contactLastName, "Wrong contact name in mapped text field after opening the mapped document");
        softAssert.assertEquals(textMappedPhone.text, contactMobile, "Wrong contact mobile number in mapped text field after opening the mapped document");
        documentsPage.selectAnAction(DELETE);
        documentsPage.deleteFile();
        softAssert.assertAll();
    }

    @Story("Fill nomap template test")
    @Test
    public void fillNoMapTemplateTest() {
        int numberFilesBeforeUpload = daDaDocsFullApp.getNumberOfItemsInCurrentTab();
        TemplatesPage templatesPage = daDaDocsFullApp.openTab(SaleforceMyDocsTab.TEMPLATES);
        DaDaDocsEditor daDaDocsEditor = templatesPage.fillTemplate("nameYnIsY");
        daDaDocsEditor.fitPage();
        daDaDocsEditor.clickDoneButton();
        daDaDocsFullApp.isOpened();
        DocumentsPage documentsPage = daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(documentsPage.getNumberOfItemsInCurrentTab(), numberFilesBeforeUpload + 1, "Number of documents wasn't changed");
        documentsPage.selectAnAction(DELETE);
        documentsPage.deleteFile();
        softAssert.assertAll();
    }
}