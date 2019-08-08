package pages.salesforce.app.airSlate_app.lightning_component;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.airSlate_app.custom_button.LoginPage;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;
import static data.salesforce.SalesforceTestData.ASAppPageTexts.IF_YOU_HAVENT_RECEIVED_AN_INVITE;
import static data.salesforce.SalesforceTestData.ASAppPageTexts.LOG_IN_TO_AIRSLATE;

public class LoginPageComponent extends LoginPage {

    public LoginPageComponent(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        By footer = By.xpath("//*[contains(@class, 'footer__')]");
        checkIsElementDisplayed(title, 15, "Login frame content");
        checkEquals(getText(title), LOG_IN_TO_AIRSLATE, "Incorrect title message on login page");
        checkEquals(getText(footer), IF_YOU_HAVENT_RECEIVED_AN_INVITE, "Incorrect footer message on login page");
    }

    @Override
    public ForgotPasswordPageComponent forgotPassword() {
        By btnForgotPassword = By.xpath("//a[@href='#/restorePassword']");

        checkIsElementDisplayed(btnForgotPassword, 4, "Forgot password button");
        checkTrue(isElementNotDisplayed(loader, 10), "Loader is not hidden");
        //ToDO webdriver error
        scrollIntoView(btnForgotPassword, false);
        click(btnForgotPassword);
        checkTrue(isElementNotDisplayed(loader, 10), "Loader is not hidden");
        return initExpectedPage(ForgotPasswordPageComponent.class);
    }
}
