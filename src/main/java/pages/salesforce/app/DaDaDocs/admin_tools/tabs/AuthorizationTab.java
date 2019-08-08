package pages.salesforce.app.DaDaDocs.admin_tools.tabs;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.admin_tools.AdminToolsPage;
import pages.salesforce.app.sf_setup.AuthorizeApplicationPage;
import utils.Logger;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class AuthorizationTab extends AdminToolsPage {
    private By btnMainAction = By.xpath("//*[contains(@class,'btn__accent__')]");
    private By inputEmail = By.xpath("//input[@name='email']");
    private By emailError = By.xpath("//input[@name='email']/parent::div/span[contains(@class, 'error__')]");
    private By inputPassword = By.xpath("//input[@name='password']");
    private By passwordError = By.xpath("//input[@name='password']/parent::div/span[contains(@class, 'error__')]");
    private By btnForgotPassword = By.xpath("//a[contains(@href, 'forgotPassword')]");
    private By btnSignUp = By.xpath("//a[contains(@href, 'signup')]");
    private By btnSignIn = By.xpath("//a[contains(@href, 'login')]");
    private By pageTitle = By.xpath("//p[contains(@class, 'title__')]");

    public AuthorizationTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(pageTitle, 15), "pageTitle is not displayed");
        skipAdminToolLoader();
    }

    @Override
    public boolean isAuthorized() {
        checkTrue(isElementPresent(pageTitle, 5), "Title is not displayed");
        return getText(pageTitle).equals("DaDaDocs Admin Account");
    }

    @Step("Logout if authorized")
    public AuthorizationTab logOut() {
        checkTrue(isElementNotDisplayed(newLoader, 10), "Loader is displayed");
        if (isAuthorized()) {
            Logger.info("Log out from account");
            checkTrue(isElementPresentAndDisplayed(btnMainAction, 5), "Log out button is not displayed");
            click(btnMainAction);
            checkTrue(isElementPresentAndDisplayed(popUp, 5), "PopUp is not displayed");
            checkTrue(isElementPresentAndDisplayed(btnAcceptPopUp, 5), "Disconnect button is not displayed");
            click(btnAcceptPopUp);
            notificationMessageContainsText("Admin account has been successfully disconnected!");
            Logger.info("Log out operation is successful");
            checkTrue(isElementDisappeared(popUp, 5), "Notification popUp is not disappeared");
        }
        isOpened();
        Logger.info("Admin is not authorized");
        return this;
    }

    @Step("Enter email: ({0}) , enter password: ({1})")
    public AuthorizationTab enterEmailAndPassword(String email, String password) {
        checkTrue(isElementPresentAndDisplayed(inputEmail, 5), "Email input is not displayed");
        type(inputEmail, email);
        checkTrue(isElementPresentAndDisplayed(inputPassword, 5), "Password input is not displayed");
        type(inputPassword, password);
        isOpened();
        return this;
    }

    @Step("Try to authorize")
    public void tryToAuthorize() {
        checkTrue(isElementPresentAndDisplayed(btnMainAction, 5), "Log out button is not displayed");
        click(btnMainAction);
    }

    public AuthorizeApplicationPage redirectToAuthorizeApplication() {
        AuthorizeApplicationPage authorizeApplicationPage = new AuthorizeApplicationPage(driver);
        authorizeApplicationPage.isOpened();
        return authorizeApplicationPage;
    }

    @Step("Open Sign Up page")
    public AuthorizationTab openSignUp() {
        if (isElementNotDisplayed(btnSignIn, 2)) {
            checkTrue(isElementPresentAndDisplayed(btnSignUp, 5), "SignUpButton is not present");
            click(btnSignUp);
        }
        isOpened();
        return this;
    }

    @Step("Open Sign In page")
    public AuthorizationTab openSignIn() {
        if (isElementNotDisplayed(btnSignUp, 2)) {
            checkTrue(isElementPresentAndDisplayed(btnSignIn, 5), "SignInButton is not present");
            click(btnSignIn);
        }
        isOpened();
        return this;
    }

    @Step("Open Sign In page")
    public AuthorizationTab forgetPassword(String email) {
        checkTrue(isElementPresentAndDisplayed(btnForgotPassword, 5), "Forgot button is not present");
        click(btnForgotPassword);
        isOpened();
        checkEquals(getText(pageTitle), "Forgot your password?", "Forgot your password page is not opened");
        checkTrue(isElementPresentAndDisplayed(inputEmail, 5), "Email input is not displayed");
        clear(inputEmail);
        type(inputEmail, email);
        click(btnMainAction);
        checkTrue(isElementPresentAndDisplayed(popUp, 5), "popup is not displayed");
        click(btnAcceptPopUp);
        isOpened();
        return this;
    }
}
