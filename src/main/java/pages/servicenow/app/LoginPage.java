package pages.servicenow.app;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.servicenow.ServiceNowBasePage;

import static core.check.Check.checkTrue;

/**
 * Created by horobets on Aug 05, 2019
 */
public class LoginPage extends ServiceNowBasePage {

    private By loginFieldBy = By.id("user_name");
    private By passwordFieldBy = By.id("user_password");
    private By loginButtonBy = By.id("sysverb_login");
    private By loginFormBy = By.id("loginPage");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        super.isOpened();
        switchToPageContentFrame();
        checkTrue(isElementDisplayed(loginFieldBy), "Login field is not presented");
        switchToDefaultContent();
    }

    @Step
    public void loginToServiceNow(String email, String password) {
        switchToPageContentFrame();
        checkTrue(isElementDisplayed(loginFieldBy), "Login field is not presented");
        type(loginFieldBy, email);
        type(passwordFieldBy, password);
        click(loginButtonBy);
        checkTrue(isElementDisappeared(loginFormBy, 10), "Login form is still opened");
        checkTrue(waitUntilPageLoaded(), "Cannot load page after login");
    }
}
