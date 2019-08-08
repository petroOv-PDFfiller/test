package pages.salesforce.app;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import pages.salesforce.SalesforceBasePage;
import utils.Logger;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static core.check.Check.checkTrue;

public class ChangePasswordPage extends SalesforceBasePage {
    @Getter
    private SelenideElement pageHeader = $x("//*[@id = 'header' and text()='Change Your Password']");

    @Getter
    private SelenideElement btnCancel = $("#cancel-button");

    public ChangePasswordPage(WebDriver driver) {
        super(driver);
    }

    public void isOpened() {
        checkTrue(waitUntilPageLoaded(), "Verify page is not loaded");
        pageHeader.waitUntil(visible, 12000);
    }

    @Step
    public void cancelPasswordChange() {
        Logger.info("Cancel password change ");
        btnCancel.scrollIntoView(false).click();
    }
}
