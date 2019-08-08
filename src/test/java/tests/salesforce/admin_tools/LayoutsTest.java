package tests.salesforce.admin_tools;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.salesforce.ClassicSalesforcePage;
import pages.salesforce.app.DaDaDocs.admin_tools.AdminToolsPage;
import pages.salesforce.app.DaDaDocs.admin_tools.entities.Layout;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.AuthorizationTab;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.LayoutsTab;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.SalesAppHomePage;
import pages.salesforce.app.sf_objects.contacts.ClassicContactConcretePage;
import pages.salesforce.app.sf_objects.contacts.ContactsPage;
import pages.salesforce.enums.SalesTab;
import pages.salesforce.enums.admin_tools.AdminToolTabs;
import tests.salesforce.SalesforceBaseTest;
import utils.Logger;

import java.util.List;

import static data.salesforce.SalesforceTestData.SalesforceLayoutsFilters.ACTIVE;
import static data.salesforce.SalesforceTestData.SalesforceLayoutsFilters.ALL;
import static data.salesforce.SalesforceTestData.SalesforceLayoutsGetMethods.LAYOUT_NAME;
import static data.salesforce.SalesforceTestData.SalesforceLayoutsGetMethods.OBJECT_NAME;
import static data.salesforce.SalesforceTestData.SortTypes.ASC_SORT;
import static data.salesforce.SalesforceTestData.SortTypes.DESC_SORT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static pages.salesforce.enums.admin_tools.LayoutsTabActions.*;

@Feature("Layouts")
@Listeners({WebTestListener.class, ImapListener.class})
public class LayoutsTest extends SalesforceBaseTest {

    private WebDriver driver;
    private AuthorizationTab authorizationPage;
    private String defaultAdminEmail;
    private String defaultAdminPassword;
    private String adminToolUrl;
    private AdminToolsPage adminToolPage;
    private SalesAppBasePage salesAppBasePage;

