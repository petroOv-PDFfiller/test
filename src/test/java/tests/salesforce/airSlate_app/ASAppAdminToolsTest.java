package tests.salesforce.airSlate_app;

import api.salesforce.entities.airslate.SetupWizardStage;
import data.TestData;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.admin_tools.ASAppAdminToolsPage;
import pages.salesforce.app.airSlate_app.admin_tools.tabs.AccountTab;
import tests.salesforce.SalesforceAirSlateBaseTest;

import java.io.IOException;
import java.net.URISyntaxException;

import static data.salesforce.SalesforceTestData.NotificationMessages.THIS_EMAIL_IS_NOT_IN_OUR_SYSTEM;
import static data.salesforce.SalesforceTestData.NotificationMessages.WRONG_EMAIL_OR_PASSWORD;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static pages.salesforce.enums.admin_tools.ASAppAdminToolTabs.*;
import static utils.StringMan.makeUniqueEmail;

@Feature("airSlate app")
@Listeners(WebTestListener.class)
public class ASAppAdminToolsTest extends SalesforceAirSlateBaseTest {

    private String adminToolURL;
    private ASAppAdminToolsPage adminToolsPage;

    @Parameters({"recipientEmail"})
    @BeforeTest
    public void setUp(@Optional("pdf_sf_rnt@support.pdffiller.com") String recipientEmail) throws IOException, URISyntaxException {
        WebDriver driver = getDriver();
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        setSetupWizardStage(SetupWizardStage.Stage.FINAL);
        deleteUserConfigurationByUsername(salesforceDefaultLogin);
        disconnectWorkspace();

        adminToolsPage = getAdminToolsPage(salesAppBasePage);
        adminToolURL = driver.getCurrentUrl();
    }

    @BeforeMethod
    public void getAccountPage() {
        deleteUserConfigurationByUsername(salesforceDefaultLogin);
        disconnectAdmin();
        getUrl(adminToolURL);
        adminToolsPage.isOpened();
    }

    @Story("Should successfully Login with airSlate account")
    @Test
    public void loginIn() {
        AccountTab accountPage = adminToolsPage.openTab(ACCOUNT);
        accountPage.checkDefaultMessages(false);
        accountPage.setEmail(defaultAirSlateUserEmail)
                .setPassword(TestData.defaultPassword)
                .login();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(accountPage.isAdminAuthorized(), "Admin is not authorized");
        softAssert.assertTrue(accountPage.isTabLocked(TEAMMATES), "Teammates tab is unlocked after admin authorized");
        softAssert.assertTrue(accountPage.isTabLocked(CUSTOM_BUTTONS), "Custom button tab is unlocked after admin authorized");
        softAssert.assertFalse(accountPage.isTabLocked(WORKSPACE), "Workspace tab is locked after admin authorized");
        softAssert.assertAll();
    }

    @Story("Admin can reset password")
    @Test
    public void passwordReset() {
        refreshPage();
        adminToolsPage.isOpened();
        AccountTab accountPage = adminToolsPage.openTab(ACCOUNT);
        accountPage.login();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(accountPage.isEmailErrorShown(), "Email error is not shown");
        softAssert.assertTrue(accountPage.isPasswordErrorShown(), "Password error is not shown");

        accountPage.forgotPassword()
                .recoverMyPassword();
        softAssert.assertTrue(accountPage.isEmailErrorShown(), "Email error is not shown when recover email");

        accountPage.setEmail(defaultAirSlateUserEmail.split("@")[0])
                .recoverMyPassword();
        softAssert.assertTrue(accountPage.isEmailErrorShown(), "Email error is not shown when entered in incorrect format");

        accountPage.setEmail(makeUniqueEmail(defaultAirSlateUserEmail))
                .recoverMyPassword();
        softAssert.assertEquals(accountPage.getNotificationMessage(), THIS_EMAIL_IS_NOT_IN_OUR_SYSTEM,
                "Incorrect notification message after recover password");
        accountPage.returnToLogIn().checkDefaultMessages(false);
        accountPage.setEmail(defaultAirSlateUserEmail.split("@")[0])
                .login();
        softAssert.assertTrue(accountPage.isEmailErrorShown(), "Email error is not shown when entered in incorrect" +
                " format at login form");
        softAssert.assertTrue(accountPage.isPasswordErrorShown(), "Password error is not shown second try");

        accountPage.setEmail(defaultAirSlateUserEmail)
                .setPassword(TestData.defaultPassword.substring(0, 5))
                .login();
        softAssert.assertFalse(accountPage.isEmailErrorShown(), "Email error is shown when correct data used");
        softAssert.assertTrue(accountPage.isPasswordErrorShown(), "Password error is not shown when his length less than 6");

        accountPage.forgotPassword();
        accountPage.returnToLogIn();
        accountPage.forgotPassword();
        softAssert.assertAll();
    }

