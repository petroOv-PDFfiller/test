package tests.salesforce.templates;

import api.salesforce.entities.SalesforceObject;
import api.salesforce.responses.CreateRecordResponse;
import data.salesforce.SalesforceTestData;
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
import pages.pdffiller.editors.IDocument;
import pages.pdffiller.editors.newJSF.constructor.ConstructorJSFiller;
import pages.pdffiller.editors.newJSF.constructor.enums.ConstructorTool;
import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.DaDaDocs.editor.constructor.ConstructorTextSaleForce;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.DaDaDocs.full_app.main_tabs.TemplatesPage;
import pages.salesforce.app.DaDaDocs.full_app.pop_ups.TemplateInfoPopUp;
import pages.salesforce.app.DaDaDocs.full_app.templates.CreateTemplateWizardPage;
import pages.salesforce.app.DaDaDocs.full_app.templates.entities.PermissionGroup;
import pages.salesforce.app.DaDaDocs.full_app.templates.entities.Record;
import pages.salesforce.app.DaDaDocs.full_app.templates.tabs.*;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.accounts.AccountConcretePage;
import pages.salesforce.enums.SaleforceMyDocsTab;
import pages.salesforce.enums.V3.TemplateCreateTabs;
import tests.salesforce.SalesforceBaseTest;
import utils.StringMan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

@Feature("Complex template")
@Listeners({WebTestListener.class, ImapListener.class})
public class CreateComplexTemplateTest extends SalesforceBaseTest {

    private static final String CONTACT_FOR_CONTACTS = "Contact (for Contacts)";
    private static final String ACCOUNT = "Account ";
    private WebDriver driver;
    private DaDaDocsFullApp daDaDocsFullApp;
    private String templateName = "name" + StringMan.getRandomString(96);
    private String templateDescription = "desc" + StringMan.getRandomString(396);
    private String fileToUpload = new File("src/test/resources/uploaders/Constructor.pdf").getAbsolutePath();
    private String homePageURL;
    private String accountName = "ATAcc" + StringMan.getRandomString(5);
    private String accountId;
    private SalesAppBasePage salesAppBasePage;

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        Map<String, String> accountParameters = new HashMap<>();
        accountParameters.put("Name", accountName);
        CreateRecordResponse response = salesforceApi.recordsService().createRecord(SalesforceObject.ACCOUNT, accountParameters);
        accountId = response.id;

