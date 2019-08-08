package pages.servicenow;

import core.PageBase;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import pages.servicenow.app.ServiceNowTab;

import static core.check.Check.checkTrue;

/**
 * Created by horobets on Aug 05, 2019
 */
public class ServiceNowBasePage extends PageBase {
    private By serviceNowLogoBy = By.cssSelector("[data-original-title^='ServiceNow']");
    private By mainContentFrameBy = By.id("gsft_main");
    private By navDivBy = By.cssSelector(".navpage-nav");
    private By filterFieldBy = By.id("filter");
    private String menuItemXpathFormat = "//li//*[@class='sn-widget-list-title' and text()='%s']";

    public ServiceNowBasePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(waitUntilPageLoaded(), "LoginPage isn`t loaded");

        switchToDefaultContent();
        checkTrue(isElementPresent(serviceNowLogoBy, 60), "ServiceNow Title not loaded");
    }

    public void switchToPageContentFrame() {
        switchToFrame(mainContentFrameBy, 2);
    }

    public void filterMenuItems(String filterString) {
        switchToDefaultContent();
        checkTrue(isElementPresent(filterFieldBy, 12), String.format("%s field is not present", filterFieldBy));
        type(filterFieldBy, filterString);
    }

    public void clickMenuItem(String menuItemName) {
        switchToDefaultContent();
        By menuItemBy = By.xpath(String.format(menuItemXpathFormat, menuItemName));
        checkTrue(isElementPresent(menuItemBy, 12), String.format("Menuitem %s field is not present", menuItemName));
        click(menuItemBy);
    }

    @Step("Open tab: {0}")
    public <T extends ServiceNowBasePage> T openTab(ServiceNowTab tab) {

        filterMenuItems(tab.toString());
        clickMenuItem(tab.toString());

        ServiceNowBasePage serviceNowBasePage = PageFactory.initElements(driver, tab.getPageClass());

        serviceNowBasePage.isOpened();

        return (T) serviceNowBasePage;
    }
}
