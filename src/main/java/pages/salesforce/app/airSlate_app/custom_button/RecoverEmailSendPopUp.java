package pages.salesforce.app.airSlate_app.custom_button;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;
import pages.salesforce.enums.PopUpActions;


public class RecoverEmailSendPopUp extends SalesforceBasePopUp {

    public RecoverEmailSendPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        By popUpBody = By.xpath("//*[contains(@class, 'infoModal__')]");
        checkIsElementDisplayed(popUpBody, 10, "Pop up is not opened");
    }

    @Step
    public String getPopUpText() {
        By popUpText = By.xpath("//p[contains(@class, 'info__')]");

        checkIsElementDisplayed(popUpText, 5, "Pop up text");
        return getAttribute(popUpText, "textContent");
    }

    @Step("Return to log in")
    public <T extends SalesAppBasePage> T returnToLogIn(Class<T> expectedPage) {
        return choosePopUpAction(PopUpActions.RETURN_TO_LOG_IN, expectedPage);
    }

    public String getExpectedPopUpText(String email) {
        return String.format("Done! Check you inbox %s for password reset instructions.", email);
    }
}
