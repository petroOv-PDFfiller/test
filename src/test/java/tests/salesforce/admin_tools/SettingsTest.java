package tests.salesforce.admin_tools;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.salesforce.app.DaDaDocs.admin_tools.AdminToolsPage;
import pages.salesforce.app.DaDaDocs.admin_tools.popups.SignNowConfirmPopUp;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.AuthorizationTab;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.SettingsTab;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.enums.admin_tools.AdminToolTabs;
import tests.salesforce.SalesforceBaseTest;
import utils.Logger;

import java.util.Map;

import static org.testng.Assert.assertTrue;
import static pages.salesforce.enums.admin_tools.AdminToolFeatureSettings.*;

@Feature("Layouts")
@Listeners({WebTestListener.class, ImapListener.class})
public class SettingsTest extends SalesforceBaseTest {

    private WebDriver driver;
    private AuthorizationTab authorizationPage;
    private String defaultAdminEmail;
    private String defaultAdminPassword;
    private String adminToolUrl;
    private AdminToolsPage adminToolPage;
    private String featuresUpdated = "Your Feature Specific Settings were updated!";
    private SalesAppBasePage salesAppBasePage;
    private boolean isSignNowInstalled;
    private boolean isProfOrg;
    private Map<String, Boolean> settingsBeforeTest;

    @Parameters({"defaultAdminEmail", "defaultAdminPassword", "isSignNowInstalled", "isProfOrg"})
    @BeforeTest
    public void setUp(@Optional("pdf_sf_aqa@support.pdffiller.com") String defaultAdminEmail,
                      @Optional("qwe1rty2") String defaultAdminPassword,
                      @Optional("false") boolean isSignNowInstalled,
                      @Optional("false") boolean isProfOrg) {
        driver = getDriver();
        this.defaultAdminEmail = defaultAdminEmail;
        this.defaultAdminPassword = defaultAdminPassword;
        this.isSignNowInstalled = isSignNowInstalled;
        this.isProfOrg = isProfOrg;
        salesAppBasePage = loginWithDefaultCredentials();
        adminToolPage = salesAppBasePage.openAppLauncher().openAdminToolPage();
        adminToolUrl = driver.getCurrentUrl();
        SettingsTab settingsTab = adminToolPage.openTab(AdminToolTabs.SETTINGS);
        settingsBeforeTest = settingsTab.initSettings();
    }

    @Step
    @BeforeMethod
    public void openPage() {
        getUrl(adminToolUrl);
        adminToolPage.isOpened();
        if (!adminToolPage.isAuthorized()) {
            authorizationPage = adminToolPage.openTab(AdminToolTabs.AUTHORIZATION);
            authorizationPage.openSignIn().enterEmailAndPassword(defaultAdminEmail, defaultAdminPassword)
                    .tryToAuthorize();
            adminToolPage = new AdminToolsPage(driver);
            refreshPage();
            adminToolPage.isOpened();
        }
    }

    @Test
    @Story("The unauthorized administrator shouldn't be able to access 'Settings' tab in the Admin Tools component.")
    public void settingsTabAccess() {
        authorizationPage = adminToolPage.openTab(AdminToolTabs.AUTHORIZATION);
        authorizationPage.logOut();
        assertTrue(authorizationPage.isTabDisabled(AdminToolTabs.SETTINGS));
    }

    @Test
    @Story("System behavior of 'Use custom token management' checkbox")
    public void useCustomTokenManagement() {
        SoftAssert softAssert = new SoftAssert();
        SettingsTab settingsTab = adminToolPage.openTab(AdminToolTabs.SETTINGS);
        Map<String, Boolean> settingsList = settingsTab.initSettings();
        if (settingsList.containsKey(USE_CUSTOM_TOKEN_MANAGEMENT.getName())
                && settingsList.get(USE_CUSTOM_TOKEN_MANAGEMENT.getName())) {
            settingsTab.switchSettingStatus(USE_CUSTOM_TOKEN_MANAGEMENT);
            softAssert.assertTrue(settingsTab.isSaveButtonActive(), "Save button is not active");
            settingsTab.saveChanges();
        }
        softAssert.assertFalse(settingsTab.isTabPresent(AdminToolTabs.AUTHORIZATION));
        settingsTab.switchSettingStatus(USE_CUSTOM_TOKEN_MANAGEMENT);
        softAssert.assertTrue(settingsTab.isSaveButtonActive(), "Save button is not active");
        settingsTab.saveChanges();
        softAssert.assertAll();
    }

