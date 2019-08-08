package tests.salesforce.documents;

import core.Browser;
import data.TestData;
import data.salesforce.SalesforceTestData;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.pdffiller.editors.Editor;
import pages.pdffiller.my_docs.MyDocsBasicPage;
import pages.pdffiller.payment.PaidPlan;
import pages.pdffiller.workflow.link_to_fill.LinkToFillWelcomePage;
import pages.salesforce.app.AppLauncherPopUp;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.DaDaDocs.full_app.pop_ups.SharePopUp;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.SalesAppHomePage;
import pages.salesforce.app.sf_objects.contacts.ContactConcretePage;
import pages.salesforce.app.sf_objects.contacts.ContactsPage;
import pages.salesforce.enums.SaleforceMyDocsTab;
import tests.salesforce.SalesforceBaseTest;
import utils.StringMan;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.testng.Assert.assertEquals;

@Feature("Sharing file by link")
@Listeners({WebTestListener.class, ImapListener.class})
public class ShareViaLinkTest extends SalesforceBaseTest {

    private String contactName = "Tim Barr";
    private String subject = "signature request";
    private WebDriver driver;
    private WebDriver pdffillerDriver;
    private String pdffillerEmail;
    private String pdffillerPassword = TestData.defaultPassword;
    private DaDaDocsFullApp daDaDocsFullApp;

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        driver = getDriver();

        pdffillerEmail = StringMan.makeUniqueEmail(testData.emails.get(0));
        setActiveDriver(pdffillerDriver);
        registerUser(pdffillerEmail, pdffillerPassword);
        pdffillerDriver = getDriver(Browser.CHROME, false);
        setActiveDriver(pdffillerDriver);
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

    @Story("Share salesforce item by link")
    @Test
    public void shareLinkTest() {
        int numberOfFiles = daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS).getNumberOfItemsInCurrentTab();
        daDaDocsFullApp.selectDaDaDocsLastFile();
        SharePopUp sharePopUp = daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.SHARE);
        String shareUrl = sharePopUp.copyLink();
        setActiveDriver(pdffillerDriver);
        getUrl(shareUrl);
        Editor editor = new LinkToFillWelcomePage(pdffillerDriver).getEditor();
        editor.clickDoneButton();

        pdffillerDriver.quit();

        setActiveDriver(driver);
        refreshPage();
        daDaDocsFullApp.isOpened();
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        assertEquals(daDaDocsFullApp.getNumberOfItemsInCurrentTab(), numberOfFiles, "Doc was returned to salesforce");
    }
}
