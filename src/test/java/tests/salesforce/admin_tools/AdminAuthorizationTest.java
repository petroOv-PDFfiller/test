package tests.salesforce.admin_tools;

import core.DriverWinMan;
import core.DriverWindow;
import data.TestData;
import imap.ImapClient;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import pages.pdffiller.ResetPasswordPage;
import pages.salesforce.app.DaDaDocs.admin_tools.AdminToolsPage;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.AuthorizationTab;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.UsersTab;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.enums.admin_tools.AdminToolTabs;
import tests.salesforce.SalesforceBaseTest;
import utils.Logger;
import utils.PDFRequestsMan;
import utils.StringMan;

import java.io.IOException;
import java.net.URISyntaxException;

import static core.check.Check.checkTrue;
import static org.testng.Assert.assertTrue;
import static utils.ImapMan.getUrlFromPasswordResetMail;

@Feature("Admin authorization process")
@Listeners({WebTestListener.class, ImapListener.class})
public class AdminAuthorizationTest extends SalesforceBaseTest {

    private WebDriver driver;
    private AuthorizationTab authorizationPage;
    private String pdffillerEmail;
    private String pdffillerPassword = TestData.defaultPassword;
    private String newPassword = "qwerty561";
    private String defaultAdminEmail;
    private String defaultAdminPassword;
    private String setupURL;
    private String loginFailed = "Login Failed. Either the specified PDFfiller account does not exist," +
            " or your password was entered incorrectly. Please try again";
    private String userAlreadyExist = "The email you are trying to enter already exists.";
    private PDFRequestsMan requestsMan;
    private SalesAppBasePage salesAppBasePage;

    @Parameters({"defaultAdminEmail", "defaultAdminPassword", "recipientEmail"})
    @BeforeTest
    public void setUp(@Optional("pdf_sf_aqa+at@support.pdffiller.com") String defaultAdminEmail,
                      @Optional("qwe1rty2") String defaultAdminPassword,
                      @Optional("pdf_sf_rnt@support.pdffiller.com") String recipientEmail) {
        driver = getDriver();
        pdffillerEmail = StringMan.makeUniqueEmail(recipientEmail);
        this.defaultAdminEmail = defaultAdminEmail;
        this.defaultAdminPassword = defaultAdminPassword;
        registerUser(pdffillerEmail, pdffillerPassword);
        requestsMan = new PDFRequestsMan(testData.url);
        salesAppBasePage = loginWithDefaultCredentials();
        setupURL = driver.getCurrentUrl();
    }

    @BeforeMethod
    public void openPage() {
        getUrl(setupURL);
        salesAppBasePage.isOpened();
        authorizationPage = salesAppBasePage.openAppLauncher().openAdminToolPage().openTab(AdminToolTabs.AUTHORIZATION);
    }

    @Test
    @Story("Should successfully Sign up with new pdffiller account")
    public void successfullySignUpWithNewPdffillerAccount() {
        authorizationPage.logOut();
        authorizationPage.openSignUp()
                .enterEmailAndPassword(pdffillerEmail.replace("sf_", "sf_n"), pdffillerPassword)
                .tryToAuthorize();
        AdminToolsPage adminToolPage = new AdminToolsPage(driver);
        refreshPage();
        adminToolPage.isOpened();
        new UsersTab(driver).isOpened();
        adminToolPage.openTab(AdminToolTabs.AUTHORIZATION);
        assertTrue(authorizationPage.isAuthorized(), "Admin isn`t authorized");
    }

    @Test
    @Story("Should successfully Login with existing PDFfiller account")
    public void loginWithExistingPDFfillerAccount() {
        authorizationPage.logOut();
        authorizationPage.openSignIn()
                .enterEmailAndPassword(pdffillerEmail, pdffillerPassword).tryToAuthorize();
        AdminToolsPage adminToolPage = new AdminToolsPage(driver);
        refreshPage();
        adminToolPage.isOpened();
        new UsersTab(driver).isOpened();
        adminToolPage.openTab(AdminToolTabs.AUTHORIZATION);
        assertTrue(authorizationPage.isAuthorized(), "Admin isn`t authorized");
    }

