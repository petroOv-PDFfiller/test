package pages.airslate.addons;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.airslate.flow_creator.ListSlatesPage;
import pages.airslate.pop_ups.PopupBase;

import static core.check.Check.checkTrue;

public class DeclineSignaturePopUp extends PopupBase {

    private By titleHeader = By.xpath("//h3[.='Decline a Signature Request']");

    public DeclineSignaturePopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(waitUntilPageLoaded(), this.getClass().getSimpleName() + " is not loaded");
        checkTrue(isElementPresent(titleHeader, 60), this.getClass().getSimpleName() + " is not opened");
    }

    @Step
    public DeclineSignaturePopUp enterDeclineReason(String reason) {
        By inputDecline = By.id("DEFINE_FORM_ID");

        checkTrue(isElementDisplayed(inputDecline), "decline input is not displayed");
        type(inputDecline, reason);
        return this;
    }

    @Step
    public ListSlatesPage declineSignature() {
        By btnDecline = By.xpath("//button[.='Decline']");
        return goToPage(btnDecline, ListSlatesPage.class);
    }
}