    @Parameters({"defaultAdminEmail", "defaultAdminPassword"})
    @BeforeTest
    public void setUp(@Optional("pdf_sf_aqa@support.pdffiller.com") String defaultAdminEmail,
                      @Optional("qwe1rty2") String defaultAdminPassword) {
        driver = getDriver();
        this.defaultAdminEmail = defaultAdminEmail;
        this.defaultAdminPassword = defaultAdminPassword;
        salesAppBasePage = loginWithDefaultCredentials();
        adminToolPage = salesAppBasePage.openAppLauncher().openAdminToolPage();
        adminToolUrl = driver.getCurrentUrl();
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
    @Story("The unauthorized administrator shouldn't be able to access 'Layouts' tab in the Admin Tools component.")
    public void layoutsTabAccess() {
        authorizationPage = adminToolPage.openTab(AdminToolTabs.AUTHORIZATION);
        authorizationPage.logOut();
        assertTrue(authorizationPage.isTabDisabled(AdminToolTabs.LAYOUTS), "Unauthorized user can open layout tab");
    }

    @Test
    @Story("Search")
    public void secondSearch() {
        LayoutsTab layoutsTab = adminToolPage.openTab(AdminToolTabs.LAYOUTS);
        List<Layout> layoutsBeforeSort = layoutsTab.getLayouts();
        layoutsTab.enterSearchRequest("12").disableSearchFocus();
        assertEquals(layoutsTab.getLayouts(), layoutsBeforeSort, "Search is done without click on tooltip");
        layoutsTab.enterSearchRequest("opp").makeSearchByEnterButton();
        List<Layout> currentLayouts = layoutsTab.getLayouts();
        currentLayouts.forEach(layout -> {
            assertTrue(layout.getLayoutName().toLowerCase().contains("opp"), "wrong layout find after search opp "
                    + layout.getLayoutName());
        });
    }

    @Test
    @Story("Search")
    public void searchByValue() {
        LayoutsTab layoutsTab = adminToolPage.openTab(AdminToolTabs.LAYOUTS);
        String searchValue = "acc";
        List<Layout> layoutsBeforeSearch = layoutsTab.getLayouts();
        layoutsTab.enterSearchRequest(searchValue).findSearchResults();
        int expectedCount = layoutsTab.getNumberOfExpectedLayoutsAfterSearch();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(layoutsTab.getTextFromSearchPanel(),
                String.format("Search results for: %s (%d)", searchValue, expectedCount),
                "Wrong message in search panel");
        softAssert.assertEquals(layoutsTab.getLayouts().size(), expectedCount, "Wrong count of search result layouts");
        layoutsTab.isEveryLayoutHasHighlightedSearchPart(searchValue);
        layoutsTab.clearSearchResult();
        softAssert.assertEquals(layoutsTab.getLayouts(), layoutsBeforeSearch, "Search parameter is not cleared");
        softAssert.assertAll();
    }

    @Test
    @Story("Search")
    public void searchWithEmptyValue() {
        LayoutsTab layoutsTab = adminToolPage.openTab(AdminToolTabs.LAYOUTS);
        String searchValue = "fsdgdgfdhnfgmfh";
        List<Layout> layoutsBeforeSearch = layoutsTab.getLayouts();
        layoutsTab.enterSearchRequest(searchValue).findSearchResults();
        int expectedCount = layoutsTab.getNumberOfExpectedLayoutsAfterSearch();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(layoutsTab.getTextFromSearchPanel().trim(),
                String.format("Search results for: %s (%d)", searchValue, expectedCount),
                "Wrong message in search panel");
        softAssert.assertEquals(layoutsTab.getLayouts().size(), expectedCount, "Wrong count of search result layouts");
        layoutsTab.clearEmptySearchResult();
        softAssert.assertEquals(layoutsTab.getLayouts(), layoutsBeforeSearch, "Search parameter is not cleared");
        softAssert.assertAll();
    }

    @Test
    @Story("Check sorting of Object name by ascending order")
    public void sortingOfObjectName() {
        SoftAssert softAssert = new SoftAssert();
        LayoutsTab layoutsTab = adminToolPage.openTab(AdminToolTabs.LAYOUTS);
        layoutsTab.sortLayoutsBy(OBJECT_NAME, DESC_SORT);
        softAssert.assertTrue(layoutsTab.checkSortBy(OBJECT_NAME, DESC_SORT), "Cannot sort of object name by desc order");
        layoutsTab.sortLayoutsBy(OBJECT_NAME, ASC_SORT);
        softAssert.assertTrue(layoutsTab.checkSortBy(OBJECT_NAME, ASC_SORT), "Cannot sort of object name by asc order");
        softAssert.assertAll();
    }

    @Test
    @Story("Check sorting of Layout name by descending order")
    public void sortingOfLayoutName() {
        SoftAssert softAssert = new SoftAssert();
        LayoutsTab layoutsTab = adminToolPage.openTab(AdminToolTabs.LAYOUTS);
        layoutsTab.sortLayoutsBy(LAYOUT_NAME, ASC_SORT);
        softAssert.assertTrue(layoutsTab.checkSortBy(LAYOUT_NAME, ASC_SORT), "Cannot sort of layout name by asc order");
        layoutsTab.sortLayoutsBy(LAYOUT_NAME, DESC_SORT);
        softAssert.assertTrue(layoutsTab.checkSortBy(LAYOUT_NAME, DESC_SORT), "Cannot sort of layout name by desc order");
        softAssert.assertAll();
    }

    @Test
    @Story("Filters")
    public void filterSettingsWhenFilteredValueChanged() {
        SoftAssert softAssert = new SoftAssert();
        LayoutsTab layoutsTab = adminToolPage.openTab(AdminToolTabs.LAYOUTS);
        layoutsTab.pageAction(HIDE_FROM_ALL_LAYOUTS).pageAction(SAVE_CHANGES);
        layoutsTab.filterLayoutsBy(ALL);
        List<Layout> layouts = layoutsTab.getLayouts();
        int allLayoutsSize = layouts.size();
        assertTrue(allLayoutsSize > 6, "Number of layouts less than 6");
        for (int i = 0; i < 6; i++) {
            layoutsTab.showLayout(layouts.get(i).getLayoutName());
        }
        layoutsTab.pageAction(SAVE_CHANGES);
        layoutsTab.filterLayoutsBy(ACTIVE);
        layouts = layoutsTab.getLayouts();
        String expectedFilterName = String.format(ACTIVE + "(%s)", layouts.size());
        softAssert.assertEquals(layoutsTab.getCurrentFilter(), expectedFilterName);
        softAssert.assertEquals(layouts.size(), 6);
        layoutsTab.pageAction(HIDE_FROM_ALL_LAYOUTS).pageAction(SAVE_CHANGES);
        expectedFilterName = String.format(ALL + "(%s)", allLayoutsSize);
        softAssert.assertEquals(layoutsTab.getCurrentFilter(), expectedFilterName);
        softAssert.assertAll();
    }

    @Test
    @Story("Filters")
    public void filterSettingsWhenFilteredValue2Changed() {
        SoftAssert softAssert = new SoftAssert();
        LayoutsTab layoutsTab = adminToolPage.openTab(AdminToolTabs.LAYOUTS);
        layoutsTab.pageAction(HIDE_FROM_ALL_LAYOUTS).pageAction(SAVE_CHANGES);
        layoutsTab.filterLayoutsBy(ALL);
        List<Layout> layouts = layoutsTab.getLayouts();
        int allLayoutsSize = layouts.size();
        for (int i = 0; i < 6; i++) {
            layoutsTab.showLayout(layouts.get(i).getLayoutName());
        }
        layoutsTab.pageAction(SAVE_CHANGES);
        layoutsTab.filterLayoutsBy(ACTIVE);
        layouts = layoutsTab.getLayouts();
        String expectedFilterName = String.format(ACTIVE + "(%s)", layouts.size());
        softAssert.assertEquals(layoutsTab.getCurrentFilter(), expectedFilterName);
        softAssert.assertEquals(layouts.size(), 6);
        for (Layout layout : layouts) {
            softAssert.assertTrue(layout.getShow(), layout.getLayoutName() + " layout have wrong show status");
        }
        layoutsTab.hideLayout(layouts.get(0).getLayoutName());
        layoutsTab.pageAction(SAVE_CHANGES);
        layouts = layoutsTab.getLayouts();
        expectedFilterName = String.format(ACTIVE + "(%s)", layouts.size());
        softAssert.assertEquals(layoutsTab.getCurrentFilter(), expectedFilterName);
        softAssert.assertEquals(layouts.size(), 5);
        for (Layout layout : layouts) {
            softAssert.assertTrue(layout.getShow(), layout.getLayoutName() + " layout have wrong show status");
        }
        softAssert.assertAll();
    }

    @Test
    @Story("'Show in all layouts' returns list of all activated layouts and add btn 'Use Dadadocs' " +
            "to the specific record")
    public void showInAllLayouts() {
        SoftAssert softAssert = new SoftAssert();
        LayoutsTab layoutsTab = adminToolPage.openTab(AdminToolTabs.LAYOUTS);
        layoutsTab.pageAction(SHOW_IN_ALL_LAYOUTS).pageAction(SAVE_CHANGES);
        salesAppBasePage.isOpened();
        SalesAppHomePage salesAppHomePage = salesAppBasePage.openAppLauncher().openSalesAppPage();
        ContactsPage contactsPage = salesAppHomePage.openTab(SalesTab.CONTACTS);
        contactsPage.openObjectByNumber(1);
        String contactName = contactsPage.selectedObject;
        ClassicSalesforcePage page = contactsPage.switchToClassicInterface();
        ClassicContactConcretePage concretePage = page.openRecentItem(contactName, ClassicContactConcretePage.class);
        softAssert.assertTrue(concretePage.isUseDaDaDocsButtonIsPresent(), "Use DaDaDocs button is not present" +
                " in contact layout");
        salesAppBasePage = concretePage.returnToLightningDesign();
        salesAppBasePage.isOpened();
        softAssert.assertAll();
    }

    @Test
    @Story("'Hide from all layouts' returns list of all deactivated layouts and removes btn 'Use Dadadocs' " +
            "from the record")
    public void hideInAllLayouts() {
        SoftAssert softAssert = new SoftAssert();
        LayoutsTab layoutsTab = adminToolPage.openTab(AdminToolTabs.LAYOUTS);
        layoutsTab.pageAction(HIDE_FROM_ALL_LAYOUTS).pageAction(SAVE_CHANGES);
        salesAppBasePage.isOpened();
        SalesAppHomePage salesAppHomePage = salesAppBasePage.openAppLauncher().openSalesAppPage();
        ContactsPage contactsPage = salesAppHomePage.openTab(SalesTab.CONTACTS);
        contactsPage.openObjectByNumber(1);
        String contactName = contactsPage.selectedObject;
        ClassicSalesforcePage page = contactsPage.switchToClassicInterface();
        ClassicContactConcretePage concretePage = page.openRecentItem(contactName, ClassicContactConcretePage.class);
        softAssert.assertFalse(concretePage.isUseDaDaDocsButtonIsPresent(), "Use DaDADocs button is present in" +
                " contact layout");
        salesAppBasePage = concretePage.returnToLightningDesign();
        salesAppBasePage.isOpened();
        softAssert.assertAll();
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
            adminToolPage = new AdminToolsPage(driver);
            refreshPage();
            adminToolPage.isOpened();
            LayoutsTab layoutsTab = adminToolPage.openTab(AdminToolTabs.LAYOUTS);
            layoutsTab.pageAction(SHOW_IN_ALL_LAYOUTS).pageAction(SAVE_CHANGES);
            Logger.info("sign In As Default Admin is completed");
        } catch (Exception e) {
            Logger.error("sign In As Default Admin is not completed");
        } finally {
            tearDown();
        }
    }
}
