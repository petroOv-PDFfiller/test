package tests.salesforce.airSlate_app;

import api.salesforce.entities.airslate.SetupWizardStage;
import com.airslate.api.models.users.User;
import data.TestData;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.WebTestListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.admin_tools.ASAppAdminToolsPage;
import pages.salesforce.app.airSlate_app.admin_tools.popups.DisconnectWorkspacePopUp;
import pages.salesforce.app.airSlate_app.admin_tools.tabs.AccountTab;
import pages.salesforce.app.airSlate_app.admin_tools.tabs.CustomButtonTab;
import pages.salesforce.app.airSlate_app.admin_tools.tabs.TeammatesTab;
import pages.salesforce.app.airSlate_app.admin_tools.tabs.WorkspaceTab;
import tests.salesforce.SalesforceAirSlateBaseTest;
import utils.StringMan;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static data.salesforce.SalesforceTestData.ASAppErrorMessages.WORKSPACE_SUBDOMAIN_ERROR;
import static data.salesforce.SalesforceTestData.ASAppWorkspaceIndustries.INSURANCE;
import static data.salesforce.SalesforceTestData.ASAppWorkspaceSizes.SIX_FIFTY;
import static data.salesforce.SalesforceTestData.NotificationMessages.WORKSPACE_IS_CONNECTED;
import static data.salesforce.SalesforceTestData.NotificationMessages.WORKSPACE_IS_DISCONNECTED;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static pages.salesforce.enums.admin_tools.ASAppAdminToolTabs.*;

@Feature("airSlate app: workspaces")
@Listeners(WebTestListener.class)
public class ASAppWorkspacesTest extends SalesforceAirSlateBaseTest {

    private static final String WITHOUT = "without";
    private static final String WITH_ANOTHER = "with another";
    private String adminToolURL;
    private ASAppAdminToolsPage adminToolsPage;

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        deleteUserConfigurationByUsername(salesforceDefaultLogin);

        adminUser = createAirSlateAdmin();
        organization = createOrganization(adminUser);
        connectAdmin(adminUser.email);
        setSetupWizardStage(SetupWizardStage.Stage.FINAL);

