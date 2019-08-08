package pages.salesforce.app.airSlate_app.admin_tools.tabs;

import core.check.SoftCheck;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.admin_tools.ASAppAdminToolsPage;
import pages.salesforce.app.airSlate_app.admin_tools.popups.DisconnectAdminPopUp;

import static core.check.Check.checkEquals;
import static data.salesforce.SalesforceTestData.ASAppPageTexts.LOG_IN_AS_AIRSLATE_ADMIN;
import static data.salesforce.SalesforceTestData.ASAppPageTexts.WELCOME_TO_AIRSLATE;
import static pages.salesforce.enums.admin_tools.ASAppAdminToolTabs.ACCOUNT;

public class AccountTab extends ASAppAdminToolsPage {

    private By inputEmail = By.name("email");
    private By inputPassword = By.name("password");
    private By passwordError = By.xpath("//*[@name='password']/following-sibling::*[contains(@class, 'error')]");
    private By btnDisconnectAdminAccount = By.xpath("//button[.='Disconnect Admin Account']");
    private By title = By.xpath("//p[contains(@class,'title')]");

    public AccountTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        skipAdminToolLoader(20);
        checkEquals(getActiveTabName(), ACCOUNT.getName(), "Account tab is not opened");
    }

    @Override
    public boolean isAdminAuthorized() {
        return isElementPresent(btnDisconnectAdminAccount);
    }

    @Step
    public AccountTab setEmail(String email) {
        checkIsElementDisplayed(inputEmail, 4, "Email input");
        typeV2(inputEmail, email);
        return this;
    }

    @Step
    public AccountTab setPassword(String password) {
        checkIsElementDisplayed(inputPassword, 4, "Password input");
        type(inputPassword, password);
        return this;
    }

    @Step
    public AccountTab login() {
        By btnLogin = By.xpath("//button[@type='submit' and .='Log In']");

        checkIsElementDisplayed(btnLogin, 4, "Log In button");
        click(btnLogin);
        isOpened();
        return this;
    }

    @Step
    public <T extends SalesAppBasePage> T signUpAndSave(Class<T> expectedPage) {
        By btnLogin = By.xpath("//button[@type='submit' and .='Sign Up And Save']");

        checkIsElementDisplayed(btnLogin, 4, "Sign Up And Save button");
        click(btnLogin);
        return initExpectedPage(expectedPage);
    }

    @Step
    public DisconnectAdminPopUp disconnectAdmin() {
        checkIsElementDisplayed(btnDisconnectAdminAccount, 4, "Disconnect admin account button");
        click(btnDisconnectAdminAccount);
        DisconnectAdminPopUp popUp = new DisconnectAdminPopUp(driver);
        popUp.isOpened();
        return popUp;
    }

    @Step
    public AccountTab forgotPassword() {
        By btnForgotPassword = By.xpath("//a[contains(@class, 'link__') and text()='Forgot password?']");

        checkIsElementDisplayed(btnForgotPassword, 4, "Forgot password button");
        click(btnForgotPassword);
        isOpened();
        checkForgotPasswordMessages();
        return this;
    }

    @Step
    public AccountTab recoverMyPassword() {
        By btnRecoverPassword = By.xpath("//button[.='Recover My Password']");

        checkIsElementDisplayed(btnRecoverPassword, 4, "Recover password button");
        click(btnRecoverPassword);
        isOpened();
        return this;
    }

    @Step
    public AccountTab returnToLogIn() {
        By btnReturnToLogIn = By.xpath("//button[text()='Return to Log In']");

        checkIsElementDisplayed(btnReturnToLogIn, 4, "Return to login button");
        click(btnReturnToLogIn);
        isOpened();
        return this;
    }

    @Step
    private void checkForgotPasswordMessages() {
        By rememberPassword = By.xpath("//*[contains(@class, 'footerContent__') and text() = 'Remember password?']");
        By pageTitle = By.xpath("//*[contains(@class, 'title__')]");
        By pageDescription = By.xpath("//*[contains(@class, 'description__')]");
        String emailPlaceholder = getAttribute(inputEmail, "placeholder");

        SoftCheck softCheck = new SoftCheck();

        softCheck.checkTrue(isElementDisplayed(rememberPassword, 2), "Remember password option is not displayed");
        softCheck.checkEquals(emailPlaceholder, "name@example.com", "Wrong email placeholder");
        softCheck.checkEquals(getText(pageTitle), "Forgot your password?", "Wrong page title");
        softCheck.checkEquals(getText(pageDescription), "No worries! Let us know your email and we'll send you" +
                " recovery instructions.", "Wrong page description");
        softCheck.checkAll();
    }

    @Step
    public void checkDefaultMessages(boolean passwordEntered) {
        By pageTitle = By.xpath("//*[@data-test='loginTitle']");
        By pageDescription = By.xpath("//*[@data-test='loginSubTitle']");

        SoftCheck softCheck = new SoftCheck();
        softCheck.checkEquals(getAttribute(inputEmail, "placeholder"), "name@example.com", "Wrong email placeholder");
        softCheck.checkEquals(getAttribute(inputPassword, "placeholder"), "Your password", "Wrong password placeholder");
        if (!passwordEntered) {
            softCheck.checkEquals(getAttribute(inputPassword, "value"), "", "Password value is set");
        }
        softCheck.checkEquals(getText(pageTitle), "Log in as airSlate Workspace Admin", "Wrong page title");
        softCheck.checkEquals(getText(pageDescription), "Enter your airSlate account credentials. You'll " +
                "need Admin rights for the Workspace to add teammates and create Flows.", "Wrong page description");
        softCheck.checkAll();
    }

    @Step
    public AccountTab openSignUpInAirSlate() {
        By btnSingUpInAirSlate = By.xpath("//a[text()='Sign Up in airSlate']");

        if (isElementPresent(btnSingUpInAirSlate)) {
            click(btnSingUpInAirSlate);
        }
        checkEquals(getAttribute(title, "textContent"), WELCOME_TO_AIRSLATE,
                "Incorrect page title on signup option");
        return this;
    }

    @Step
    public AccountTab openSignInToAirSlate() {
        By btnSingInToAirSlate = By.xpath("//a[text()='Sign In to airSlate']");

        if (isElementPresent(btnSingInToAirSlate)) {
            click(btnSingInToAirSlate);
        }
        skipAdminToolLoader(5);
        checkEquals(getAttribute(title, "textContent"), LOG_IN_AS_AIRSLATE_ADMIN,
                "Incorrect page title on signin option");
        return this;
    }

    public boolean isEmailErrorShown() {
        By emailError = By.xpath("//*[@name='email']/following-sibling::*[contains(@class, 'error') " +
                "and text()='Please enter a valid email address']");
        return isElementDisplayed(emailError, 2);
    }

    public boolean isPasswordErrorShown() {
        return isElementDisplayed(passwordError, 2);
    }

    public String getPasswordError() {
        return getText(passwordError);
    }

    public String getEmailValue() {
        if (isAdminAuthorized()) {
            By emailData = By.xpath("//*[contains(@class, 'rowHeaders__') and text()='Email']/following-sibling::div");
            return getText(emailData);
        } else {
            return getAttribute(inputEmail, "value");
        }
    }
}