    @Test
    @Story("Admin can reset password")
    public void passwordReset() throws IOException, URISyntaxException {
        ImapClient imap = new ImapClient(pdffillerEmail, pdffillerPassword);
        authorizationPage.logOut();
        imap.deleteAllMessagesWithSubject("Password reset");
        authorizationPage.openSignIn().forgetPassword(pdffillerEmail);

        String resetPasswordUrl = getUrlFromPasswordResetMail(pdffillerEmail, imap);
        DriverWinMan driverWinMan = new DriverWinMan(driver);
        DriverWindow currentWindow = driverWinMan.getCurrentWindow();
        checkTrue(driverWinMan.switchToNewWindow(), "New window is not created or cannot switch to it");

        getUrl(resetPasswordUrl);
        ResetPasswordPage resetPasswordPage = new ResetPasswordPage(driver);
        resetPasswordPage.isOpened();
        try {
            resetPasswordPage.resetPassword(newPassword);
        } catch (Exception ignored) {
            //temporary solve, Problem is that opened page is different,but password is changed successfully
            //need help with this
        } finally {
            driverWinMan.switchToWindow(currentWindow);
            driverWinMan.keepOnlyWindow(currentWindow);
            AdminToolsPage adminToolPage = new AdminToolsPage(driver);
            adminToolPage.isOpened();
            authorizationPage.isOpened();
            authorizationPage.openSignIn().enterEmailAndPassword(pdffillerEmail, newPassword).tryToAuthorize();
            adminToolPage = new AdminToolsPage(driver);
            refreshPage();
            adminToolPage.isOpened();
            new UsersTab(driver).isOpened();
            adminToolPage.openTab(AdminToolTabs.AUTHORIZATION);
            checkTrue(requestsMan.changePassword(pdffillerEmail, newPassword, pdffillerPassword), "Failed to revert password to [" + newPassword + "]");
        }
        assertTrue(authorizationPage.isAuthorized(), "Admin isn`t authorized");
    }

    @Test
    @Story("Should show error message when trying Sign up with already exist account")
    public void signUpWithAlreadyExistAccount() {
        authorizationPage.logOut();
        authorizationPage.openSignUp().enterEmailAndPassword(pdffillerEmail, pdffillerPassword + "123")
                .tryToAuthorize();
        assertTrue(authorizationPage.notificationMessageContainsText(userAlreadyExist), "Wrong notification message is displayed");
    }

    @Test
    @Story("Should show error message when trying to login with incorrect credentials")
    public void loginWithIncorrectCredentials() {
        authorizationPage.logOut();
        authorizationPage.openSignIn().enterEmailAndPassword(pdffillerEmail.substring(3), pdffillerPassword + "123")
                .tryToAuthorize();
        assertTrue(authorizationPage.notificationMessageContainsText(loginFailed), "Wrong notification message is displayed");
    }

    @AfterTest
    public void signInAsDefaultAdmin() {
        try {
            getUrl(setupURL);
            salesAppBasePage.isOpened();
            authorizationPage = salesAppBasePage.openAppLauncher().openAdminToolPage().openTab(AdminToolTabs.AUTHORIZATION);
            authorizationPage.logOut();
            authorizationPage.openSignIn().enterEmailAndPassword(defaultAdminEmail, defaultAdminPassword)
                    .tryToAuthorize();
            AdminToolsPage adminToolPage = new AdminToolsPage(driver);
            refreshPage();
            adminToolPage.isOpened();
            Logger.info("signInAsDefaultAdmin is completed");
        } catch (Exception e) {
            e.printStackTrace();
            Logger.info("signInAsDefaultAdmin is not completed");
        } finally {
            tearDown();
        }
    }
}
