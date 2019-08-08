package pages.salesforce.app.airSlate_app.custom_button;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;

import static com.codeborne.selenide.Selenide.$;
import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;
import static data.salesforce.SalesforceTestData.ASAppPageTexts.IF_YOU_HAVENT_RECEIVED_AN_INVITE;
import static data.salesforce.SalesforceTestData.ASAppPageTexts.LOG_IN_TO_AIRSLATE;

public class LoginPage extends SalesAppBasePage {

    protected By title = By.xpath("//*[@data-test='loginTitle']");

    public LoginPage(WebDriver driver) {
        super(driver);
        iframe = By.xpath("//iframe[@title = 'airSlate App']");
        loader = By.xpath("//*[contains(@class, 'loaderWrapper__')]");
    }

    @Override
    public void isOpened() {
        By footer = By.xpath("//*[contains(@class, 'footer__')]");

        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "Login page is not loaded");
        checkIsElementDisplayed(iframe, 15, "login page frame");
        checkTrue(switchToFrame(iframe, 15), "Cannot switch to login page frame");
        checkIsElementDisplayed(title, 15, "Login frame content");
        checkEquals(getText(title), LOG_IN_TO_AIRSLATE, "Incorrect title message on login page");
        checkEquals(getText(footer), IF_YOU_HAVENT_RECEIVED_AN_INVITE, "Incorrect footer message on login page");
    }

    @Step
    public LoginPage enterPassword(String password) {
        By inputPassword = By.name("password");

        checkTrue(isElementNotDisplayed(loader, 10), "Loader is not hidden");
        checkIsElementDisplayed(inputPassword, 4, "Password input");
        type(inputPassword, password);
        return this;
    }

    @Step
    public LoginPage enterLogin(String login) {
        By inputLogin = By.name("email");

        checkTrue(isElementNotDisplayed(loader, 10), "Loader is not hidden");
        checkIsElementDisplayed(inputLogin, 4, "login input");
        type(inputLogin, login);
        return this;
    }

    @Step
    public <T extends SalesAppBasePage> T clickOnLogIn(Class<T> expectedPage) {
        By btnLogIn = By.xpath("//button[. = 'Log In']");

        checkTrue(isElementNotDisplayed(loader, 10), "Loader is not hidden");
        checkIsElementDisplayed(btnLogIn, 4, "Log In button");
        click(btnLogIn);
        $(loader).waitUntil(Condition.hidden, 120000);
        return initExpectedPage(expectedPage);
    }

    @Step
    public ForgotPasswordPage forgotPassword() {
        By btnForgotPassword = By.xpath("//a[@href='#/restorePassword']");

        checkIsElementDisplayed(btnForgotPassword, 4, "Forgot password button");
        checkTrue(isElementNotDisplayed(loader, 10), "Loader is not hidden");
        click(btnForgotPassword);
        checkTrue(isElementNotDisplayed(loader, 10), "Loader is not hidden");
        return initExpectedPage(ForgotPasswordPage.class);
    }
}