    @Story("Should show error message when trying to login with incorrect credentials")
    @Test
    public void incorrectPassword() {
        AccountTab accountPage = adminToolsPage.openTab(ACCOUNT);
        accountPage.setEmail(defaultAirSlateUserEmail)
                .setPassword(TestData.defaultPassword + "wrong")
                .login();
        assertEquals(accountPage.getNotificationMessage(), WRONG_EMAIL_OR_PASSWORD, "Incorrect notification on login try");
    }

    @Story("Should successfully delete admin authorization")
    @Test
    public void deleteAdminAuthorization() {
        AccountTab accountPage = adminToolsPage.openTab(ACCOUNT);
        accountPage.setEmail(defaultAirSlateUserEmail)
                .setPassword(TestData.defaultPassword)
                .login();
        assertTrue(accountPage.isAdminAuthorized(), "Admin is not authorized");
        accountPage.disconnectAdmin().cancel();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(accountPage.isTabLocked(TEAMMATES), "Teammates tab is unlocked after cancel disconnect");
        softAssert.assertTrue(accountPage.isTabLocked(CUSTOM_BUTTONS), "Custom button tab is unlocked after cancel disconnect");
        softAssert.assertFalse(accountPage.isTabLocked(WORKSPACE), "Workspace tab is locked after cancel disconnect");
        softAssert.assertTrue(accountPage.isAdminAuthorized(), "Admin is not authorized after cancel disconnect");
        softAssert.assertEquals(accountPage.getEmailValue(), defaultAirSlateUserEmail, "Email value is changed after cancel disconnect");

        accountPage = accountPage.disconnectAdmin().closePopUp(AccountTab.class);
        softAssert.assertTrue(accountPage.isTabLocked(TEAMMATES), "Teammates tab is unlocked after close disconnect popup");
        softAssert.assertTrue(accountPage.isTabLocked(CUSTOM_BUTTONS), "Custom button tab is unlocked after close disconnect popup");
        softAssert.assertFalse(accountPage.isTabLocked(WORKSPACE), "Workspace tab is locked after close disconnect popup");
        softAssert.assertTrue(accountPage.isAdminAuthorized(), "Admin is not authorized after close disconnect popup");
        softAssert.assertEquals(accountPage.getEmailValue(), defaultAirSlateUserEmail, "Email value is changed after close disconnect popup");

        accountPage.disconnectAdmin().disconnect();
        softAssert.assertTrue(accountPage.isTabLocked(TEAMMATES), "Teammates tab is unlocked after disconnect");
        softAssert.assertTrue(accountPage.isTabLocked(CUSTOM_BUTTONS), "Custom button tab is unlocked after disconnect");
        softAssert.assertTrue(accountPage.isTabLocked(WORKSPACE), "Workspace tab is unlocked after disconnect");
        softAssert.assertFalse(accountPage.isAdminAuthorized(), "Admin is not authorized after disconnect");
        accountPage.checkDefaultMessages(false);

        softAssert.assertAll();
    }
}
