package pages.salesforce.app;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import pages.salesforce.SalesforceBasePage;
import utils.Logger;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static core.check.Check.checkTrue;

public class AllowRemoteAccessPage extends SalesforceBasePage {
    @Getter
    private SelenideElement pageHeader = $x("//*[@id = 'header' and text()='Allow Access?']");

    @Getter
    private SelenideElement btnAllow = $("#oaapprove");

    public AllowRemoteAccessPage(WebDriver driver) {
        super(driver);
    }

    public void isOpened() {
        checkTrue(waitUntilPageLoaded(), "Remote access page is not loaded");
    }

    @Step
    public void allowAccess() {
        Logger.info("Allow remote access");
        if (pageHeader.exists()) {
            btnAllow.shouldBe(Condition.visible).click();
            pageHeader.waitUntil(Condition.disappear, 5000);
        }
    }
}
