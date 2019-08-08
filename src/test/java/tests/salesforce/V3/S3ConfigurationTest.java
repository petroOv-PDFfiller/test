package tests.salesforce.V3;

import api.salesforce.entities.SalesforceObject;
import api.salesforce.responses.CreateRecordResponse;
import com.amazonaws.regions.Regions;
import core.DriverWinMan;
import core.DriverWindow;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.salesforce.app.DaDaDocs.admin_tools.AdminToolsPage;
import pages.salesforce.app.DaDaDocs.admin_tools.popups.CloudStorageIntegrationPopUp;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.SettingsTab;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsFullAppV3;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.DocumentsTab;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.opportunities.OpportunitiesConcretePage;
import pages.salesforce.enums.V3.DaDaDocsV3Tabs;
import pages.salesforce.enums.admin_tools.AdminToolTabs;
import tests.salesforce.SalesforceBaseTest;
import utils.S3Man;
import utils.StringMan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static core.AllureAttachments.textAttachment;
import static data.salesforce.SalesforceTestData.SalesforceOpportunityStages.PROSPECTING;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static utils.StringMan.getRandomString;


@Feature("S3 integration")
@Listeners({WebTestListener.class})
public class S3ConfigurationTest extends SalesforceBaseTest {

    private String fileToUpload = new File("src/test/resources/uploaders/Constructor.pdf").getAbsolutePath();
    private String fileName = "/Constructor.pdf";
    private WebDriver driver;
    private DriverWinMan driverWinMan;
    private DriverWindow salesforceWindow;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String opportunityId;
    private SalesAppBasePage salesAppBasePage;
    private String setupURL;
    private SettingsTab settingsPage;
    private String accessKey = "AKIAIPG6M665ZMQAWEKA";
    private String secretKey = "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";
    private String bucketName = "dev-salesforce";
    private String pathName = "at";
    private Regions region = Regions.US_EAST_1;

    @BeforeTest
    @Step
    public void setUp() throws IOException, URISyntaxException {
        salesforceApi.auth();
        Map<String, String> opportunityParameters = new HashMap<>();
        String opportunityName = "ATOpp" + StringMan.getRandomString(5);
        opportunityParameters.put("Name", opportunityName);
        opportunityParameters.put("StageName", PROSPECTING);
        opportunityParameters.put("CloseDate", dateFormat.format(new Date()));
        CreateRecordResponse response = salesforceApi.recordsService().createRecord(SalesforceObject.OPPORTUNITY, opportunityParameters);
        opportunityId = response.id;
        textAttachment("Opportunity id: " + opportunityId, opportunityId);

        driver = getDriver();
        driverWinMan = new DriverWinMan(driver);
        salesforceWindow = driverWinMan.getCurrentWindow();
        salesAppBasePage = loginWithDefaultCredentials();
        settingsPage = salesAppBasePage.openDaDaDocsAdminToolPage().openTab(AdminToolTabs.SETTINGS);
        setupURL = driver.getCurrentUrl();
    }

    @Step
    @BeforeMethod
    public void openPage() {
        driverWinMan.keepOnlyWindow(salesforceWindow);
        getUrl(setupURL);
        AdminToolsPage adminToolsPage = new AdminToolsPage(driver);
        adminToolsPage.isOpened();
        settingsPage = adminToolsPage.openTab(AdminToolTabs.SETTINGS);
        if (settingsPage.isStorageAdded()) {
            settingsPage = settingsPage.deleteCloudStorage().deleteStorage();
        }
    }


