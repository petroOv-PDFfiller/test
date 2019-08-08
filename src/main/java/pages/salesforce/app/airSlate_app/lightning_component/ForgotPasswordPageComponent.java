package pages.salesforce.app.airSlate_app.lightning_component;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.airSlate_app.custom_button.ForgotPasswordPage;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;
import static data.salesforce.SalesforceTestData.ASAppPageTexts.FORGOT_YOUR_PASSWORD;
import static data.salesforce.SalesforceTestData.ASAppPageTexts.RECOVERY_INSTRUCTIONS;

public class ForgotPasswordPageComponent extends ForgotPasswordPage {

    public ForgotPasswordPageComponent(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        By description = By.xpath("//*[contains(@class, 'description__')]");
        checkIsElementDisplayed(title, 15, "forgot password frame content");
        checkEquals(getText(title), FORGOT_YOUR_PASSWORD, "Incorrect title message on forgot password page");
        checkEquals(getText(description), RECOVERY_INSTRUCTIONS, "Incorrect text message on forgot password page");
    }

    @Override
    public LoginPageComponent returnToLogIn() {
        By btnReturnLogIn = By.xpath("//*[contains(@class, 'returnToLoginBtn__')]");

        checkIsElementDisplayed(btnReturnLogIn, 4, "return to log in button");
        click(btnReturnLogIn);
        checkTrue(isElementNotDisplayed(loader, 10), "Loader is not hidden");
        return initExpectedPage(LoginPageComponent.class);

    }
}
