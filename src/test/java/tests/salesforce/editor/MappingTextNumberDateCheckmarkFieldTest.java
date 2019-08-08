package tests.salesforce.editor;

import api.salesforce.entities.SalesforceObject;
import data.salesforce.SalesforceTestData;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.pdffiller.components.popups.NextWizardPopUp;
import pages.pdffiller.editors.IDocument;
import pages.pdffiller.editors.newJSF.DateContentElement;
import pages.pdffiller.editors.newJSF.NewJSFiller;
import pages.pdffiller.editors.newJSF.TextContentElement;
import pages.pdffiller.editors.newJSF.constructor.ConstructorJSFiller;
import pages.pdffiller.editors.newJSF.constructor.enums.ConstructorTool;
import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.DaDaDocs.editor.constructor.ConstructorDateSaleForce;
import pages.salesforce.app.DaDaDocs.editor.constructor.ConstructorNumberSaleForce;
import pages.salesforce.app.DaDaDocs.editor.constructor.ConstructorTextSaleForce;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.DaDaDocs.full_app.main_tabs.DocumentsPage;
import pages.salesforce.app.DaDaDocs.full_app.main_tabs.TemplatesPage;
import pages.salesforce.app.DaDaDocs.full_app.templates.CreateTemplateWizardPage;
import pages.salesforce.app.DaDaDocs.full_app.templates.tabs.TemplateAccessSettingsPage;
import pages.salesforce.app.DaDaDocs.full_app.templates.tabs.TemplateAddFieldsPage;
import pages.salesforce.app.DaDaDocs.full_app.templates.tabs.TemplateInfoPage;
import pages.salesforce.app.DaDaDocs.full_app.templates.tabs.TemplateMapToPrefillPage;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.accounts.AccountConcretePage;
import pages.salesforce.app.sf_objects.accounts.AccountsPage;
import pages.salesforce.enums.SaleforceMyDocsTab;
import tests.salesforce.SalesforceBaseTest;
import utils.Logger;
import utils.StringMan;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static pages.salesforce.enums.V3.TemplateCreateTabs.ACCESS_SETTINGS;
import static pages.salesforce.enums.V3.TemplateCreateTabs.TEMPLATE_INFO;

@Feature("Mapping: text, number, date, checkmark")
@Listeners({WebTestListener.class})
public class MappingTextNumberDateCheckmarkFieldTest extends SalesforceBaseTest {

    private WebDriver driver;
    private String filePath = new File("src/test/resources/uploaders/Constructor.pdf").getAbsolutePath();
    private IDocument<ConstructorJSFiller, ConstructorTool> document;
    private int pageWidth;
    private int pageHeight;
    private DocumentsPage documents;
    private TemplatesPage templatesPage;
    private String templateName;
    private TemplateAddFieldsPage addFieldsPage;
    private DaDaDocsEditor daDaDocsEditor;
    private String account = "Account ";

    @BeforeTest
    public void setUp() {
        driver = getDriver();
        getUrl(baseUrl);
        //using default account
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        AccountsPage accounts = salesAppBasePage.openObjectPage(SalesforceObject.ACCOUNT);
        AccountConcretePage accountConcretePage = accounts.openAccountName(1);
        documents = accountConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullApp().openTab(SaleforceMyDocsTab.DOCUMENTS);
    }

    @BeforeMethod
    public void uploadNewDocument() {
        documents.uploadFile(filePath);
        templateName = "name" + StringMan.getRandomString(5);
        documents.isOpened();
        CreateTemplateWizardPage template = documents.selectDaDaDocsLastFile().selectAnAction(SalesforceTestData.DocumentsActions.CREATE_TEMPLATE);
        TemplateInfoPage infoPage = template.openTab(TEMPLATE_INFO);
        infoPage.setTemplateName(templateName);
        addFieldsPage = infoPage.navigateToNextTab();
        daDaDocsEditor = addFieldsPage.workWithEditor();
    }

