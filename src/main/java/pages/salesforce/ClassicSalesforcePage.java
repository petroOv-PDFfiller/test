package pages.salesforce;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import static core.check.Check.checkTrue;

public class ClassicSalesforcePage extends SalesforceBasePage {

    private By tabBar = By.id("tabBar");

    public ClassicSalesforcePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "Classic salesforce page is not loaded");
        checkTrue(isElementPresentAndDisplayed(tabBar, 15), "Tab bar is not displayed");
    }

    @Step
    public <T extends SalesforceBasePage> T openRecentItem(String itemName, Class<T> pageClass) {
        By btnRecentItem = By.xpath("//a[.='" + itemName + "']");

        checkTrue(isElementPresentAndDisplayed(btnRecentItem, 5), "Recent Item is not displayed");
        click(btnRecentItem);
        SalesforceBasePage salesforceBasePage = PageFactory.initElements(driver, pageClass);
        salesforceBasePage.isOpened();
        return (T) salesforceBasePage;
    }
}