        adminToolsPage = getAdminToolsPage(salesAppBasePage);
        adminToolURL = getDriver().getCurrentUrl();
    }

    @BeforeMethod
    public void getAdminToolPage() {
        getUrl(adminToolURL);
        adminToolsPage.isOpened();
    }

    @Story("Should connect Workspace")
    @Test
    public void connectWorkspace() {
        disconnectWorkspace();
        refreshPage();
        adminToolsPage.isOpened();
        WorkspaceTab workspacePage = adminToolsPage.openTab(WORKSPACE);
        workspacePage.checkUnconnectedWorkspacePageLabels();
        workspacePage.selectWorkspace(organization.name);
        assertEquals(workspacePage.getNotificationMessage(), WORKSPACE_IS_CONNECTED, "Incorrect notification after connecting workspace");
    }

    @Story("Should create new Workspace")
    @Test
    public void createNewWorkspace() {
        String companyName = "companyName" + StringMan.getRandomString(6);
        String companyDomain = "sf-autotest" + StringMan.getRandomString(5);
        String companySize = SIX_FIFTY;
        String companyIndustry = INSURANCE;

        disconnectWorkspace();
        refreshPage();
        adminToolsPage.isOpened();
        WorkspaceTab workspacePage = adminToolsPage.openTab(WORKSPACE);

        workspacePage.createNewWorkspace()
                .checkCreateWorkspacePageLabels();
        workspacePage.backToChooseWorkspace()
                .checkUnconnectedWorkspacePageLabels();
        workspacePage.createNewWorkspace()
                .finishCreateNewWorkspace();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(workspacePage.isSubdomainErrorIsShown(), "Subdomain error is not shown");
        softAssert.assertEquals(workspacePage.getSubdomainErrorText(), WORKSPACE_SUBDOMAIN_ERROR, "Incorrect error message");

        workspacePage.setCompanySubdomain(companyDomain)
                .setCompanyName(companyName)
                .selectCompanyIndustry(companyIndustry)
                .selectCompanySize(companySize)
                .finishCreateNewWorkspace();

        softAssert.assertTrue(workspacePage.isWorkspaceConnected(), "Workspace is not created");
        Map<String, String> connectedWorkspace = workspacePage.getConnectedWorkspaceInfo();
        softAssert.assertEquals(connectedWorkspace.get("companyName"), companyName, "Incorrect company name is connected");
        softAssert.assertEquals(connectedWorkspace.get("companyIndustry"), companyIndustry, "Incorrect company industry is connected");
        softAssert.assertEquals(connectedWorkspace.get("companySize"), companySize, "Incorrect company size is connected");

        softAssert.assertFalse(workspacePage.isTabLocked(TEAMMATES), "Teammates tab is locked after connect workspace");
        softAssert.assertFalse(workspacePage.isTabLocked(CUSTOM_BUTTONS), "Custom button tab is locked after connect workspace");
        softAssert.assertFalse(workspacePage.isTabLocked(WORKSPACE), "Workspace tab is locked after connect workspace");
        softAssert.assertTrue(workspacePage.isAdminAuthorized(), "Admin is not authorized after connect workspace");
        softAssert.assertAll();
    }


    @Story("Should create first Workspace")
    @Test(priority = 1)
    public void createFirstWorkspace() {
        String companyName = "companyName" + StringMan.getRandomString(6);
        String companyDomain = "sf-autotest" + StringMan.getRandomString(5);

        User user = createAirSlateUser();
        connectAdmin(user.email);
        disconnectWorkspace();
        refreshPage();
        adminToolsPage.isOpened();
        WorkspaceTab workspacePage = adminToolsPage.openTab(WORKSPACE);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(workspacePage.isCreateWorkspaceBlockIsPresent(), "Create workspace block is not displayed");
        softAssert.assertFalse(workspacePage.isExistingWorkspacesListDisplayed(), "Existing workspaces is shown to new user");

        workspacePage.createNewWorkspace()
                .checkCreateWorkspacePageLabels();

        workspacePage.backToChooseWorkspace()
                .checkUnconnectedWorkspacePageLabels();
        softAssert.assertFalse(workspacePage.isExistingWorkspacesListDisplayed(), "Existing workspaces is shown after cancel create");

        workspacePage.createNewWorkspace()
                .setCompanySubdomain(companyDomain)
                .setCompanyName(companyName)
                .finishCreateNewWorkspace();

        Map<String, String> connectedWorkspace = workspacePage.getConnectedWorkspaceInfo();
        softAssert.assertEquals(connectedWorkspace.get("companyName"), companyName, "Incorrect company name is connected");
        softAssert.assertEquals(connectedWorkspace.get("companyIndustry"), "", "Incorrect company industry is connected");
        softAssert.assertEquals(connectedWorkspace.get("companySize"), "", "Incorrect company size is connected");

        softAssert.assertFalse(workspacePage.isTabLocked(TEAMMATES), "Teammates tab is locked after connect workspace");
        softAssert.assertFalse(workspacePage.isTabLocked(CUSTOM_BUTTONS), "Custom button tab is locked after connect workspace");
        softAssert.assertFalse(workspacePage.isTabLocked(WORKSPACE), "Workspace tab is locked after connect workspace");
        softAssert.assertTrue(workspacePage.isAdminAuthorized(), "Admin is not authorized after connect workspace");
        softAssert.assertAll();
    }

    @Story("Should disconnect Workspace")
    @Test(priority = 2)
    public void disconnectWorkspaceTest() {
        WorkspaceTab workspacePage = adminToolsPage.openTab(WORKSPACE);
        workspacePage.checkDisconnectWorkspaceText();

        DisconnectWorkspacePopUp popUp = workspacePage.disconnectWorkspace();
        popUp.checkPopUpBodyText();
        workspacePage = popUp.cancel();

        popUp = workspacePage.disconnectWorkspace();
        workspacePage = popUp.closePopUp(WorkspaceTab.class);
        workspacePage = workspacePage.disconnectWorkspace()
                .disconnect();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(workspacePage.getNotificationMessage(), WORKSPACE_IS_DISCONNECTED,
                "Incorrect notification message after disconnect workspace");
        workspacePage.checkUnconnectedWorkspacePageLabels();

        softAssert.assertTrue(workspacePage.isTabLocked(TEAMMATES), "Teammates tab is unlocked after disconnect workspace");
        softAssert.assertTrue(workspacePage.isTabLocked(CUSTOM_BUTTONS), "Custom button tab is unlocked after disconnect workspace");
        softAssert.assertFalse(workspacePage.isTabLocked(WORKSPACE), "Workspace tab is locked after disconnect workspace");
        softAssert.assertTrue(workspacePage.isAdminAuthorized(), "Admin is not authorized after disconnect workspace");
        softAssert.assertAll();
    }

    @Story("Should allow to disconnect Workspace {0} Admin authorization ")
    @Test(priority = 3)
    public void disconnectWorkspaceAdminAuthorization() {
        String companyName = "companyName" + StringMan.getRandomString(6);
        String companyDomain = "sf-autotest" + StringMan.getRandomString(5);
        String disconnectType = WITHOUT;

        AccountTab accountPage = adminToolsPage.openTab(ACCOUNT);
        if (!accountPage.isAdminAuthorized()) {
            accountPage.setEmail(defaultAirSlateUserEmail)
                    .setPassword(TestData.defaultPassword)
                    .login();
            assertTrue(accountPage.isAdminAuthorized(), "Admin is not authorized");
        }

        WorkspaceTab workspacePage = adminToolsPage.openTab(WORKSPACE);
        if (!workspacePage.isWorkspaceConnected()) {
            workspacePage.createNewWorkspace()
                    .setCompanyName(companyName)
                    .setCompanySubdomain(companyDomain)
                    .finishCreateNewWorkspace();
            assertTrue(workspacePage.isWorkspaceConnected(), "Workspace is not connected");
        }

        accountPage = adminToolsPage.openTab(ACCOUNT);
        accountPage.disconnectAdmin().disconnect();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(workspacePage.isTabLocked(TEAMMATES), "Teammates tab is locked after logout");
        softAssert.assertTrue(workspacePage.isTabLocked(CUSTOM_BUTTONS), "Custom button tab is locked after logout");
        softAssert.assertFalse(workspacePage.isTabLocked(WORKSPACE), "Workspace tab is locked after logout");
        softAssert.assertFalse(workspacePage.isAdminAuthorized(), "Admin is not authorized after logout");

        if (disconnectType.equals(WITH_ANOTHER)) {
            User user = createAirSlateAdmin();
            accountPage.setEmail(user.email)
                    .setPassword(TestData.defaultPassword)
                    .login();

            softAssert.assertTrue(workspacePage.isTabLocked(TEAMMATES), "Teammates tab is locked after new login");
            softAssert.assertTrue(workspacePage.isTabLocked(CUSTOM_BUTTONS), "Custom button tab is locked new login");
            softAssert.assertFalse(workspacePage.isTabLocked(WORKSPACE), "Workspace tab is locked after new login");
        }

        accountPage.checkPermissionIcon(TEAMMATES);
        accountPage.checkPermissionIcon(CUSTOM_BUTTONS);

        TeammatesTab teammatesPage = workspacePage.openTab(TEAMMATES);
        teammatesPage.checkIsTeammatesPageReadOnly();

        CustomButtonTab customButtonPage = teammatesPage.openTab(CUSTOM_BUTTONS);
        customButtonPage.checkIsCustomButtonPageReadOnly();

        workspacePage = customButtonPage.openTab(WORKSPACE);
        softAssert.assertTrue(workspacePage.isWorkspaceConnected(), "Workspace is not connected logout");
        if (disconnectType.equals(WITH_ANOTHER)) {
            workspacePage.disconnectWorkspace()
                    .disconnect();
        } else {
            workspacePage.disconnectWorkspace()
                    .disconnect(AccountTab.class);
        }

        softAssert.assertEquals(adminToolsPage.getNotificationMessage(), WORKSPACE_IS_DISCONNECTED,
                "Incorrect notification after workspace disconnect");
        softAssert.assertTrue(workspacePage.isTabLocked(TEAMMATES), "Teammates tab is unlocked after disconnect workspace");
        softAssert.assertTrue(workspacePage.isTabLocked(CUSTOM_BUTTONS), "Custom button tab is unlocked after disconnect workspace");
        if (disconnectType.equals(WITH_ANOTHER)) {
            softAssert.assertFalse(workspacePage.isTabLocked(WORKSPACE), "Workspace tab is locked after disconnect workspace");
            softAssert.assertTrue(workspacePage.isAdminAuthorized(), "Admin is not authorized after disconnect workspace");
        } else {
            softAssert.assertTrue(workspacePage.isTabLocked(WORKSPACE), "Workspace tab is unlocked after disconnect workspace");
            softAssert.assertFalse(workspacePage.isAdminAuthorized(), "Admin is authorized after disconnect workspace");
        }
        softAssert.assertAll();
    }
}
