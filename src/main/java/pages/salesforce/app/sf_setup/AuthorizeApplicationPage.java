package pages.salesforce.app.sf_setup;

import core.PageBase;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.admin_tools.tabs.UsersTab;

import static core.check.Check.checkTrue;

public class AuthorizeApplicationPage extends PageBase {
    private By authorizeBlock = By.xpath("//div[@class='api-authorize']");
    private By btnAuthorize = By.xpath("//button[contains(@class, 'authorize-btn')]");

    public AuthorizeApplicationPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        driver.switchTo().defaultContent();
        checkTrue(waitUntilPageLoaded(), "AuthorizeApplicationPage is not loaded");
        checkTrue(isElementPresent(authorizeBlock, 30), "authorize block is not present");
    }

    @Step("Authorize DaDaDocs")
    public UsersTab authorizeDaDaDocs() {
        checkTrue(isElementPresent(btnAuthorize), "Authorize button is not present");
        click(btnAuthorize);
        UsersTab page = new UsersTab(driver);
        page.isOpened();
        return page;
    }
}
