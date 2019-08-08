package tests.salesforce.documents;

import data.salesforce.SalesforceTestData;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeTest;
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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Feature("Delete file test")
@Listeners({WebTestListener.class, ImapListener.class})
public class DeleteFileTest extends SalesforceBaseTest {

    private WebDriver driver;
    private String contactName = "Tim Barr";
    private String fileToUpload = new File("src/test/resources/uploaders/Constructor.pdf").getAbsolutePath();

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        driver = getDriver();
    }

    @Story("Deleting the dadadadocs file")
    @Test
    public void deleteFileTest() {
        String newName = "newName_" + StringMan.getRandomString(6);

        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
        SalesAppHomePage salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        ContactsPage contactsPage = salesAppHomePage.openContactsPage();
        ContactConcretePage contactConcretePage = contactsPage.openObjectByName(contactName);
        DaDaDocsFullApp daDaDocsFullApp = contactConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullApp()
                .openTab(SaleforceMyDocsTab.DOCUMENTS);
        daDaDocsFullApp.uploadFile(fileToUpload);
        int numberOfFiles = daDaDocsFullApp.getNumberOfItemsInCurrentTab();
        daDaDocsFullApp.selectDaDaDocsLastFile();
        daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.RENAME);
        daDaDocsFullApp.renamePopUp.renameFile(newName, false);
        daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.DELETE);
        daDaDocsFullApp.deleteFile();
        assertTrue(!daDaDocsFullApp.isDadaDocsItemPresent(newName), newName + " doc was not deleted");
        assertEquals(daDaDocsFullApp.getNumberOfItemsInCurrentTab(), numberOfFiles - 1, newName + " doc was not deleted");
    }
}
