package pages.salesforce.app.sf_setup;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.admin_tools.AdminToolsPage;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.admin_tools.ASAppAdminToolsPage;
import utils.TimeMan;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class InstalledPackagesPage extends SalesAppBasePage {
    private By pageTitle = By.xpath("//h1[contains(@class, 'noSecondHeader')]");

    public InstalledPackagesPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(waitUntilPageLoaded(), "InstalledPackagesPage is not loaded");
        waitForSalesforceLoading();
        checkTrue(isElementPresentAndDisplayed(iframe, 25), "Vframe is not present");
        switchToFrame(iframe);
        TimeMan.sleep(1);
        checkTrue(isElementPresentAndDisplayed(pageTitle, 15), "Page title is not displayed");
        checkEquals(getText(pageTitle), "Installed Packages", "Wrong page is opened");
    }

    @Step
    public AdminToolsPage openDaDaDocsAdminTools() {
        By configureDaDaDocs = By.xpath("//td[text() ='PDFfiller']/ancestor::tr//a[text()='Configure']");

        checkTrue(isElementPresentAndDisplayed(configureDaDaDocs, 5), "DaDaDocs configure button is not displayed");
        int windowsNumber = driver.getWindowHandles().size();
        clickJS(configureDaDaDocs);
        switchToIfWindowsNumberIs(windowsNumber, asAppAdminToolPageURLPath);
        checkTrue(isElementDisappeared(configureDaDaDocs, 5), "Configure btn is still present");
        AdminToolsPage adminToolPage = new AdminToolsPage(driver);
        adminToolPage.isOpened();
        return adminToolPage;
    }

    @Step
    public ASAppAdminToolsPage openASAppAdminTools() {
        By configureASApp = By.xpath("//td[text() ='airSlate']/ancestor::tr//a[text()='Configure']");

        checkTrue(isElementPresentAndDisplayed(configureASApp, 5), "airSlate configure button is not displayed");
        clickJS(configureASApp);
        driverWinMan.switchToWindow(asAppAdminToolPageURLPath);
        driverWinMan.keepOnlyWindow(driverWinMan.getCurrentWindow());
        return initExpectedPage(ASAppAdminToolsPage.class);
    }
}
