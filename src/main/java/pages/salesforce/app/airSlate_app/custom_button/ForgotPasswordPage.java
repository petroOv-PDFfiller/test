package pages.salesforce.app.airSlate_app.custom_button;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;
import static data.salesforce.SalesforceTestData.ASAppPageTexts.FORGOT_YOUR_PASSWORD;
import static data.salesforce.SalesforceTestData.ASAppPageTexts.RECOVERY_INSTRUCTIONS;

public class ForgotPasswordPage extends SalesAppBasePage {

    protected By title = By.xpath("//*[contains(@class, 'title__')]");

    public ForgotPasswordPage(WebDriver driver) {
        super(driver);
        loader = By.xpath("//*[contains(@class, 'loaderWrapper__')]");
    }

    @Override
    public void isOpened() {
        By description = By.xpath("//*[contains(@class, 'description__')]");

        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "Forgot password page is not loaded");
        checkIsElementDisplayed(iframe, 10, "Forgot password page frame");
        checkTrue(switchToFrame(iframe, 15), "Cannot switch to login page frame");
        checkIsElementDisplayed(title, 15, "Login frame content");
        checkEquals(getText(title), FORGOT_YOUR_PASSWORD, "Incorrect title message on forgot password page");
        checkEquals(getText(description), RECOVERY_INSTRUCTIONS, "Incorrect text message on forgot password page");
    }

    @Step
    public LoginPage returnToLogIn() {
        By btnReturnLogIn = By.xpath("//*[contains(@class, 'returnToLoginBtn__')]");

        checkIsElementDisplayed(btnReturnLogIn, 4, "return to log in button");
        click(btnReturnLogIn);
        checkTrue(isElementNotDisplayed(loader, 10), "Loader is not hidden");
        return initExpectedPage(LoginPage.class);
    }

    @Step
    public ForgotPasswordPage enterEmail(String email) {
        By inputEmail = By.name("email");

        checkIsElementDisplayed(inputEmail, 4, "Email input");
        type(inputEmail, email);
        return this;
    }

    @Step
    public <T extends SalesAppBasePage> T recoverMyPassword(Class<T> expectedPage) {
        By btnRecoverPassword = By.xpath("//button[. = 'Recover My Password']");

        checkIsElementDisplayed(btnRecoverPassword, 4, "Button recover my password");
        click(btnRecoverPassword);
        checkTrue(isElementNotDisplayed(loader, 10), "Loader is not hidden");
        return initExpectedPage(expectedPage);
    }

    @Step
    public String getEmailError() {
        By emailError = By.xpath("//*[@name='email']/following::span");

        checkIsElementDisplayed(emailError, 4, "Email error");
        return getAttribute(emailError, "textContent");
    }

    public String getNotificationMessage() {
        By notificationMessage = By.xpath("//*[contains(@class,'toastrMessage__')]");

        checkIsElementDisplayed(notificationMessage, 4, "Notification message");
        return getText(notificationMessage);
    }
}
