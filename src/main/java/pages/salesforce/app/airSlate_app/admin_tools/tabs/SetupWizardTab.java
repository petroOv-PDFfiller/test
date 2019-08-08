package pages.salesforce.app.airSlate_app.admin_tools.tabs;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.airSlate_app.admin_tools.ASAppAdminToolsPage;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;
import static data.salesforce.SalesforceTestData.ASAppPageTexts.LOG_IN_AS_AIRSLATE_ADMIN;
import static pages.salesforce.enums.admin_tools.ASAppAdminToolTabs.SETUP_WIZARD;

public class SetupWizardTab extends ASAppAdminToolsPage {

    private By inputEmail = By.name("email");
    private By inputPassword = By.name("password");
    private By title = By.xpath("//p[contains(@class,'title')]");

    public SetupWizardTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        skipAdminToolLoader(20);
        checkEquals(getActiveTabName(), SETUP_WIZARD.getName(), "Setup wizard tab is not opened");
    }

    @Step
    public SetupWizardTab setEmail(String email) {
        checkIsElementDisplayed(inputEmail, 4, "Email input");
        type(inputEmail, email);
        return this;
    }

    @Step
    public SetupWizardTab setPassword(String password) {
        checkIsElementDisplayed(inputPassword, 4, "Password input");
        type(inputPassword, password);
        return this;
    }

    @Step
    public SetupWizardTab login() {
        By btnLogin = By.xpath("//button[@type='submit' and .='Log in']");

        checkIsElementDisplayed(btnLogin, 4, "Login button");
        click(btnLogin);
        isOpened();
        return this;
    }

    @Step
    public SetupWizardTab openSignInToAirSlate() {
        By btnSingInToAirSlate = By.xpath("//a[text()='Sign In to airSlate']");

        if (isElementPresent(btnSingInToAirSlate)) {
            click(btnSingInToAirSlate);
        }
        skipAdminToolLoader(5);
        checkEquals(getAttribute(title, "textContent"), LOG_IN_AS_AIRSLATE_ADMIN,
                "Incorrect page title on signin option");
        return this;
    }

    @Step
    public SetupWizardTab selectWorkspace(String name) {
        By workspace = By.xpath("//*[contains(@class, 'workspaceItem__') and contains(.,'" + name + "')]//button");

        checkTrue(isElementPresent(workspace), name + " Workspace is not present");
        scrollTo(workspace);
        click(workspace);
        isOpened();
        return this;
    }

    @Step
    public SetupWizardTab skipForNow() {
        By btnSkipForNow = By.xpath("//button[.='Skip for Now']");

        checkIsElementDisplayed(btnSkipForNow, 10, "Skip for now button");
        click(btnSkipForNow);
        isOpened();
        return this;
    }

    @Step
    public DashboardTab skip() {
        By btnSkip = By.xpath("//button[.='Skip']");

        checkIsElementDisplayed(btnSkip, 10, "Skip button");
        click(btnSkip);
        return initExpectedPage(DashboardTab.class);
    }
}
