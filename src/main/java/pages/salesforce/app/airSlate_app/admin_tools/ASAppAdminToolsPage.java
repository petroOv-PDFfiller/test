package pages.salesforce.app.airSlate_app.admin_tools;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.admin_tools.tabs.AccountTab;
import pages.salesforce.app.interfaces.AdminTool;
import pages.salesforce.enums.admin_tools.ASAppAdminToolTabs;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class ASAppAdminToolsPage extends SalesAppBasePage implements AdminTool {

    private By navBar = By.xpath("//div[contains(@class,'navbar__')]");

    public ASAppAdminToolsPage(WebDriver driver) {
        super(driver);
        iframe = By.xpath("//iframe[@title='airSlate App']");
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "Admin tool page is not loaded");
        if (isElementPresent(btnAppLauncher, 2)) {
            checkTrue(isElementPresentAndDisplayed(iframe, 15), "Iframe is not displayed");
            checkTrue(switchToFrame(iframe), "Cannot switch to admin tools frame");
        }
        checkTrue(isElementPresent(navBar, 30), "Nav bar menu is not appeared");
        skipAdminToolLoader(60);
        checkIsElementDisplayed(navBar, 10, "Nav bar");
    }

    @Override
    @Step
    public void skipAdminToolLoader(int seconds) {
        checkTrue(isElementNotDisplayed(newLoader, seconds), "AS admin tool loader is still present");
    }

    @Step
    public boolean isAdminAuthorized() {
        ASAppAdminToolTabs currentTab = getActiveTab();
        AccountTab accountPage = openTab(ASAppAdminToolTabs.ACCOUNT);
        boolean isAuthorized = accountPage.isAdminAuthorized();
        accountPage.openTab(currentTab);
        return isAuthorized;
    }

    protected String getActiveTabName() {
        By tab = By.xpath("//a[contains(@class, 'navbarLink__') and contains(@class, 'isActive__')]//span");
        checkTrue(isElementPresent(tab, 16), "AdminToolPage is not opened");
        return getText(tab);
    }

    public ASAppAdminToolTabs getActiveTab() {
        String currentTab = getActiveTabName();
        for (int i = 0; i < ASAppAdminToolTabs.values().length; i++) {
            if (currentTab.equals(ASAppAdminToolTabs.values()[i].getName())) {
                return ASAppAdminToolTabs.values()[i];
            }
        }
        return null;
    }

    @Step
    public <T extends ASAppAdminToolsPage> T openTab(ASAppAdminToolTabs tab) {
        By tabLink = By.xpath("//a[contains(@class, 'navbarLink__') and .='" + tab.getName() + "']");

        checkTrue(isElementPresentAndDisplayed(tabLink, 4), tab.getName() + " link is not available");
        click(tabLink);
        ASAppAdminToolsPage expectedPage = PageFactory.initElements(driver, tab.getExpectedPage());
        expectedPage.isOpened();
        return (T) expectedPage;
    }

    @Step
    public void checkPermissionIcon(ASAppAdminToolTabs tab) {
        By permissionHint = By.xpath("//a[contains(@class, 'navbarLink__') and .='" + tab.getName() + "']//*[contains(@class, 'permissionHint__')]");
        By permissionIcon = By.xpath("//a[contains(@class, 'navbarLink__') and .='" + tab.getName() + "']//*[contains(@class, 'permissionIcon__')]");

        checkIsElementDisplayed(permissionIcon, 2, "Permission icon for tab " + tab.getName());
        hoverOver(permissionIcon);
        checkIsElementDisplayed(permissionHint, 2, "Permission hint");
        checkEquals(getAttribute(permissionHint, "aria-label"), tab.getPermissionsErrorMessage(),
                "Incorrect hint message");
    }

    @Step
    public void waitForNotificationDisappered() {
        By toaster = By.xpath("//*[contains(@class, 'toastrContent__')]");
        new WebDriverWait(driver, 30).until(ExpectedConditions.numberOfElementsToBe(toaster, 0));
    }

    public boolean isTabLocked(ASAppAdminToolTabs tab) {
        By tabLink = By.xpath("//a[contains(@class, 'navbarLink__') and .='" + tab.getName() + "']//*[contains(@class, 'permissionIcon__')]");
        return isElementPresent(tabLink);
    }

    public String getNotificationMessage() {
        By notificationMessage = By.xpath("//*[contains(@class,'toastrMessage__')]");

        checkIsElementDisplayed(notificationMessage, 4, "Notification message");
        return getText(notificationMessage);
    }
}