    @Story("Text fields: check mapping the text, phone, url, text with numbers, date")
    @Test
    public void testMappedTextFields() {
        document = new ConstructorJSFiller(driver, daDaDocsEditor.newJSFiller);
        ((ConstructorJSFiller) document).isOpened();
        daDaDocsEditor.newJSFiller.selectZoom("Fit Page");
        pageHeight = (int) daDaDocsEditor.newJSFiller.getPageHeight(1);
        pageWidth = (int) daDaDocsEditor.newJSFiller.getPageWidth(1);


        ConstructorTextSaleForce accountName = (ConstructorTextSaleForce) document.putContent(ConstructorTool.TEXT, pageWidth / 4, pageHeight / 13);
        String accountNameFieldName = accountName.getFieldName();
        ConstructorTextSaleForce accountNumber = (ConstructorTextSaleForce) document.putContent(ConstructorTool.TEXT, pageWidth / 2, pageHeight / 13);
        String accountNumberFieldName = accountNumber.getFieldName();
        ConstructorTextSaleForce accountPhone = (ConstructorTextSaleForce) document.putContent(ConstructorTool.TEXT, pageWidth * 3 / 4, pageHeight / 13);
        String accountPhoneFieldName = accountPhone.getFieldName();
        ConstructorTextSaleForce accountWebsite = (ConstructorTextSaleForce) document.putContent(ConstructorTool.TEXT, pageWidth / 4, pageHeight / 9);
        String accountWebsiteFieldName = accountWebsite.getFieldName();
        daDaDocsEditor.newJSFiller.selectPage(2);
        ConstructorDateSaleForce slaExpirationDate = (ConstructorDateSaleForce) document.putContent(ConstructorTool.DATE, pageWidth / 4, pageHeight / 13);
        String slaExpirationDateFieldName = slaExpirationDate.getFieldName();
        TemplateMapToPrefillPage prefillPage = addFieldsPage.saveTemplate();

        prefillPage.addRecord(account);
        prefillPage.addField();
        prefillPage.mapField(0, "AccountName (String)", accountNameFieldName);
        prefillPage.addField();
        prefillPage.mapField(1, "AccountNumber (String)", accountNumberFieldName);
        prefillPage.addField();
        prefillPage.mapField(2, "AccountPhone (Phone)", accountPhoneFieldName);
        prefillPage.addField();
        prefillPage.mapField(3, "Website (Url)", accountWebsiteFieldName);
        prefillPage.addField();
        prefillPage.mapField(4, "SlaExpirationDate (Date)", slaExpirationDateFieldName);
        prefillPage.saveRecord();

        TemplateAccessSettingsPage settingsPage = prefillPage.openTab(ACCESS_SETTINGS);
        DaDaDocsFullApp daDaDocsFullApp = settingsPage.navigateToNextTab();
        templatesPage = daDaDocsFullApp.openTab(SaleforceMyDocsTab.TEMPLATES);
        templatesPage.selectDadadocsItemByFullName(templateName);
        templatesPage.selectAnAction(SalesforceTestData.TemplatesActions.FILL_TEMPLATE);
        daDaDocsEditor.newJSFiller.initExistingElements();
        assertEquals(NewJSFiller.fillableFields.size(), 5, "Mapped text fields were not saved after saving the document");
        TextContentElement textMappedName = (TextContentElement) NewJSFiller.fillableFields.get(0);
        TextContentElement textMappedNumber = (TextContentElement) NewJSFiller.fillableFields.get(1);
        TextContentElement textMappedPhone = (TextContentElement) NewJSFiller.fillableFields.get(2);
        TextContentElement textMappedWebsite = (TextContentElement) NewJSFiller.fillableFields.get(3);
        DateContentElement dateMapped = (DateContentElement) NewJSFiller.fillableFields.get(4);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(textMappedName.text, "Grand Hotels &amp; Resorts Ltd", "Wrong account name in mapped text field after opening the mapped document");
        softAssert.assertEquals(textMappedNumber.text, "CD439877", "Wrong account number in mapped text field after opening the mapped document");
        softAssert.assertEquals(textMappedPhone.text, "(312) 596-1000", "Wrong account phone in mapped text field after opening the mapped document");
        softAssert.assertEquals(textMappedWebsite.text, "www.grandhotels.com", "Wrong account website in mapped text field after opening the mapped document");
        softAssert.assertEquals(dateMapped.text, "07/04/2018", "Wrong SLA Expiration Date in mapped date field after opening the mapped document");

        softAssert.assertTrue(textMappedName.isElementActive(), "1st text field is not active after opening the mapped document");
        NextWizardPopUp next = new NextWizardPopUp(driver);
        try {
            next.isOpened();
        } catch (Exception e) {
            Logger.error("Next pop up is not present after opening document with mapped text fields");
            softAssert.fail("Next pop up is not present after opening document with mapped text fields");
        }
        softAssert.assertAll();
    }

