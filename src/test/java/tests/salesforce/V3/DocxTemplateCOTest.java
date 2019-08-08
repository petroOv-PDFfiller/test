package tests.salesforce.V3;

import api.salesforce.entities.SalesforceObject;
import api.salesforce.responses.CreateRecordResponse;
import core.DriverWinMan;
import core.DriverWindow;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import listeners.WebTestListener;
import org.apache.commons.io.FileUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsFullAppV3;
import pages.salesforce.app.DaDaDocs.full_app.V3.NewTemplatePage;
import pages.salesforce.app.DaDaDocs.full_app.V3.entities.Template;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.DocumentsTab;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.TemplatesTab;
import pages.salesforce.app.DaDaDocs.full_app.templates.tabs.x_templates.*;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.contacts.ContactConcretePage;
import pages.salesforce.enums.V3.DaDaDocsV3Tabs;
import pages.salesforce.enums.V3.NewTemplateV3Actions;
import tests.salesforce.SalesforceBaseTest;
import utils.StringMan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static api.salesforce.entities.SalesforceObject.CONTACT;
import static core.AllureAttachments.textAttachment;
import static core.check.Check.checkTrue;
import static data.TestData.PATH_TO_ACCESSORY_TEST_RESOURCES_FOLDER;
import static pages.salesforce.enums.V3.TemplateTabV3Actions.GENERATE_DOCUMENT;
import static pages.salesforce.enums.V3.TemplateTabV3Actions.NEW_TEMPLATE;


@Feature("Docx template v3 tests")
@Listeners({WebTestListener.class})
public class DocxTemplateCOTest extends SalesforceBaseTest {

    private final String docxName = "/CustomObjectTemplate.docx";
    private WebDriver driver;
    private String templateToUpload = new File("src/test/resources/docx/Opp.docx").getAbsolutePath();
    private String dadadocsApp;
    private String contactLastName = getUniqueRecordName(CONTACT);
    private String contactFirstName = "ATCFN" + StringMan.getRandomString(5);
    private String corporateOrderName = "ATCO" + StringMan.getRandomString(5);
    private DriverWinMan driverWinMan;
    private DriverWindow salesforceWindow;
    private String pathToExpectedDocx;
    private String docxFile;

    @BeforeTest
    @Step
    public void setUp() throws IOException, URISyntaxException {
        salesforceApi.auth();
        Map<String, String> contactParameters = new HashMap<>();
        contactParameters.put("LastName", contactLastName);
        contactParameters.put("FirstName", contactFirstName);
        CreateRecordResponse response = salesforceApi.recordsService().createRecord(CONTACT, contactParameters);
        String contactID = response.id;
        textAttachment("Contact id: " + contactID, contactID);

        salesforceApi.auth();
        Map<String, String> corporateOrderParameters = new HashMap<>();
        corporateOrderParameters.put("Name", corporateOrderName);
        corporateOrderParameters.put("Contact__c", contactID);
        salesforceApi.recordsService().createRecord(SalesforceObject.CORPORATE_ORDER, corporateOrderParameters);
        initPathToDocx();
        setupDirectories();

        driver = getDriver();
        driverWinMan = new DriverWinMan(driver);
        salesforceWindow = driverWinMan.getCurrentWindow();
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        ContactConcretePage contactConcretePage = salesAppBasePage.openRecordPageById(CONTACT, contactID);
        contactConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullAppV3();
        dadadocsApp = driver.getCurrentUrl();
    }

    @BeforeMethod
    @Step
    public void windowsSetUp() {
        driverWinMan.keepOnlyWindow(salesforceWindow);
    }

