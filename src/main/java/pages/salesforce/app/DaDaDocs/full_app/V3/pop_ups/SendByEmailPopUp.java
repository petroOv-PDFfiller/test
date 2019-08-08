package pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsV3BasePopUp;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.DocumentsTab;
import utils.Logger;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class SendByEmailPopUp extends DaDaDocsV3BasePopUp {

    public SendByEmailPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(popUpBody, 4), "Send by email template popup is not displayed");
        checkEquals(getText(popUpHeader), "Send by email options", "Wrong header for send by email template popup");
    }

    @Step("Set recipient {0} info email - {1} name - {2}")
    public SendByEmailPopUp setRecipient(int recipientNumber, String recipientEmail, String recipientName) {
        By inputRecipientEmail = By.xpath("(//*[contains(@class, 'recipientRow__')])[" + recipientNumber + "]//input[@name='email']");
        By inputRecipientName = By.xpath("(//*[contains(@class, 'recipientRow__')])[" + recipientNumber + "]//input[@name='name']");

        checkTrue(isElementPresentAndDisplayed(inputRecipientEmail, 2), "Email input for "
                + recipientNumber + " recipient is not displayed");
        type(inputRecipientEmail, recipientEmail);
        checkTrue(isElementPresentAndDisplayed(inputRecipientName, 2), "Name input for "
                + recipientNumber + " recipient is not displayed");
        type(inputRecipientName, recipientName);
        return this;
    }

    @Step
    public SendByEmailPopUp addRecipient() {
        By btnAddRecipient = By.xpath("(//*[contains(@class, 'recipientRow__')])[last()]//button");

        Logger.info("Add recipient");
        checkTrue(isElementEnabled(btnAddRecipient, 2), "Add recipient button is not enabled");
        click(btnAddRecipient);
        isOpened();
        return this;
    }

    @Step("Add sign message subject - {0}, text -{1}")
    public SendByEmailPopUp addSignMessage(String subject, String text) {
        By btnAddMessage = By.xpath("//button[contains(@class, 'btn__dashed__') and .='Add message']");
        By inputSubject = By.xpath("//input[@name='subject']");
        By inputText = By.xpath("//textarea[@name='message']");

        Logger.info("Add sign message subject - " + subject + ", text -" + text);
        checkTrue(isElementDisplayed(btnAddMessage), "Add message btn is not displayed");
        click(btnAddMessage);
        checkTrue(isElementPresentAndDisplayed(inputSubject, 2), "Message subject field is not displayed");
        clear(inputSubject);
        type(inputSubject, subject);
        checkTrue(isElementDisplayed(inputText), "Message text field is not displayed");
        clear(inputText);
        type(inputText, text);
        return this;
    }

    @Step
    public DocumentsTab sendByEmail() {
        By btnSendByEmail = By.xpath("//div[contains(@class, 'popup__container__')]//button[.='Send by email']");

        Logger.info("Share by email document");
        checkTrue(isElementPresentAndDisplayed(btnSendByEmail, 2), "SendToSign button is not displayed");
        click(btnSendByEmail);
        checkTrue(isElementNotDisplayed(btnSendByEmail, 5), "Send to sign button is still displayed");
        skipLoader();
        DocumentsTab documentsTab = new DocumentsTab(driver);
        documentsTab.isOpened();
        return documentsTab;
    }
}
