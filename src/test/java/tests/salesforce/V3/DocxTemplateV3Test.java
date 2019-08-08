package tests.salesforce.V3;

import api.salesforce.entities.SalesforceObject;
import api.salesforce.responses.CreateRecordResponse;
import core.AssertionException;
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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsFullAppV3;
import pages.salesforce.app.DaDaDocs.full_app.V3.NewTemplatePage;
import pages.salesforce.app.DaDaDocs.full_app.V3.entities.Template;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.DocumentsTab;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.TemplatesTab;
import pages.salesforce.app.DaDaDocs.full_app.templates.popups.CancelTemplateCreationPopUp;
import pages.salesforce.app.DaDaDocs.full_app.templates.tabs.x_templates.*;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.opportunities.OpportunitiesConcretePage;
import pages.salesforce.enums.V3.DaDaDocsV3Tabs;
import pages.salesforce.enums.V3.NewTemplateV3Actions;
import tests.salesforce.SalesforceBaseTest;
import utils.StringMan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static api.salesforce.entities.SalesforceObject.OPPORTUNITY;
import static core.AllureAttachments.textAttachment;
import static core.check.Check.checkTrue;
import static data.TestData.PATH_TO_ACCESSORY_TEST_RESOURCES_FOLDER;
import static data.salesforce.SalesforceTestData.SalesforceOpportunityStages.PROSPECTING;
import static pages.salesforce.enums.V3.TemplateTabV3Actions.GENERATE_DOCUMENT;
import static pages.salesforce.enums.V3.TemplateTabV3Actions.NEW_TEMPLATE;


@Feature("Docx template v3 tests")
@Listeners({WebTestListener.class})
public class DocxTemplateV3Test extends SalesforceBaseTest {

    private final String docxName = "/template.docx";
    private WebDriver driver;
    private String templateToUpload = new File("src/test/resources/docx/Opp.docx").getAbsolutePath();
    private String dadadocsApp;
    private String opportunityName = "ATOpp" + StringMan.getRandomString(5);
    private DriverWinMan driverWinMan;
    private DriverWindow salesforceWindow;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String brokenDocx = new File("src/test/resources/uploaders/broken.docx").getAbsolutePath();
    private String pathToExpectedDocx;
    private String docxFile;

    @BeforeTest
    @Step
    public void setUp() throws IOException, URISyntaxException {
        salesforceApi.auth();
        Map<String, String> opportunityParameters = new HashMap<>();
        opportunityParameters.put("Name", opportunityName);
        opportunityParameters.put("StageName", PROSPECTING);
        opportunityParameters.put("CloseDate", dateFormat.format(new Date()));
        CreateRecordResponse response = salesforceApi.recordsService().createRecord(OPPORTUNITY, opportunityParameters);
        String opportunityId = response.id;
        textAttachment("Opportunity id: " + opportunityId, opportunityId);
        initPathToDocx();
        setupDirectories();

        driver = getDriver();
        driverWinMan = new DriverWinMan(driver);
        salesforceWindow = driverWinMan.getCurrentWindow();
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        OpportunitiesConcretePage contactConcretePage = salesAppBasePage.openRecordPageById(OPPORTUNITY, opportunityId);
        contactConcretePage.switchToDaDaDocsLightningComponent().openDadadocsFullAppV3();
        dadadocsApp = driver.getCurrentUrl();
    }

    @BeforeMethod
    @Step
    public void windowsSetUp() {
        driverWinMan.keepOnlyWindow(salesforceWindow);
    }

    @Story("Docx template creation")
    @Test
    public void createDocxTemplate() throws Docx4JException {
        String templateName = "template" + StringMan.getRandomString(5);
        textAttachment("Template name:" + templateName, templateName);
        String nameTag = "Name";
        String stageTag = "Stage";

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
                .selectStartingObject(OPPORTUNITY.getObjectName())
                .navigateToNextTab();
        relatedParentPage.setRelatedObjectTo(1, SalesforceObject.ACCOUNT.getObjectName());
        RelatedChildPage relatedChildPage = relatedParentPage.navigateToNextTab();
        relatedChildPage.setRelatedObjectTo(1, SalesforceObject.ATTACHMENT.getObjectName());
        CopyTagsPage copyTagsPage = relatedChildPage.navigateToNextTab();
        copyTagsPage.selectTag(nameTag);
        copyTagsPage.selectTag(stageTag);
        String tags = copyTagsPage.copySelectedTags();
        TemplateUploadPage templateUploadPage = copyTagsPage.navigateToNextTab();
        WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
        mainDocumentPart.addParagraphOfText(tags);
        File exportFile = new File(docxFile);
        wordPackage.save(exportFile);
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

    @Story("Check decline template creation")
    @Test
    public void declineDocxTemplateCreate() {
        String templateName = "template" + StringMan.getRandomString(5);
        textAttachment("Template name:" + templateName, templateName);

        getUrl(dadadocsApp);
        DaDaDocsFullAppV3 daDaDocsFullApp = new DaDaDocsFullAppV3(driver);
        daDaDocsFullApp.isOpened();
        TemplatesTab templatesTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.TEMPLATES_TAB);
        NewTemplatePage newTemplatePage = templatesTab.selectAnAction(NEW_TEMPLATE);
        SoftAssert softAssert = new SoftAssert();

        newTemplatePage.selectAnAction(NewTemplateV3Actions.NEW_DOCX_TEMPLATE);
        XTemplateInfoPage infoPage = new XTemplateInfoPage(driver);
        infoPage.isOpened();
        infoPage.setTemplateName(templateName);
        CancelTemplateCreationPopUp cancelTemplateCreationPopUp = infoPage.cancelTemplateCreation();
        cancelTemplateCreationPopUp.cancelCreation(true);
        templatesTab.isOpened();
        softAssert.assertFalse(templatesTab.isTemplatePresent(templateName), "Template created after cancel");
        softAssert.assertAll();
    }