    @Story("Number fields and checkmark: check mapping the integer digits, float digits, currency")
    @Test
    public void testMappedNumberFields() {
        document = new ConstructorJSFiller(driver, daDaDocsEditor.newJSFiller);
        ((ConstructorJSFiller) document).isOpened();
        daDaDocsEditor.newJSFiller.selectZoom("Fit Page");
        pageHeight = (int) daDaDocsEditor.newJSFiller.getPageHeight(1);
        pageWidth = (int) daDaDocsEditor.newJSFiller.getPageWidth(1);

        ConstructorNumberSaleForce numberLocations = (ConstructorNumberSaleForce) document.putContent(ConstructorTool.NUMBER, pageWidth / 4, pageHeight / 13);
        String numberLocationsFieldName = numberLocations.getFieldName();
        ConstructorNumberSaleForce numberEmployees = (ConstructorNumberSaleForce) document.putContent(ConstructorTool.NUMBER, pageWidth / 2, pageHeight / 13);
        String numberEmployeesFieldName = numberEmployees.getFieldName();
        ConstructorNumberSaleForce numberAnnualRevenue = (ConstructorNumberSaleForce) document.putContent(ConstructorTool.NUMBER, pageWidth * 3 / 4, pageHeight / 13);
        String numberAnnualRevenueFieldName = numberAnnualRevenue.getFieldName();

        TemplateMapToPrefillPage prefillPage = addFieldsPage.saveTemplate();

        prefillPage.addRecord(account);
        prefillPage.addField();
        prefillPage.mapField(0, "NumberOfLocations (Double)", numberLocationsFieldName);
        prefillPage.addField();
        prefillPage.mapField(1, "Employees (Int)", numberEmployeesFieldName);
        prefillPage.addField();
        prefillPage.mapField(2, "AnnualRevenue (Currency)", numberAnnualRevenueFieldName);
        prefillPage.saveRecord();

        TemplateAccessSettingsPage settingsPage = prefillPage.openTab(ACCESS_SETTINGS);
        DaDaDocsFullApp daDaDocsFullApp = settingsPage.navigateToNextTab();
        templatesPage = daDaDocsFullApp.openTab(SaleforceMyDocsTab.TEMPLATES);
        templatesPage.selectDadadocsItemByFullName(templateName);
        templatesPage.selectAnAction(SalesforceTestData.TemplatesActions.FILL_TEMPLATE);
        daDaDocsEditor.newJSFiller.initExistingElements();
        assertEquals(NewJSFiller.fillableFields.size(), 3, "Mapped number fields were not saved after saving the document");
        TextContentElement locations = (TextContentElement) NewJSFiller.fillableFields.get(0);
        TextContentElement employees = (TextContentElement) NewJSFiller.fillableFields.get(1);
        TextContentElement annualRevenue = (TextContentElement) NewJSFiller.fillableFields.get(2);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(locations.text, "57", "Wrong locations number in 1st number field after opening the mapped document");
        softAssert.assertEquals(employees.text, "5600", "Wrong employees number in mapped number field after opening the mapped document");
        softAssert.assertEquals(annualRevenue.text, "500000000", "Wrong annual revenue in mapped number field after opening the mapped document");

        softAssert.assertTrue(locations.isElementActive(), "1st text field is not active after opening the mapped document");
        NextWizardPopUp next = new NextWizardPopUp(driver);
        try {
            next.isOpened();
        } catch (Exception e) {
            Logger.error("Next pop up is not present after opening document with mapped number field");
            softAssert.fail("Next pop up is not present after opening document with mapped number field");
        }
        softAssert.assertAll();
    }

    @AfterMethod
    public void backToDocumentsList() {
        daDaDocsEditor.clickDoneButton();
        templatesPage.isOpened();
        documents.openTab(SaleforceMyDocsTab.DOCUMENTS);
    }
}