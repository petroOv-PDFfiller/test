package pages.salesforce.app.DaDaDocs.send_to_sign;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsFullAppV3;
import pages.salesforce.app.SalesAppBasePage;
import utils.StringMan;

import java.util.List;

import static core.check.Check.checkTrue;

public class SendToSignPage extends SalesAppBasePage {

    private By sendToSignButton = By.cssSelector("button[ng-click='s2s.save()']");
    private By envelopeField = By.xpath("//input[@placeholder='Name of group']");
    private By addAnotherRecipient = By.xpath("//button[contains(.,'Add Another Recipient')]");

    public SendToSignPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        skipGlobalLoader();
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "SendToSignPage isn`t loaded");
        checkTrue(isElementPresentAndDisplayed(iframe, 20), "Send to sign frame is not displayed");
        checkTrue(switchToFrame(iframe, 5), "Cannot switch to send to sign frame");
        checkTrue(isElementNotDisplayed(orangeLoader, 40), "Vframe loader isn`t disappeared");
        checkTrue(isElementPresentAndDisplayed(fillerFrame, 10), "Filler frame is not present");
        checkTrue(switchToFrame(fillerFrame, 5), "cannot switch to filler frame");
        checkTrue(isElementPresent(sendToSignButton, 60), "DaDaDocs s2s page was not loaded");
    }

    @Step
    public void chooseSendType(String sendType) {
        By sendTypeOption = By.xpath("//*[contains(@class,'sts__to-sign__choice')][@ng-class][descendant::h4[contains(.,'" + sendType + "')]]");
        checkTrue(isElementPresentAndDisplayed(sendTypeOption, 5), sendType + " selector is not displayed");
        if (!isElementContainsStringInClass(sendTypeOption, "active")) {
            click(sendTypeOption);
            skipLoader();
            checkTrue(isElementContainsStringInClass(sendTypeOption, "active"), sendType + " send type was not selected");
        }
    }

    @Step
    public void fillRecipients(List<List<String>> recipients) {
        int recipientsNumber = recipients.size();
        for (int i = 0; i < recipientsNumber; i++) {
            By emailField = By.xpath("(//input[@placeholder='Recipient Email'])[" + (i + 1) + "]");
            By nameField = By.xpath("(//input[@placeholder='Recipient Name'])[" + (i + 1) + "]");

            checkTrue(isElementDisplayed(emailField), (i + 1) + " email field is not presented");
            checkTrue(isElementDisplayed(nameField), (i + 1) + " name field is not presented");

            type(emailField, recipients.get(i).get(0));
            type(nameField, recipients.get(i).get(1));
        }
    }

    @Step
    public SendToSignPage clickAddAnotherRecipient() {
        checkTrue(isElementDisplayed(addAnotherRecipient, 5), "AddRecipient button is not presented");

        click(addAnotherRecipient);
        isOpened();

        return this;
    }

    @Step
    public DaDaDocsFullApp sendToSign() {
        checkTrue(isElementDisplayed(sendToSignButton, 5), "s2s button is not presented");
        click(sendToSignButton);
        skipLoader();
        checkTrue(isElementDisappeared(sendToSignButton, 20), "s2s button is still presented");
        DaDaDocsFullApp daDaDocsFullApp = new DaDaDocsFullApp(driver);
        daDaDocsFullApp.isOpened();

        return daDaDocsFullApp;
    }

    @Step
    public DaDaDocsFullAppV3 sendToSignV3() {
        By nameModal = By.xpath("//div[contains(@class, 'old-modal-header') and text()='Include your name']");
        By btnSaveName = By.xpath("//div[contains(@class, 'old-modal-content')]//button[contains(@class, 'btn--orange') and text()='Save']");
        By inputSenderName = By.id("sender_name");

        checkTrue(isElementDisplayed(sendToSignButton, 5), "s2s button is not presented");
        click(sendToSignButton);
        if (isElementPresentAndDisplayed(nameModal, 2)) {
            checkTrue(isElementPresentAndDisplayed(inputSenderName, 2), "sender name is not present");
            type(inputSenderName, "AutotestSF" + StringMan.getRandomString(5));
            checkTrue(isElementPresentAndDisplayed(btnSaveName, 2), "Save button is not displayed");
            click(btnSaveName);
        }
        skipLoader();
        checkTrue(isElementDisappeared(sendToSignButton, 20), "s2s button is still presented");
        DaDaDocsFullAppV3 daDaDocsFullApp = new DaDaDocsFullAppV3(driver);
        daDaDocsFullApp.isOpened();

        return daDaDocsFullApp;
    }

    @Step
    public void fillEnvelopeName(String name) {
        checkTrue(isElementDisplayed(envelopeField), "Envelope field is not presented");
        type(envelopeField, name);
    }
}
