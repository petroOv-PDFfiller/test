package tests.salesforce.V3;

import api.salesforce.entities.SalesforceObject;
import api.salesforce.responses.CreateRecordResponse;
import core.DriverWinMan;
import core.DriverWindow;
import data.TestData;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import listeners.WebTestListener;
import org.apache.commons.io.FileUtils;
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
import pages.salesforce.app.DaDaDocs.full_app.templates.tabs.x_templates.*;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.opportunities.OpportunitiesConcretePage;
import pages.salesforce.enums.V3.DaDaDocsV3Tabs;
import pages.salesforce.enums.V3.NewTemplateV3Actions;
import tests.salesforce.SalesforceBaseTest;
import utils.FileMan;
import utils.PDFReadMan;
import utils.StringMan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static api.salesforce.entities.SalesforceObject.OPPORTUNITY;
import static core.AllureAttachments.textAttachment;
import static data.salesforce.SalesforceTestData.SalesforceOpportunityStages.PROSPECTING;
import static pages.salesforce.enums.V3.TemplateTabV3Actions.GENERATE_DOCUMENT;
import static pages.salesforce.enums.V3.TemplateTabV3Actions.NEW_TEMPLATE;


@Feature("Docx template v3 tests")
@Listeners({WebTestListener.class})
public class DocxHealthCheckTest extends SalesforceBaseTest {

    private WebDriver driver;
    private String templateToUpload = new File("src/test/resources/docx/Opp.docx").getAbsolutePath();
    private String expectedPDF = new File("src/test/resources/docx/DaDaMosisRender.pdf").getAbsolutePath();
    private String dadadocsApp;
    private String opportunityName = "ATOppHealthCheck";
    private DriverWinMan driverWinMan;
    private DriverWindow salesforceWindow;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String opportunityId;

    @BeforeTest
    @Step
    public void setUp() throws IOException, URISyntaxException {
        salesforceApi.auth();
        Map<String, String> opportunityParameters = new HashMap<>();
        opportunityParameters.put("Name", opportunityName);
        opportunityParameters.put("StageName", PROSPECTING);
        opportunityParameters.put("CloseDate", dateFormat.format(new Date()));
        CreateRecordResponse response = salesforceApi.recordsService().createRecord(OPPORTUNITY, opportunityParameters);
        opportunityId = response.id;
        textAttachment("Opportunity id: " + opportunityId, opportunityId);

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

    @Story("Docx health check")
    @Test
    public void docxRenderHealthCheck() throws IOException {
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
        copyTagsPage.copySelectedTags();
        TemplateUploadPage templateUploadPage = copyTagsPage.navigateToNextTab();
        XTemplateAccessSettingsPage accessSettingsPage = templateUploadPage.uploadTemplate(templateToUpload)
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

        FileUtils.cleanDirectory(new File(TestData.PATH_TO_DOWNLOADS_FOLDER));
        OpportunitiesConcretePage contactConcretePage = documentsTab.openRecordPageById(OPPORTUNITY, opportunityId);
        contactConcretePage.openFilePreview(templateName).downloadFile();

        downloadFileSelenoid(templateName + ".pdf");
        String downloadFilePath = new File(TestData.PATH_TO_DOWNLOADS_FOLDER + "/" + templateName + ".pdf").getAbsolutePath();
        softAssert.assertTrue(FileMan.isFileDownloaded(downloadFilePath), "File is not downloaded");
        softAssert.assertEquals(PDFReadMan.getPdfText(downloadFilePath), PDFReadMan.getPdfText(expectedPDF), "Pdf`s are not equals");
        softAssert.assertAll();
    }
}