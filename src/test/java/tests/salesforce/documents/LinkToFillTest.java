package tests.salesforce.documents;

import core.Browser;
import data.TestData;
import data.salesforce.SalesforceTestData;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.pdffiller.editors.newJSF.NewJSFiller;
import pages.pdffiller.my_docs.MyDocsBasicPage;
import pages.pdffiller.payment.PaidPlan;
import pages.pdffiller.workflow.link_to_fill.LinkToFillFinishPage;
import pages.salesforce.app.AppLauncherPopUp;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.DaDaDocs.link_to_fill.LinkToFillActivateTab;
import pages.salesforce.app.DaDaDocs.link_to_fill.LinkToFillCustomizeTab;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.SalesAppHomePage;
import pages.salesforce.app.sf_objects.contacts.ContactConcretePage;
import pages.salesforce.app.sf_objects.contacts.ContactsPage;
import pages.salesforce.enums.SaleforceMyDocsTab;
import tests.salesforce.SalesforceBaseTest;
import utils.Logger;
import utils.StringMan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.testng.Assert.assertEquals;

@Feature("Link to fill feature")
@Listeners({WebTestListener.class, ImapListener.class})
public class LinkToFillTest extends SalesforceBaseTest {

    private String shortName = "name" + StringMan.getRandomString(5);
    private WebDriver driver;
    private WebDriver pdffillerDriver;
    private String pdffillerEmail;
    private String pdffillerPassword = TestData.defaultPassword;
    private DaDaDocsFullApp daDaDocsFullApp;
    private int filesBeforeTests;
    private String dadadocsFullAppURL;
    private String fileToUpload = new File("src/test/resources/uploaders/Constructor.pdf").getAbsolutePath();

    @BeforeMethod
    public void setUp() throws IOException, URISyntaxException {
        driver = getDriver();

        pdffillerEmail = StringMan.makeUniqueEmail(testData.emails.get(1));
        registerUser(pdffillerEmail, pdffillerPassword);
        pdffillerDriver = getDriver(Browser.CHROME, true);

        setActiveDriver(driver);
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
        SalesAppHomePage salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        ContactsPage contactsPage = salesAppHomePage.openContactsPage();
        ContactConcretePage contactConcretePage = contactsPage.openObjectByNumber(1);
        daDaDocsFullApp = contactConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullApp();
        dadadocsFullAppURL = driver.getCurrentUrl();
        filesBeforeTests = daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS).getNumberOfItemsInCurrentTab();
        daDaDocsFullApp.uploadFile(fileToUpload);
        refreshPage();
        daDaDocsFullApp.isOpened();
        daDaDocsFullApp.uploadFile(fileToUpload);
    }

    @Story("Link to fill default test")
    @Test
    public void linkToFillTest() {
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        int numberOfFiles = daDaDocsFullApp.getNumberOfItemsInCurrentTab();
        daDaDocsFullApp.selectDaDaDocsLastFile();
        LinkToFillCustomizeTab customizeTab = daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.LINK_TO_FILL);
        customizeTab.skipStatusMessage();
        LinkToFillActivateTab activateTab = customizeTab.selectTab(SalesforceTestData.LinkToFillTabs.ACTIVATE);
        activateTab.activateLinkToFill(true);
        String linkToFillUrl = activateTab.getLinkToFillUrl();

        setActiveDriver(pdffillerDriver);
        MyDocsBasicPage myDocsBasicPage = autoLogin(pdffillerEmail, testData.url);
        autoPay(PaidPlan.BUSINESS_ANNUAL);
        myDocsBasicPage.goToMyDocsPage(testData.url);

        setActiveDriver(driver);
        activateTab.clickDoneButton();

        setActiveDriver(pdffillerDriver);
        getUrl(linkToFillUrl);
        NewJSFiller newJSFiller = new NewJSFiller(pdffillerDriver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        LinkToFillFinishPage linkToFillFinishPage = new LinkToFillFinishPage(pdffillerDriver);
        linkToFillFinishPage.isOpened();
        pdffillerDriver.quit();

        setActiveDriver(driver);
        refreshPage();
        daDaDocsFullApp.isOpened();
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        assertEquals(daDaDocsFullApp.getNumberOfItemsInCurrentTab(), numberOfFiles + 1, "Doc was returned to salesforce");
    }

    @Story("Link to fill with merged files test")
    @Test
    public void linkToFillWithMergedFilesTest() {
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        int numberOfFiles = daDaDocsFullApp.getNumberOfItemsInCurrentTab();
        daDaDocsFullApp.selectDaDaDocsLastFile();
        daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.DOCUMENT_PACKAGES);
        daDaDocsFullApp.selectFilesTab.selectNFirstFiles(2);
        LinkToFillCustomizeTab customizeTab = daDaDocsFullApp.selectFilesTab.selectAnAction(SalesforceTestData.DaDaDocsSelectFilesActions.LINK_TO_FILL);

        customizeTab.skipStatusMessage();
        LinkToFillActivateTab activateTab = customizeTab.selectTab(SalesforceTestData.LinkToFillTabs.ACTIVATE);
        activateTab.activateLinkToFill(true);
        String linkToFillUrl = activateTab.getLinkToFillUrl();

        setActiveDriver(pdffillerDriver);
        MyDocsBasicPage myDocsBasicPage = autoLogin(pdffillerEmail, testData.url);
        autoPay(PaidPlan.BUSINESS_ANNUAL);
        myDocsBasicPage.goToMyDocsPage(testData.url);

        setActiveDriver(driver);
        activateTab.clickDoneButton();

        setActiveDriver(pdffillerDriver);
        getUrl(linkToFillUrl);
        NewJSFiller newJSFiller = new NewJSFiller(pdffillerDriver);
        newJSFiller.isOpened();
        newJSFiller.isReady();
        newJSFiller.clickLetsGoButton();
        newJSFiller.clickDoneButton();
        newJSFiller.clickLetsGoButton();
        LinkToFillFinishPage linkToFillFinishPage = new LinkToFillFinishPage(pdffillerDriver);
        linkToFillFinishPage.isOpened();
        pdffillerDriver.quit();

        setActiveDriver(driver);
        refreshPage();
        daDaDocsFullApp.isOpened();
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        daDaDocsFullApp.getNumberOfItemsInCurrentTab();
        assertEquals(daDaDocsFullApp.getNumberOfItems(), numberOfFiles + 1, "Doc was returned to salesforce");
    }

    @Step
    @AfterTest
    public void cleanUp() {
        try {
            setActiveDriver(driver);
            getUrl(dadadocsFullAppURL);
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
}