        driver = getDriver();
        salesAppBasePage = loginWithDefaultCredentials();
        homePageURL = driver.getCurrentUrl();
    }

    @BeforeMethod
    public void openFullApp() {
        getUrl(homePageURL);
        salesAppBasePage.isOpened();
        AccountConcretePage contactConcretePage = salesAppBasePage.openRecordPageById(SalesforceObject.ACCOUNT, accountId);
        daDaDocsFullApp = contactConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullApp();
    }

    @Test
    @Story("Complex template creation test")
    public void createComplexTemplateTest() {
        daDaDocsFullApp.uploadFile(fileToUpload);
        daDaDocsFullApp.selectDaDaDocsLastFile();

        CreateTemplateWizardPage template = daDaDocsFullApp.selectAnAction(SalesforceTestData.DocumentsActions.CREATE_TEMPLATE);
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        TemplateInfoPage infoPage = template.openTab(TemplateCreateTabs.TEMPLATE_INFO);
        infoPage.setTemplateName(templateName + StringMan.getRandomString(4));
        infoPage.setTemplateDescription(templateDescription + StringMan.getRandomString(4));
        TemplateAddFieldsPage addFieldsPage = infoPage.navigateToNextTab();
        DaDaDocsEditor daDaDocsEditor = addFieldsPage.workWithEditor();
        IDocument<ConstructorJSFiller, ConstructorTool> document = new ConstructorJSFiller(driver, daDaDocsEditor.newJSFiller);
        ((ConstructorJSFiller) document).isOpened();
        daDaDocsEditor.fitPage();
        int pageHeight = (int) daDaDocsEditor.newJSFiller.getPageHeight(1);
        int pageWidth = (int) daDaDocsEditor.newJSFiller.getPageWidth(1);
        ConstructorTextSaleForce accountName = (ConstructorTextSaleForce) document.putContent(ConstructorTool.TEXT, pageWidth / 4, pageHeight / 13);
        String accountNameFieldName = accountName.getFieldName();
        ConstructorTextSaleForce accountNumber = (ConstructorTextSaleForce) document.putContent(ConstructorTool.TEXT, pageWidth / 2, pageHeight / 13);
        String accountNumberFieldName = accountNumber.getFieldName();
        ConstructorTextSaleForce accountDescription = (ConstructorTextSaleForce) document.putContent(ConstructorTool.TEXT, pageWidth / 4, pageHeight / 9);
        String accountDescriptionFieldName = accountDescription.getFieldName();
        ConstructorTextSaleForce lastName = (ConstructorTextSaleForce) document.putContent(ConstructorTool.TEXT, pageWidth / 4, pageHeight / 5);
        String lastNameFieldName = lastName.getFieldName();

        TemplateMapToPrefillPage prefillPage = addFieldsPage.saveTemplate();
        prefillPage.isOpened();
        prefillPage.addRecord(ACCOUNT);
        prefillPage.addField();
        prefillPage.mapField(0, "AccountName (String)", accountNameFieldName);
        prefillPage.addField();
        prefillPage.mapField(1, "AccountNumber (String)", accountNumberFieldName);
        prefillPage.saveRecord();
        List<Record> prefillMapping = prefillPage.initRecords();
        TemplateMapToUpdatePage mapToUpdatePage = prefillPage.navigateToNextTab();
        mapToUpdatePage.addRecord(ACCOUNT);
        mapToUpdatePage.addField();
        mapToUpdatePage.mapField(0, "AccountDescription (Textarea)", accountDescriptionFieldName);
        mapToUpdatePage.saveRecord();
        List<Record> updateMappingBeforeCloning = mapToUpdatePage.initRecords();
        assertNotEquals(prefillMapping, updateMappingBeforeCloning, "Update mapping before cloning and prefill" +
                " mappings is equals");
        mapToUpdatePage.clickOnCopyPrefill().cancelCopying();
        List<Record> updateMapping = mapToUpdatePage.initRecords();
        assertNotEquals(prefillMapping, updateMapping, "Update and prefill mappings is equals");
        assertEquals(updateMappingBeforeCloning, updateMapping, "Update mapping before cloning and Update" +
                " mapping ater cancel cloning mappings is not equals");
        mapToUpdatePage.clickOnCopyPrefill().acceptCopying();
        updateMapping = mapToUpdatePage.initRecords();
        assertEquals(prefillMapping, updateMapping, "Update mapping after cloning and prefill mapping is not equals");
        TemplateMapToCreatePage mapToCreatePage = mapToUpdatePage.navigateToNextTab();
        mapToCreatePage.addRecord(CONTACT_FOR_CONTACTS);
        mapToCreatePage.mapField(0, "Last Name", lastNameFieldName);
        mapToCreatePage.saveRecord();
        TemplateAccessSettingsPage accessSettingsPage = mapToCreatePage.navigateToNextTab();
        List<PermissionGroup> permissionGroups = accessSettingsPage.initPermissionGroups();
        SoftAssert softAssert = new SoftAssert();
        for (PermissionGroup permissionGroup : permissionGroups) {
            if (permissionGroup.getGroupName().equalsIgnoreCase("Profile") ||
                    permissionGroup.getGroupName().equalsIgnoreCase("Role")) {
                for (int j = 0; j < permissionGroup.getPermissions().size(); j++) {
                    if (permissionGroup.getPermissions().get(j).getName().equals("DaDaDocs Set")) {
                        softAssert.assertTrue(permissionGroup.getPermissions().get(j).isChecked(),
                                "Permission " + permissionGroup.getPermissions().get(j).getName() + " is not selected");
                    } else {
                        softAssert.assertTrue(!permissionGroup.getPermissions().get(j).isChecked(),
                                "Permission " + permissionGroup.getPermissions().get(j).getName() + " is selected");
                    }
                }
            }
        }
        accessSettingsPage.navigateToNextTab();
        driver.manage().timeouts().implicitlyWait(IMPLICITLY_WAIT_SECONDS, TimeUnit.SECONDS);
        TemplatesPage templatesPage = daDaDocsFullApp.openTab(SaleforceMyDocsTab.TEMPLATES);
        assertTrue(daDaDocsFullApp.isDadaDocsItemPresent(templateName), "Complex template is not present");
        TemplateInfoPopUp templateInfoPopUp = templatesPage.getTemplateInfo(templateName);
        softAssert.assertEquals(templateInfoPopUp.getTemplateName(), templateName, "Wrong template Name");
        softAssert.assertEquals(templateInfoPopUp.getTemplateDescription(), templateDescription, "Wrong template description");
        daDaDocsFullApp = templateInfoPopUp.closePopUp();
        softAssert.assertAll();
    }
}
