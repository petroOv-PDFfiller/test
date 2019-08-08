package tests.salesforce.documents;

import data.salesforce.SalesforceTestData;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.salesforce.app.AppLauncherPopUp;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.SalesAppHomePage;
import pages.salesforce.app.sf_objects.contacts.ContactConcretePage;
import pages.salesforce.app.sf_objects.contacts.ContactsPage;
import pages.salesforce.enums.SaleforceMyDocsTab;
import tests.salesforce.SalesforceBaseTest;
import utils.StringMan;
import utils.TimeMan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.testng.Assert.assertTrue;

@Feature("Merge files test")
@Listeners({WebTestListener.class, ImapListener.class})
public class MergeFilesTests extends SalesforceBaseTest {

    private WebDriver driver;
    private String contactName = "Tim Barr";
    private DaDaDocsFullApp daDaDocsFullApp;
    private String fileName = "name" + StringMan.getRandomString(5);
    private String fileToUpload = new File("src/test/resources/uploaders/Constructor.pdf").getAbsolutePath();

    @BeforeMethod
    public void setUp() throws IOException, URISyntaxException {
        driver = getDriver();

        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
        SalesAppHomePage salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        ContactsPage contactsPage = salesAppHomePage.openContactsPage();
        ContactConcretePage contactConcretePage = contactsPage.openObjectByName(contactName);
        daDaDocsFullApp = contactConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullApp();
        daDaDocsFullApp.uploadFile(fileToUpload);
        daDaDocsFullApp.uploadFile(fileToUpload);
        daDaDocsFullApp.selectDaDaDocsLastFile();
        daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.DOCUMENT_PACKAGES);
    }

    @Story("Merge files")
    @Test
    public void mergeFilesInOneTest() {
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
}