    @Story("Check starting object validation")
    @Test
    public void startingObjectIsNotSelectedDocxTemplate() {
        String templateName = "template" + StringMan.getRandomString(76);
        String templateDescription = "desc" + StringMan.getRandomString(396);
        textAttachment("Template name:" + templateName, templateName);

        getUrl(dadadocsApp);
        DaDaDocsFullAppV3 daDaDocsFullApp = new DaDaDocsFullAppV3(driver);
        daDaDocsFullApp.isOpened();
        TemplatesTab templatesTab = daDaDocsFullApp.openTab(DaDaDocsV3Tabs.TEMPLATES_TAB);
        NewTemplatePage newTemplatePage = templatesTab.selectAnAction(NEW_TEMPLATE);
        SoftAssert softAssert = new SoftAssert();

        newTemplatePage.selectAnAction(NewTemplateV3Actions.NEW_DOCX_TEMPLATE);
        XTemplateInfoPage infoPage = new XTemplateInfoPage(driver);
        infoPage.isOpened();
        infoPage.setTemplateName(templateName + "moreThan80Symbols");
        infoPage.setTemplateDescription(templateDescription + "moreThan400Symbols");
        try {
            infoPage.navigateToNextTab();
        } catch (AssertionException e) {
            infoPage.isOpened();
            softAssert.assertTrue(infoPage.isStartingObjectErrorDisplayed(), "Starting object is not selected error is not present");
        }
        infoPage.selectStartingObject(OPPORTUNITY.getObjectName());
        RelatedParentPage relatedParentPage = infoPage.navigateToNextTab();
        infoPage = relatedParentPage.navigateToPreviousTab();
        softAssert.assertFalse(infoPage.isStartingObjectEditable(), "Starting object is editable");
        softAssert.assertAll();
    }

    @Story("Check starting object validation")
    @Test
    public void brokenDocxTemplate() {
        String templateName = "template" + StringMan.getRandomString(5);
        textAttachment("Template name:" + templateName, templateName);

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
                .selectStartingObject(OPPORTUNITY.getObjectName())
                .navigateToNextTab();
        RelatedChildPage relatedChildPage = relatedParentPage.navigateToNextTab();
        CopyTagsPage copyTagsPage = relatedChildPage.navigateToNextTab();
        TemplateUploadPage templateUploadPage = copyTagsPage.navigateToNextTab();
        templateUploadPage = templateUploadPage.uploadTemplate(brokenDocx)
                .selectRecordForPreview(opportunityName)
                .generatePreview();
        softAssert.assertTrue(templateUploadPage.notificationMessageContainsText("Oops! Something went wrong"),
                "Wrong message is shown");
        XTemplateAccessSettingsPage accessSettingsPage = templateUploadPage.navigateToNextTab();
        accessSettingsPage.navigateToNextTabV3();
        templatesTab.isOpened();
        softAssert.assertTrue(templatesTab.isTemplatePresent(templateName), "Template is not created");
        Template template = templatesTab.selectTemplate(templateName).getSelectedTemplate();
        softAssert.assertNotEquals(template.getModifiedDate(), null, "Date is not set");
        try {
            templatesTab.selectAnAction(GENERATE_DOCUMENT);
        } catch (AssertionException e) {
            daDaDocsFullApp.isOpened();
            templatesTab.isOpened();
        }
        DocumentsTab documentsTab = templatesTab.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        softAssert.assertFalse(documentsTab.isDocumentPresent(templateName), "Document is not generated");
        softAssert.assertAll();
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