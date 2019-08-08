package tests.salesforce.admin_tools;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.salesforce.app.DaDaDocs.admin_tools.AdminToolsPage;
import pages.salesforce.app.DaDaDocs.admin_tools.entities.DaDaDocsUser;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.AuthorizationTab;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.UsersTab;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_setup.InstalledPackagesPage;
import pages.salesforce.app.sf_setup.SetupSalesforcePage;
import pages.salesforce.enums.admin_tools.AdminToolTabs;
import tests.salesforce.SalesforceBaseTest;
import utils.Logger;

import java.util.List;

import static data.salesforce.SalesforceTestData.SalesforceUserGetMethods.*;
import static java.util.Collections.sort;

@Feature("New admin tools test")
@Listeners({WebTestListener.class, ImapListener.class})
public class AdminToolsTest extends SalesforceBaseTest {

    private WebDriver driver;
    private String defaultAdminEmail;
    private String defaultAdminPassword;
    private String setupURL;
    private UsersTab usersTab;
    private List<DaDaDocsUser> usersBeforeTest;

    @Parameters({"defaultAdminEmail", "defaultAdminPassword"})
    @BeforeTest
    public void setUp(@Optional("pdf_sf_aqa@support.pdffiller.com") String defaultAdminEmail, @Optional("qwe1rty2") String defaultAdminPassword) {
        driver = getDriver();
        this.defaultAdminEmail = defaultAdminEmail;
        this.defaultAdminPassword = defaultAdminPassword;
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        SetupSalesforcePage setupSalesForcePage = salesAppBasePage.openSetupPage();
        InstalledPackagesPage installedPackagesPage = setupSalesForcePage.openInstalledPackages();
        AuthorizationTab authorizationPage = installedPackagesPage.openDaDaDocsAdminTools().openTab(AdminToolTabs.AUTHORIZATION);
        if (!authorizationPage.isAuthorized()) {
            authorizationPage.openSignIn().enterEmailAndPassword(defaultAdminEmail, defaultAdminPassword)
                    .tryToAuthorize();
            AdminToolsPage adminToolPage = new AdminToolsPage(driver);
            refreshPage();
            adminToolPage.isOpened();
        }
        setupURL = driver.getCurrentUrl();
        usersTab = authorizationPage.openTab(AdminToolTabs.USERS);
        usersBeforeTest = usersTab.users;
    }

    @Test
    @Story("Activate user license")
    public void activateUserLicense() {
        getUrl(setupURL);
        AdminToolsPage adminToolPage = new AdminToolsPage(driver);
        adminToolPage.isOpened();
        usersTab = adminToolPage.openTab(AdminToolTabs.USERS);
        usersTab.deactivateAllUsers();
        int beforeActivate = usersTab.getNumberOfElements(usersTab.activeUsers);
        usersTab.activateUser();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(usersTab.getNumberOfElements(usersTab.activeUsers), beforeActivate + 1, "Number of active users was not changed");
        usersTab.deactivateUser(usersTab.userId);
        softAssert.assertEquals(usersTab.getNumberOfElements(usersTab.activeUsers), beforeActivate, "Number of active users was not returned to initial value");
        softAssert.assertAll();
    }

    @Test
    @Story("Sort user in admin tool")
    public void sortUsers() {
        getUrl(setupURL);
        AdminToolsPage adminToolPage = new AdminToolsPage(driver);
        adminToolPage.isOpened();
        usersTab = adminToolPage.openTab(AdminToolTabs.USERS);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(usersTab.sortUsersBy(GET_FULL_NAME), "First sort by fullname works incorrect");
        softAssert.assertTrue(usersTab.sortUsersBy(GET_FULL_NAME), "Second sort by fullname works incorrect");

        softAssert.assertTrue(usersTab.sortUsersBy(GET_EMAIL), "First sort by email works incorrect");
        softAssert.assertTrue(usersTab.sortUsersBy(GET_EMAIL), "Second sort by email works incorrect");

        softAssert.assertTrue(usersTab.sortUsersBy(GET_PROFILE), "First sort by profile works incorrect");
        softAssert.assertTrue(usersTab.sortUsersBy(GET_PROFILE), "Second sort by profile works incorrect");

        softAssert.assertTrue(usersTab.sortUsersBy(GET_LICENSE), "First sort by license works incorrect");
        softAssert.assertTrue(usersTab.sortUsersBy(GET_LICENSE), "Second sort by license works incorrect");
        softAssert.assertFalse(usersTab.isUserListContainsEmail(defaultAdminEmail), "DDD admin user is present in the list");
        softAssert.assertAll();
    }

    @Test
    @Story("Filter user in admin tool by license")
    public void filterUsersByLicense() {
        getUrl(setupURL);
        AdminToolsPage adminToolPage = new AdminToolsPage(driver);
        adminToolPage.isOpened();
        usersTab = adminToolPage.openTab(AdminToolTabs.USERS);
        usersTab.setUsersTo(usersBeforeTest);
        usersTab.checkAllUserLicenseFilters();
        usersTab.disableUserFilters();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(usersTab.isUserFiltersDisabled(), "User filter is not disabled");
        sort(usersTab.users);
        sort(usersBeforeTest);
        softAssert.assertEquals(usersTab.users, usersBeforeTest, "User list was changed after applying filters");
        softAssert.assertAll();
    }

    @Test
    @Story("Filter user in admin tool by profile")
    public void filterUsersByProfile() {
        getUrl(setupURL);
        AdminToolsPage adminToolPage = new AdminToolsPage(driver);
        adminToolPage.isOpened();
        usersTab = adminToolPage.openTab(AdminToolTabs.USERS);
        usersTab.checkAllUserProfileFilters();
        usersTab.disableUserFilters();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(usersTab.isUserFiltersDisabled(), "User filter is not disabled");
        sort(usersTab.users);
        sort(usersBeforeTest);
        softAssert.assertEquals(usersTab.users, usersBeforeTest, "User list was changed after applying filters");
        softAssert.assertAll();
    }

    @Test
    @Story("Search by email")
    public void searchByEmail() {
        getUrl(setupURL);
        AdminToolsPage adminToolPage = new AdminToolsPage(driver);
        adminToolPage.isOpened();
        usersTab = adminToolPage.openTab(AdminToolTabs.USERS);
        String partOfUserEmail = usersTab.users.get(0).getEmail().substring(0, 5);
        usersTab.searchUserByEmail(partOfUserEmail);

        SoftAssert softAssert = new SoftAssert();

        for (DaDaDocsUser user : usersTab.users) {
            softAssert.assertTrue(user.getEmail().contains(partOfUserEmail),
                    "Wrong user displayed. Expected email that contained " + partOfUserEmail +
                            ", but present: " + user.getEmail());
        }
        softAssert.assertTrue(usersTab.isHighlightedPartCorrectlyMarked(partOfUserEmail), "Highlight feature is working wrong");
        usersTab.clearSearchParameter();
        softAssert.assertAll();
    }

    @AfterTest
    public void cleanUp() {
        try {
            getUrl(setupURL);
            AdminToolsPage adminToolPage = new AdminToolsPage(driver);
            adminToolPage.isOpened();
            usersTab = adminToolPage.openTab(AdminToolTabs.USERS);
            usersTab.setUsersTo(usersBeforeTest);
            Logger.info("Clean up is completed");
        } catch (Exception e) {
            e.printStackTrace();
            Logger.info("Clean up is not completed");
        } finally {
            tearDown();
        }
    }
}