    @Story("Upload file to S3")
    @Test
    public void uploadFileToS3() {
        CloudStorageIntegrationPopUp cloudStorageIntegrationPopUp = settingsPage.addCloudStorage();
        settingsPage = cloudStorageIntegrationPopUp.setSecretAndAccessKey(secretKey, accessKey)
                .selectRegion(region.getDescription())
                .authorizeInAmazon()
                .setBucketAndPath(bucketName, pathName)
                .saveSettings();
        assertTrue(settingsPage.isStorageAdded(), "Storage is not added");

        OpportunitiesConcretePage concretePage = salesAppBasePage.openRecordPageById(SalesforceObject.OPPORTUNITY, opportunityId);
        DaDaDocsFullAppV3 daDaDocsFullAppV3 = concretePage.switchToDaDaDocsLightningComponent().openDadadocsFullAppV3();
        DocumentsTab documentsTab = daDaDocsFullAppV3.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        S3Man s3Man = new S3Man(accessKey, secretKey, Regions.US_EAST_1);

        SoftAssert softAssert = new SoftAssert();
        s3Man.deleteObject(bucketName, pathName + fileName);
        List<String> keysInBucket = s3Man.getKeysInBucket(bucketName, pathName);
        softAssert.assertFalse(keysInBucket.contains(pathName + fileName), fileName + " file is not deleted");
        documentsTab.uploadFileToS3(fileToUpload);
        keysInBucket = s3Man.getKeysInBucket(bucketName, pathName);
        softAssert.assertTrue(keysInBucket.contains(pathName + fileName), fileName + " file is not uploaded");

        getUrl(setupURL);
        AdminToolsPage adminToolsPage = new AdminToolsPage(driver);
        adminToolsPage.isOpened();
        settingsPage = adminToolsPage.openTab(AdminToolTabs.SETTINGS);
        String newPathName = "at-new";
        settingsPage = settingsPage.changeStorageSettings()
                .authorizeInAmazon()
                .setBucketAndPath(bucketName, newPathName)
                .saveSettings();

        concretePage = salesAppBasePage.openRecordPageById(SalesforceObject.OPPORTUNITY,
                opportunityId
        );
        daDaDocsFullAppV3 = concretePage.switchToDaDaDocsLightningComponent().openDadadocsFullAppV3();
        documentsTab = daDaDocsFullAppV3.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        s3Man.deleteObject(bucketName, newPathName + fileName);
        documentsTab.uploadFileToS3(fileToUpload);
        keysInBucket = s3Man.getKeysInBucket(bucketName, newPathName);
        softAssert.assertTrue(keysInBucket.contains(newPathName + fileName),
                fileName + " file is not uploaded to new bucket");
        softAssert.assertAll();
    }

    @Story("Use wrong credentials to authorize in S3")
    @Test
    public void wrongCredentials() {
        CloudStorageIntegrationPopUp cloudStorageIntegrationPopUp = settingsPage.addCloudStorage();
        settingsPage = cloudStorageIntegrationPopUp.setSecretAndAccessKey(getRandomString(5), getRandomString(10))
                .selectRegion(region.getDescription())
                .authorizeInAmazon()
                .setBucketAndPath(getRandomString(4), pathName)
                .saveSettings();
        assertFalse(settingsPage.isStorageAdded(), "Storage is added");
    }

    @Story("Use special variable as path")
    @Test
    public void uploadFileToS3CustomPath() {
        String pathRecordId = "$sf_record_id";
        String opportunityIdFixed = opportunityId.substring(0, 15);

        CloudStorageIntegrationPopUp cloudStorageIntegrationPopUp = settingsPage.addCloudStorage();
        settingsPage = cloudStorageIntegrationPopUp.setSecretAndAccessKey(secretKey, accessKey)
                .selectRegion(region.getDescription())
                .authorizeInAmazon()
                .setBucketAndPath(bucketName, pathRecordId)
                .saveSettings();
        assertTrue(settingsPage.isStorageAdded(), "Storage is not added");

        OpportunitiesConcretePage concretePage = salesAppBasePage.openRecordPageById(SalesforceObject.OPPORTUNITY, opportunityId);
        DaDaDocsFullAppV3 daDaDocsFullAppV3 = concretePage.switchToDaDaDocsLightningComponent().openDadadocsFullAppV3();
        DocumentsTab documentsTab = daDaDocsFullAppV3.openTab(DaDaDocsV3Tabs.DOCUMENTS_TAB);
        S3Man s3Man = new S3Man(accessKey, secretKey, Regions.US_EAST_1);

        SoftAssert softAssert = new SoftAssert();
        s3Man.deleteObject(bucketName, opportunityIdFixed + fileName);
        List<String> keysInBucket = s3Man.getKeysInBucket(bucketName, opportunityIdFixed);
        softAssert.assertFalse(keysInBucket.contains(opportunityIdFixed + fileName), fileName + " file is not deleted");
        documentsTab.uploadFileToS3(fileToUpload);
        s3Man.getKeysInBucket(bucketName, "").forEach(System.out::println);
        keysInBucket = s3Man.getKeysInBucket(bucketName, opportunityIdFixed);
        softAssert.assertTrue(keysInBucket.contains(opportunityIdFixed + fileName), fileName + " file is not uploaded");
        s3Man.deleteObject(bucketName, opportunityIdFixed + fileName);
        softAssert.assertAll();
    }
}