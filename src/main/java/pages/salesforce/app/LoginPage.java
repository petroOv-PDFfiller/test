package pages.salesforce.app;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.SalesforceBasePage;

import static core.check.Check.checkFalse;
import static core.check.Check.checkTrue;

public class LoginPage extends SalesforceBasePage {

    private By salesforceLogo = By.xpath("//img[@alt='Salesforce']");
    private By loginField = By.id("username");
    private By passwordField = By.id("password");
    private By loginButton = By.id("Login");
    private By loginForm = By.id("login_form");
    private By loginFormError = By.id("error");


    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(waitUntilPageLoaded(), "LoginPage isn`t loaded");
        checkTrue(isElementPresent(salesforceLogo, 60), "Salesforce is not opened");
    }

    @Step
    public void loginToSalesforce(String email, String password) {
        checkTrue(isElementDisplayed(loginField), "Login field is not presented");
        type(loginField, email);
        type(passwordField, password);
        click(loginButton);
        checkFalse(isElementDisplayed(loginFormError, 10), "No errors should be displayed. But got: " + getText(loginFormError));
        checkTrue(isElementDisappeared(loginForm, 10), "Login form is not disappeared");
        checkTrue(waitUntilPageLoaded(), "Cannot load page after login");
    }
}