    @Test
    @Story("System behavior of 'Store files to external storage' checkbox")
    public void storeFilesToExternalStorage() {
        SoftAssert softAssert = new SoftAssert();
        SettingsTab settingsTab = adminToolPage.openTab(AdminToolTabs.SETTINGS);
        Map<String, Boolean> settingsList = settingsTab.initSettings();
        if (settingsList.get(USE_CHATTER_FILES_PRIOR_TO_ATTACHMENTS.getName())) {
            settingsTab.switchSettingStatus(USE_CHATTER_FILES_PRIOR_TO_ATTACHMENTS);
            softAssert.assertTrue(settingsTab.isSaveButtonActive(), "Save button is not active");
            settingsTab.saveChanges();
        }
        settingsTab.switchSettingStatus(STORE_FILES_TO_EXTERNAL_STORAGE);
        softAssert.assertTrue(settingsTab.isSaveButtonActive(), "Save button is not active");
        settingsTab.saveChanges();
        settingsList = settingsTab.initSettings();
        softAssert.assertTrue(settingsList.get(USE_CHATTER_FILES_PRIOR_TO_ATTACHMENTS.getName()), "Use chatter files setting " +
                "is not checked automatically");
        settingsTab.switchSettingStatus(USE_CHATTER_FILES_PRIOR_TO_ATTACHMENTS);
        softAssert.assertTrue(settingsTab.isSaveButtonActive(), "Save button is not active");
        settingsTab.saveChanges();
        settingsList = settingsTab.initSettings();
        softAssert.assertFalse(settingsList.get(STORE_FILES_TO_EXTERNAL_STORAGE.getName()), "Store files in external storage " +
                "is not unchecked automatically");
        softAssert.assertAll();
    }

    @Test
    @Story("System behavior of 'Store files to external storage' checkbox")
    public void signNow() {
        if (!isSignNowInstalled) {
            SoftAssert softAssert = new SoftAssert();
            SettingsTab settingsTab = adminToolPage.openTab(AdminToolTabs.SETTINGS);
            Map<String, Boolean> settingsList = settingsTab.initSettings();
            if (settingsList.get(GET_YOUR_DOCUMENTS_SIGNED_WITH_SIGN_NOW.getName())) {
                settingsTab.switchSettingStatus(GET_YOUR_DOCUMENTS_SIGNED_WITH_SIGN_NOW);
                softAssert.assertTrue(settingsTab.isSaveButtonActive(), "Save button is not active");
                settingsTab.saveChanges();
                softAssert.assertTrue(settingsTab.notificationMessageContainsText(featuresUpdated),
                        "Wrong notification message is displayed");
            }
            settingsTab.switchSettingStatus(GET_YOUR_DOCUMENTS_SIGNED_WITH_SIGN_NOW);
            SignNowConfirmPopUp confirmPopUp = new SignNowConfirmPopUp(driver);
            confirmPopUp.isOpened();
            settingsTab = confirmPopUp.cancelSignNowInstallation();
            settingsTab.saveChanges();
            settingsList = settingsTab.initSettings();
            softAssert.assertFalse(settingsList.get(GET_YOUR_DOCUMENTS_SIGNED_WITH_SIGN_NOW.getName()), "SignNow " +
                    "setting is activated without package");
            softAssert.assertAll();
        }
    }

    @AfterTest
    public void signInAsDefaultAdmin() {
        try {
            getUrl(adminToolUrl);
            adminToolPage.isOpened();
            authorizationPage = adminToolPage.openTab(AdminToolTabs.AUTHORIZATION);
            authorizationPage.logOut();
            authorizationPage.openSignIn().enterEmailAndPassword(defaultAdminEmail, defaultAdminPassword)
                    .tryToAuthorize();
            AdminToolsPage adminToolPage = new AdminToolsPage(driver);
            refreshPage();
            adminToolPage.isOpened();
            SettingsTab settingsTab = adminToolPage.openTab(AdminToolTabs.SETTINGS);
            settingsTab.setSettingsTo(settingsBeforeTest);
            Logger.info("signInAsDefaultAdmin is completed");
        } catch (Exception e) {
            e.printStackTrace();
            Logger.warning("signInAsDefaultAdmin is not completed");
        } finally {
            tearDown();
        }
    }
}
