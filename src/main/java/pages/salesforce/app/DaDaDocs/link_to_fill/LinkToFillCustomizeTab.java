package pages.salesforce.app.DaDaDocs.link_to_fill;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static core.check.Check.checkTrue;

public class LinkToFillCustomizeTab extends LinkToFillBasePage {

    private By linkToFillStatus = By.xpath("//*[@class='modal-alert__message'][contains(text(), 'already has a LinkToFill')]");
    private By btnCreateNewLinkToFill = By.xpath("//button[text()='Create a new LinkToFill']");
    private By fillOnlineButton = By.xpath("//span[text()='Fill Online']");

    public LinkToFillCustomizeTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "SendToSignPage isn`t loaded");
        checkTrue(isElementPresentAndDisplayed(iframe, 20), "Send to sign frame is not displayed");
        checkTrue(switchToFrame(iframe, 5), "Cannot switch to send to sign frame");
        checkTrue(isElementNotDisplayed(orangeLoader, 40), "Vframe isn`t disappeared");
        checkTrue(isElementPresentAndDisplayed(fillerFrame, 10), "Filler frame is not present");
        checkTrue(switchToFrame(fillerFrame, 5), "cannot switch to filler frame");
        checkTrue(isElementDisplayed(fillOnlineButton, 60), "l2f customize tab was not loaded");
        skipLoader();
    }

    @Step
    public void skipStatusMessage() {
        if (isElementDisplayed(linkToFillStatus)) {
            checkTrue(isElementDisplayed(btnCreateNewLinkToFill), "Create l2f button is absent");
            click(btnCreateNewLinkToFill);
            checkTrue(isElementNotDisplayed(linkToFillStatus, 5), "Link to fill status was not disappeared");
        }
    }
}
