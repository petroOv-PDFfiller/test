package pages.salesforce.app;

import api.salesforce.entities.SalesforceObject;
import core.PageBase;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import pages.salesforce.SalesforceBasePage;
import pages.salesforce.app.DaDaDocs.admin_tools.AdminToolsPage;
import pages.salesforce.app.airSlate_app.admin_tools.ASAppAdminToolsPage;
import pages.salesforce.app.sf_objects.ConcreteRecordPage;
import pages.salesforce.app.sf_objects.SalesforceObjectPage;
import pages.salesforce.app.sf_objects.accounts.AccountsPage;
import pages.salesforce.app.sf_objects.campaigns.CampaignsPage;
import pages.salesforce.app.sf_objects.contacts.ContactsPage;
import pages.salesforce.app.sf_objects.opportunities.OpportunitiesPage;
import pages.salesforce.app.sf_setup.CustomSettingsPage;
import pages.salesforce.app.sf_setup.InstalledPackagesPage;
import pages.salesforce.app.sf_setup.SetupSalesforcePage;
import pages.salesforce.enums.SalesTab;

import java.lang.reflect.InvocationTargetException;

import static core.check.Check.checkFail;
import static core.check.Check.checkTrue;


public class SalesAppBasePage extends SalesforceBasePage {

    protected final String asAppAdminToolPageURLPath = "apex/AdminTools";
    private final String lightning = "lightning/";
    private By allTabs = By.xpath("//*[@class='slds-assistive-text keyboardDnd']");
    private By contactsLink = By.xpath("//a[@title='Contacts']/span");
    private By btnSetup = By.xpath("//span[@class='slds-align-middle' and .='Setup']");
    private By beforeAppPageWrapper = By.id("wrapper");

