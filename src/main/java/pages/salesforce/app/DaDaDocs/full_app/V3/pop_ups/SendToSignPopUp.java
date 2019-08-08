package pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsFullAppV3;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsV3BasePopUp;
import pages.salesforce.app.DaDaDocs.send_to_sign.SendToSignPage;
import utils.Logger;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class SendToSignPopUp extends DaDaDocsV3BasePopUp {

    public SendToSignPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(popUpBody, 4), "S2S template popup is not displayed");
        checkEquals(getText(popUpHeader), "SendToSign options", "Wrong header for S2S template popup");
    }

    @Step
    public SendToSignPage openSend2SignSettings() {
        By btnSettings = By.xpath("//div[contains(@class, 'popup__container__')]//button[.='Settings']");

        Logger.info("open S2S settings");
        checkTrue(isElementPresentAndDisplayed(btnSettings, 2), "Setting s button is not displayed");
        click(btnSettings);
        skipLoader();
        SendToSignPage sendToSignPage = new SendToSignPage(driver);
        sendToSignPage.isOpened();
        return sendToSignPage;
    }

    @Step("Set recipient {0} info email - {1} name - {2}")
    public SendToSignPopUp setRecipient(int recipientNumber, String recipientEmail, String recipientName) {
        By inputRecipientEmail = By.xpath("(//*[contains(@class, 'recipientRow__')])[" + recipientNumber + "]//input[@name='email']");
        By inputRecipientName = By.xpath("(//*[contains(@class, 'recipientRow__')])[" + recipientNumber + "]//input[@name='name']");

        checkTrue(isElementPresentAndDisplayed(inputRecipientEmail, 2), "Email input for "
                + recipientNumber + " recipient is not displayed");
        type(inputRecipientEmail, recipientEmail);
        checkTrue(isElementPresentAndDisplayed(inputRecipientName, 2), "Name input for "
                + recipientNumber + " recipient is not displayed");
        type(inputRecipientName, recipientName);
        isOpened();
        return this;
    }

    @Step
    public SendToSignPopUp addRecipient() {
        By btnAddRecipient = By.xpath("(//*[contains(@class, 'recipientRow__')])[last()]//button");

        Logger.info("Add recipient");
        checkTrue(isElementEnabled(btnAddRecipient, 2), "Add recipient button is not enabled");
        click(btnAddRecipient);
        isOpened();
        return this;
    }

    @Step("Add sign message subject - {0}, text -{1}")
    public SendToSignPopUp addSignMessage(String subject, String text) {
        By btnAddMessage = By.xpath("//button[contains(@class, 'btn__dashed__') and .='Add message']");
        By inputSubject = By.xpath("//input[@name='subject']");
        By inputText = By.xpath("//textarea[@name='message']");

        Logger.info("Add sign message subject - " + subject + ", text -" + text);
        checkTrue(isElementDisplayed(btnAddMessage), "Add message btn is not displayed");
        click(btnAddMessage);
        checkTrue(isElementPresentAndDisplayed(inputSubject, 2), "Message subject field is not displayed");
        click(inputSubject);
        clear(inputSubject);
        type(inputSubject, subject);
        checkTrue(isElementDisplayed(inputText), "Message text field is not displayed");
        click(inputText);
        clear(inputText);
        type(inputText, text);
        isOpened();
        return this;
    }

    @Step
    public <T extends DaDaDocsFullAppV3> T sendToSign(Class<T> expectedPage) {
        By btnSendToSign = By.xpath("//div[contains(@class, 'popup__container__')]//button[.='SendToSign']");

        Logger.info("SendToSign document");
        checkTrue(isElementPresentAndDisplayed(btnSendToSign, 2), "SendToSign button is not displayed");
        click(btnSendToSign);
        checkTrue(isElementNotDisplayed(btnSendToSign, 5), "Send to sign button is still displayed");
        skipLoader();
        DaDaDocsFullAppV3 page = PageFactory.initElements(driver, expectedPage);
        page.isOpened();
        return (T) page;
    }
}
