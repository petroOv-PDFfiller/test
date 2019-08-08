package pages.salesforce.app.DaDaDocs.full_app.V3;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.enums.V3.DaDaDocsV3Tabs;
import utils.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static core.check.Check.checkTrue;

public class DaDaDocsFullAppV3 extends SalesAppBasePage {

    public SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm");
    private By templateFrame = By.xpath("//div[contains(@class, 'slds-template_iframe')]");
    private By tabs = By.xpath("//div[contains(@class, 'grid__tabs-nav__')]");
    private By activeTab = By.xpath("//a[contains(@class, 'tabs__nav-item__') and contains(@class, 'is-active-tab__')]//span");
    private List<String> breadcrumbs;
    private By breadcrumbsFolder = By.xpath("//div[contains(@class, 'breadcrumbs__')]//*[contains(@class, 'controlWrapper__')]");

    public DaDaDocsFullAppV3(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "Dadadocs v3 full app is not loaded");
        if (isElementPresent(btnAppLauncher)) {
            checkTrue(isElementPresentAndDisplayed(templateFrame, 25), "Template frame is not displayed");
            checkTrue(isElementPresentAndDisplayed(iframe, 20), "DDD frame is not present");
            Logger.info("Switch to DDD full app frame");
            checkTrue(switchToFrame(iframe, 5), "Cannot switch to DDD fullApp v3 frame");
        }
        checkTrue(isElementPresentAndDisplayed(tabs, 35), "DaDaDocs v3 full application was not loaded");
        skipLoader();
    }

    public void skipLoader() {
        By loader = By.xpath("//*[contains(@class,'loader__')]");
        checkTrue(isElementNotDisplayed(loader, 60), "Loader is not disappeared");
    }

    public <T extends DaDaDocsFullAppV3> T getActiveTab() {
        DaDaDocsFullAppV3 fullAppV3 = null;
        checkTrue(isElementPresent(activeTab, 16), "AdminToolPage is not opened");
        for (int i = 0; i < DaDaDocsV3Tabs.values().length; i++) {
            if (getText(activeTab).equals(DaDaDocsV3Tabs.values()[i].getName())) {
                fullAppV3 = PageFactory.initElements(driver, DaDaDocsV3Tabs.values()[i].getExpectedPage());
                break;
            }
        }

        return (T) fullAppV3;
    }

    public String getActiveTabName() {
        checkTrue(isElementPresent(activeTab, 16), "DaDaDocs page is not opened");
        return getText(activeTab);
    }

    @Step("Open tab: {0}")
    public <T extends DaDaDocsFullAppV3> T openTab(DaDaDocsV3Tabs tab) {
        String tabName = tab.getName();
        DaDaDocsFullAppV3 fullAppV3 = null;
        By daDaDocsTab = By.xpath("//*[@aria-label = '" + tabName + "']");

        if (!getActiveTabName().equals(tabName)) {
            click(daDaDocsTab);
        }
        fullAppV3 = PageFactory.initElements(driver, tab.getExpectedPage());
        fullAppV3.isOpened();
        return (T) fullAppV3;
    }

    @Step("Check if notification contains message:({0})")
    public boolean notificationMessageContainsText(String message) {
        By notificationPopUp = By.xpath("//div[contains(@class, 'success__')]");
        checkTrue(isElementPresentAndDisplayed(notificationPopUp, 10), "Pop up is not displayed");
        String actualMessage = getAttribute(notificationPopUp, "innerText");
        Logger.info("Actual message is - " + actualMessage);
        return actualMessage.trim().contains(message);
    }

    @Step
    public List<String> initBreadcrumbs() {
        By breadcrumbsLinks;
        if (isBreadcrumbsFolderDisplayed()) {
            breadcrumbsLinks = By.xpath("//div[contains(@class, 'breadcrumbs__')]//a[contains(@class, 'listItem_')]");
        } else {
            breadcrumbsLinks = By.xpath("//div[contains(@class, 'breadcrumbsItem__')]");
        }
        Logger.info("Init breadcrumbs");
        breadcrumbs = new ArrayList<>();
        driver.findElements(breadcrumbsLinks).forEach(bc -> {
            breadcrumbs.add(bc.getAttribute("innerText").trim());
        });
        return breadcrumbs;
    }

    @Step
    public boolean isBreadcrumbsFolderDisplayed() {
        return isElementDisplayed(breadcrumbsFolder);
    }

    @Step
    public <T extends SalesAppBasePage> T openPageByBreadcrumbsFolder(String pageName, Class<T> expectedPage) {
        By folderElement = By.xpath("//div[contains(@class, 'breadcrumbs__')]//*[@data-test='listItem' and .='" + pageName + "']");
        checkTrue(isBreadcrumbsFolderDisplayed(), "Breadcrumbs folder is not displayed");
        click(breadcrumbsFolder);
        checkTrue(isElementPresentAndDisplayed(folderElement, 2), "Element is not present in breadcrumbs folder");
        click(folderElement);
        SalesAppBasePage page = PageFactory.initElements(driver, expectedPage);
        page.isOpened();
        return (T) page;
    }
}
