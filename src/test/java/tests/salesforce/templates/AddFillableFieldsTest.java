package tests.salesforce.templates;

import data.salesforce.SalesforceTestData;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.salesforce.app.AppLauncherPopUp;
import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
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

@Feature("Add fillable fields in editor test")
@Listeners({WebTestListener.class, ImapListener.class})
public class AddFillableFieldsTest extends SalesforceBaseTest {

    DaDaDocsFullApp daDaDocsFullApp;
    private WebDriver driver;
    private String contactName = "Tim Barr";
    private String newName = "Constructor_" + StringMan.getRandomString(6);
    private String fileToUpload = new File("src/test/resources/uploaders/Constructor.pdf").getAbsolutePath();

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        driver = getDriver();
    }

    @Story("Add fillable field test")
    @Test
    public void addFillableFieldTest() {

        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        AppLauncherPopUp appLauncherPopUp = salesAppBasePage.openAppLauncher();
        SalesAppHomePage salesAppHomePage = appLauncherPopUp.openSalesAppPage();
        ContactsPage contactsPage = salesAppHomePage.openContactsPage();
        ContactConcretePage contactConcretePage = contactsPage.openObjectByName(contactName);

        daDaDocsFullApp = contactConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullApp();
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        int numberFilesBeforeUpload = daDaDocsFullApp.getNumberOfItemsInCurrentTab();
        daDaDocsFullApp.uploadFile(fileToUpload);
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(daDaDocsFullApp.getNumberOfItemsInCurrentTab(), numberFilesBeforeUpload + 1, "New file wasn't upload");

        daDaDocsFullApp.selectDaDaDocsLastFile();
        daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.EDIT_DOCUMENT);
        DaDaDocsEditor daDaDocsEditor = daDaDocsFullApp.renamePopUp.renameFile(newName, true);

        int numberOfFillableFields = daDaDocsEditor.getNumberOfFillableFields();
        daDaDocsEditor.fitPage();
        daDaDocsEditor.addFillableField();
        daDaDocsEditor.clickSaveButton();
        daDaDocsEditor.clickDoneButton();
        daDaDocsEditor.skipLoader();
        daDaDocsFullApp.isOpened();
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        daDaDocsFullApp.selectDadadocsItemByFullName(newName);
        daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.EDIT_DOCUMENT);
        daDaDocsFullApp.renamePopUp.clickOkButton();
        daDaDocsEditor.isOpened();
        softAssert.assertEquals(numberOfFillableFields + 1, daDaDocsEditor.getNumberOfFillableFields(), "Number of fillable fields wasn't changed");
        daDaDocsEditor.clickDoneButton();
        daDaDocsEditor.skipLoader();
        daDaDocsFullApp.isOpened();
        cleanUp();
        softAssert.assertAll();
    }

    public void cleanUp() {
        daDaDocsFullApp.openTab(SaleforceMyDocsTab.DOCUMENTS);
        while (daDaDocsFullApp.getNumberOfItemsInCurrentTab() > 2) {
            daDaDocsFullApp.selectDaDaDocsLastFile();
            daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.DELETE);
            daDaDocsFullApp.deleteFile();
        }
    }
}
