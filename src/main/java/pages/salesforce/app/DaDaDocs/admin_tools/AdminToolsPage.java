package pages.salesforce.app.DaDaDocs.admin_tools;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.AuthorizationTab;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.LayoutsTab;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.SettingsTab;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.UsersTab;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.interfaces.AdminTool;
import pages.salesforce.enums.SaleforceMyDocsTab;
import pages.salesforce.enums.admin_tools.AdminToolTabs;
import utils.Logger;

import static core.check.Check.checkTrue;

public class AdminToolsPage extends SalesAppBasePage implements AdminTool {

    protected By navBar = By.xpath("//div[contains(@class,'navbarContainer__')]");
    protected By popUp = By.xpath("//*[contains(@class,'popup__container__')]");
    protected By btnAcceptPopUp = By.xpath("//*[contains(@class,'popup__container__')]//button[contains(@class,'btn__accent__')]");
    protected By notificationPopUp = By.xpath("//*[contains(@class,'toastrWrap__')]");

    public AdminToolsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "Admin tool page is not loaded");
        if (isElementPresent(iframe)) {
            checkTrue(isElementPresentAndDisplayed(iframe, 10), "Iframe is not displayed");
            checkTrue(switchToFrame(iframe), "Cannot switch to admin tools frame");
        }
        checkTrue(isElementPresentAndDisplayed(navBar, 60), "nav bar is not displayed");
        checkTrue(isElementNotDisplayed(newLoader, 90), "Loader is still present");
    }

    public <T extends AdminToolsPage> T getActiveTab() {
        By tab;
        AdminToolsPage adminToolPage = null;
        tab = By.xpath("//a[contains(@class, 'navbarLink__') and contains(@class, 'isActive__')]//span");
        checkTrue(isElementPresent(tab, 16), "AdminToolPage is not opened");
        for (int i = 0; i < AdminToolTabs.values().length; i++) {
            if (getText(tab).equals(SaleforceMyDocsTab.values()[i].getName())) {
                adminToolPage = PageFactory.initElements(driver, AdminToolTabs.values()[i].getExpectedPage());
                break;
            }
        }

        return (T) adminToolPage;
    }

    public String getActiveTabName() {
        By tab = By.xpath("//a[contains(@class, 'navbarLink__') and contains(@class, 'isActive__')]//span");
        checkTrue(isElementPresent(tab, 16), "AdminToolPage is not opened");
        return getText(tab);
    }

    @Step("Open tab: {0}")
    public <T extends AdminToolsPage> T openTab(AdminToolTabs tab) {
        AdminToolsPage adminToolPage = null;
        String tabName = tab.getName();
        By adminToolTab = By.xpath("//a[contains(@class, 'navbarLink__')]//span[text() = '" + tabName + "']/parent::a");
        skipAdminToolLoader();
        checkTrue(isElementPresentAndDisplayed(adminToolTab, 16), "tab " + tabName + " is not present");

        if (!getActiveTabName().equals(tabName)) {
            skipAdminToolLoader();
            click(adminToolTab);
        }

        switch (tab) {
            case AUTHORIZATION: {
                adminToolPage = new AuthorizationTab(driver);
                break;
            }
            case USERS: {
                adminToolPage = new UsersTab(driver);
                break;
            }
            case LAYOUTS: {
                adminToolPage = new LayoutsTab(driver);
                break;
            }
            case SETTINGS: {
                adminToolPage = new SettingsTab(driver);
                break;
            }
        }
        adminToolPage.isOpened();

        return (T) adminToolPage;
    }

    @Step("Check is tab disabled: {0}")
    public boolean isTabDisabled(AdminToolTabs tab) {
        String tabName = tab.getName();
        By adminToolTab = By.xpath("//a[contains(@class, 'navbarLink__')]//span[text() = '" + tabName + "']/parent::a");
        By adminToolTabWrapper = By.xpath("//div[@aria-label = '" + tabName + "']");
        skipAdminToolLoader();
        checkTrue(isElementPresentAndDisplayed(adminToolTab, 16), "tab " + tabName + " is not present");
        String currentTabName = getActiveTabName();

        if (isElementContainsStringInAttribute(adminToolTab, "class", "disabled__", 3)
                && getActiveTabName().equals(AdminToolTabs.AUTHORIZATION.getName())
                && !isAuthorized()) {
            By lockedIcon = By.xpath("//a[contains(@class, 'navbarLink__')]//span[text() = '" + tabName + "']/parent::a" +
                    "//i[contains(@class, 'lockedIcon__')]");
            checkTrue(isElementPresentAndDisplayed(lockedIcon, 5), "Locked Icon");
        }
        checkTrue(isElementPresentAndDisplayed(adminToolTabWrapper, 2), "Wrapper block is not displayed");
        click(adminToolTabWrapper);
        return getActiveTabName().equals(currentTabName);
    }

    @Step("Check is tab disabled: {0}")
    public boolean isTabPresent(AdminToolTabs tab) {
        By adminToolTab = By.xpath("//a[contains(@class, 'navbarLink__')]//span[text() = '" + tab.getName() + "']/parent::a");

        return isElementPresentAndDisplayed(adminToolTab, 2);
    }

    public boolean isAuthorized() {
        By info = By.xpath("//div[@aria-label = 'Authorize administrator account to use settings']");
        return isElementDisappeared(info, 1);
    }

    @Step("Check if notification contains message:({0})")
    public boolean notificationMessageContainsText(String message) {
        checkTrue(isElementPresentAndDisplayed(notificationPopUp, 10), "Pop up is not displayed");
        String actualMessage = getAttribute(notificationPopUp, "innerText");
        Logger.info("Actual message is - " + actualMessage);
        return actualMessage.contains(message);
    }

    @Override
    public void skipAdminToolLoader(int seconds) {
        checkTrue(isElementNotDisplayed(newLoader, seconds), "Loader is still present");
    }
}