    @Story("Docx template with custom object creation")
    @Test
    public void createDocxTemplateWithCustomObject() throws Docx4JException {
        String templateName = "template" + StringMan.getRandomString(5);
        textAttachment("Template name:" + templateName, templateName);
        String firstNameTag = "First Name";
        String lastNameTag = "Last Name";
        String corporateOrderNameTag = "Corporate order Name";

        getUrl(dadadocsApp);
        DaDaDocsFullAppV3 daDaDocsFullApp = new DaDaDocsFullAppV3(driver);
        daDaDocsFullApp.isOpened();
        TemplatesTab templatesTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.TEMPLATES_TAB);
        NewTemplatePage newTemplatePage = templatesTab.selectAnAction(NEW_TEMPLATE);
        SoftAssert softAssert = new SoftAssert();

        newTemplatePage.selectAnAction(NewTemplateV3Actions.NEW_DOCX_TEMPLATE);
        XTemplateInfoPage infoPage = new XTemplateInfoPage(driver);
        infoPage.isOpened();
        RelatedParentPage relatedParentPage = infoPage.setTemplateName(templateName)
                .selectStartingObject(CONTACT.getObjectName())
                .navigateToNextTab();
        relatedParentPage.setRelatedObjectTo(1, SalesforceObject.ACCOUNT.getObjectName());
        RelatedChildPage relatedChildPage = relatedParentPage.navigateToNextTab();
        relatedChildPage.setRelatedObjectTo(1, SalesforceObject.CORPORATE_ORDER.getObjectName());
        CopyTagsPage copyTagsPage = relatedChildPage.navigateToNextTab();
        copyTagsPage.selectTag(firstNameTag);
        copyTagsPage.selectTag(lastNameTag);
        String tags = copyTagsPage.copySelectedTags();
        WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
        mainDocumentPart.addParagraphOfText(tags);

        copyTagsPage.selectObjeсtFields(SalesforceObject.CORPORATE_ORDER.getObjectName());
        copyTagsPage.selectTag(corporateOrderNameTag);
        tags = copyTagsPage.copySelectedTags();
        mainDocumentPart.addParagraphOfText(tags);

        File exportFile = new File(docxFile);
        wordPackage.save(exportFile);
        TemplateUploadPage templateUploadPage = copyTagsPage.navigateToNextTab();
        XTemplateAccessSettingsPage accessSettingsPage = templateUploadPage.uploadTemplate(docxFile)
                .skipPreview()
                .navigateToNextTab();

        accessSettingsPage.navigateToNextTabV3();
        templatesTab.isOpened();
        softAssert.assertTrue(templatesTab.isTemplatePresent(templateName), "Template is not created");
        Template template = templatesTab.selectTemplate(templateName).getSelectedTemplate();
        softAssert.assertNotEquals(template.getModifiedDate(), null, "Date is not set");
        DaDaDocsEditor daDaDocsEditor = templatesTab.selectAnAction(GENERATE_DOCUMENT);
        daDaDocsEditor.clickDoneButton();
        daDaDocsFullApp.isOpened();
        DocumentsTab documentsTab = new DocumentsTab(driver);
        documentsTab.isOpened();
        softAssert.assertTrue(documentsTab.isDocumentPresent(templateName), "Document is not generated");
        softAssert.assertAll();
    }

    @AfterTest
    public void deleteCorporateOrders() throws IOException, URISyntaxException {
        String corporateOrderQuery = "SELECT id FROM " + SalesforceObject.CORPORATE_ORDER.getAPIName() +
                " Where Name LIKE 'ATCO%'";

        if (!salesforceApi.isAuthorized()) {
            salesforceApi.auth();
        }

        clearRecords(SalesforceObject.CORPORATE_ORDER);
    }

    private void setupDirectories() throws IOException {
        if (!new File(pathToExpectedDocx).exists()) {
            checkTrue(new File(pathToExpectedDocx).mkdirs(), "Folder for expected layouts was not created");
        }
        if (Files.exists(Paths.get(pathToExpectedDocx))) {
            FileUtils.cleanDirectory(new File(pathToExpectedDocx));
        }
    }

    private void initPathToDocx() {
        pathToExpectedDocx = new File(PATH_TO_ACCESSORY_TEST_RESOURCES_FOLDER + "/files/salesforce/docx").getAbsolutePath();
        docxFile = pathToExpectedDocx + docxName;
    }
}