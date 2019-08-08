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
import tests.salesforce.SalesforceBaseTest;
import utils.StringMan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.testng.Assert.assertTrue;

@Feature("Rename file test")
@Listeners({WebTestListener.class, ImapListener.class})
public class RenameFileTest extends SalesforceBaseTest {

    private WebDriver driver;
    private String contactName = "Tim Barr";
    private String newName = "newName_" + StringMan.getRandomString(6);
    private String fileToUpload = new File("src/test/resources/uploaders/Constructor.pdf").getAbsolutePath();

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        driver = getDriver();
    }

    @Story("Rename file")
    @Test
    public void renameFileTest() {

        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
        SalesAppHomePage salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        ContactsPage contactsPage = salesAppHomePage.openContactsPage();
        ContactConcretePage contactConcretePage = contactsPage.openObjectByName(contactName);
        DaDaDocsFullApp daDaDocsFullApp = contactConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullApp();
        daDaDocsFullApp.uploadFile(fileToUpload);
        daDaDocsFullApp.selectDaDaDocsLastFile();
        daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.RENAME);
        daDaDocsFullApp.renamePopUp.renameFile(newName, false);
        assertTrue(daDaDocsFullApp.isDadaDocsItemPresent(newName), "Renamed file wasn't appear");
    }
}