    public SalesAppBasePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "HomePage isn`t loaded");
        checkTrue(isElementDisappeared(beforeAppPageWrapper, 5), "Login wrapper is not disappeared");
        checkTrue(waitUntilPageLoaded(), "HomePage isn`t loaded");
        waitForSalesforceLoading();
        checkTrue(isElementPresentAndDisplayed(btnAppLauncher, 60), "Home page is not opened");
    }

    @Step("Open tab: {0}")
    public <T extends SalesforceBasePage> T openTab(SalesTab tab) {
        By titleTab = By.xpath("//*[@class='slds-grid slds-has-flexi-truncate navUL']//*[@title='" + tab.getNameNewLayout() + "']");
        checkTrue(isElementPresent(titleTab, 12), "tab " + tab + " is not present");
        clickJS(titleTab); //Spring 19 version fix

        SalesforceBasePage salesforceBasePage = null;
        switch (tab) {
            case ACCOUNTS: {
                salesforceBasePage = new AccountsPage(driver);
                break;
            }
            case CONTACTS: {
                salesforceBasePage = new ContactsPage(driver);
                break;
            }
            case OPPORTUNITIES: {
                salesforceBasePage = new OpportunitiesPage(driver);
                break;
            }
            case CAMPAIGNS: {
                salesforceBasePage = new CampaignsPage(driver);
                break;
            }
            // TODO for extra classes
        }
        salesforceBasePage.isOpened();

        return (T) salesforceBasePage;
    }

    @Step
    public ContactsPage openContactsPage() {
        driver.switchTo().defaultContent();
        checkTrue(isElementPresentAndDisplayed(contactsLink, 3), "Contacts link is not presented");
        click(contactsLink);
        ContactsPage contactsPage = new ContactsPage(driver);
        contactsPage.isOpened();

        return contactsPage;
    }

    @Step("Open Setup page")
    public SetupSalesforcePage openSetupPage() {
        checkTrue(isElementPresentAndDisplayed(setupGear, 5), "Setup gear is not displayed");
        click(setupGear);
        checkTrue(isElementPresentAndDisplayed(setupGearMenu, 15), "Gear menu is not displayed");
        checkTrue(isElementPresentAndDisplayed(btnSetup, 15), "Setup menu button is not displayed");
        click(btnSetup);
        switchToSetupPageWindows();
        SetupSalesforcePage setupSalesForcePage = new SetupSalesforcePage(driver);
        setupSalesForcePage.isOpened();
        return setupSalesForcePage;
    }

    public SetupSalesforcePage openSetupPageByUrl() {
        String expectedURL = getInstanceURL() + lightning + "setup/SetupOneHome/home";
        open(expectedURL);
        SetupSalesforcePage setupSalesForcePage = new SetupSalesforcePage(driver);
        setupSalesForcePage.isOpened();
        return setupSalesForcePage;
    }

    public InstalledPackagesPage openInstalledPackagesUrl() {
        String expectedURL = getInstanceURL() + lightning + "setup/ImportedPackage/home";
        open(expectedURL);
        return initExpectedPage(InstalledPackagesPage.class);
    }

    public CustomSettingsPage openCustomSettingsPage() {
        String expectedURL = getInstanceURL() + lightning + "setup/CustomSettings/home";
        open(expectedURL);
        return initExpectedPage(CustomSettingsPage.class);
    }

    private void switchToSetupPageWindows() {
        checkTrue(driverWinMan.switchToWindow("setup"), "Cannot switch to window");
        driverWinMan.keepOnlyWindow(driverWinMan.getCurrentWindow());
    }

    public <T extends ConcreteRecordPage> T openRecordPageById(SalesforceObject objectType, String recordId) {
        String instanceURL = getInstanceURL();
        String expectedURL = String.format(instanceURL + lightning + "r/%s/%s/view", objectType.getAPIName(), recordId);
        refreshPage();
        checkTrue(waitUntilPageLoaded(), "page is not loaded after refresh");
        open(expectedURL);
        checkTrue(waitUntilPageLoaded(), objectType.getRecordPage().getSimpleName() + "cannot load");
        ConcreteRecordPage page = PageFactory.initElements(driver, objectType.getRecordPage());
        page.isOpened();
        return (T) page;
    }

    public <T extends SalesforceObjectPage> T openObjectPage(SalesforceObject objectType) {
        String instanceURL = getInstanceURL();
        String expectedURL = String.format(instanceURL + lightning + "o/%s/list", objectType.getAPIName());
        refreshPage();
        checkTrue(waitUntilPageLoaded(), "page is not loaded after refresh");
        open(expectedURL);
        checkTrue(waitUntilPageLoaded(), objectType.getObjectPage().getSimpleName() + "cannot load");
        SalesforceObjectPage page = PageFactory.initElements(driver, objectType.getObjectPage());
        page.isOpened();
        return (T) page;
    }

    public ASAppAdminToolsPage openASAppAdminToolPageOnDX() {
        String instanceURL = getInstanceURL();
        String expectedURL = instanceURL + asAppAdminToolPageURLPath;
        open(expectedURL);
        return initExpectedPage(ASAppAdminToolsPage.class);
    }

    public AdminToolsPage openDaDaDocsAdminToolPage() {
        String expectedURL = getInstanceURL() + lightning + "n/pdffiller_sf__DaDaDocs_Settings";
        open(expectedURL);
        AdminToolsPage adminToolsPage = new AdminToolsPage(driver);
        adminToolsPage.isOpened();
        return adminToolsPage;
    }

    private String getInstanceURL() {
        String currentURL = driver.getCurrentUrl();
        String oneApp = "one/";
        String apex = "apex/";
        String currentAppPath;
        if (currentURL.contains(lightning))
            currentAppPath = lightning;
        else if (currentURL.contains(oneApp)) {
            currentAppPath = oneApp;
        } else {
            currentAppPath = apex;
        }
        return currentURL.substring(0, currentURL.indexOf(currentAppPath));
    }

    public LoginPage logOutFromSalesforce() {
        String logOutPath = "/secur/logout.jsp";
        driver.get(getInstanceURL() + logOutPath);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.isOpened();
        return loginPage;
    }

    protected <T extends PageBase> T initExpectedPage(Class<T> expectedPage) {
        T pageObject = null;
        try {
            pageObject = expectedPage.getConstructor(WebDriver.class).newInstance(driver);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            try {
                pageObject = PageFactory.initElements(driver, expectedPage);
            } catch (Exception error) {
                error.printStackTrace();
                checkFail("Cannot initialize page " + expectedPage.getSimpleName());
            }
        }
        if (pageObject != null) {
            pageObject.isOpened();
        }
        return pageObject;
    }
}