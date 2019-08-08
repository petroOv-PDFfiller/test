package pages.salesforce.app;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.SalesforceBasePage;
import pages.salesforce.app.DaDaDocs.admin_tools.AdminToolsPage;
import pages.salesforce.app.sf_objects.SalesAppHomePage;

import static core.check.Check.checkTrue;

public class AppLauncherPopUp extends SalesforceBasePage {

    private By appAppsTitle = By.xpath("//span[text()='All Apps']");
    private By btnSalesApp = By.xpath("//a[@class='appTileTitle'][.='Sales']");
    private By btnAdminTool = By.xpath("//span[.='Admin Tools']/span");

    public AppLauncherPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(waitUntilPageLoaded(), "AppLauncherPopUp page isn`t loaded");
        waitForSalesforceLoading();
        checkTrue(isElementPresentAndDisplayed(appAppsTitle, 10), "App launcher is not opened");
    }

    @Step
    public SalesAppHomePage openSalesAppPage() {
        checkTrue(isElementPresentAndDisplayed(btnSalesApp, 10), "Sales app button is not presented");
        clickJS(btnSalesApp);
        checkTrue(isElementNotDisplayed(btnSalesApp, 10), "Sales app button is still present");
        SalesAppHomePage salesAppHomePage = new SalesAppHomePage(driver);
        salesAppHomePage.isOpened();
        return salesAppHomePage;
    }

    @Step
    public AdminToolsPage openAdminToolPage() {
        checkTrue(isElementPresentAndDisplayed(btnAdminTool, 10), "Admin tool button is not presented");
        click(btnAdminTool);
        checkTrue(isElementNotDisplayed(btnSalesApp, 10), "Sales app button is still present");
        AdminToolsPage adminToolsPage = new AdminToolsPage(driver);
        adminToolsPage.isOpened();
        return adminToolsPage;
    }
}
