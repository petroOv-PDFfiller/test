package pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups;

import core.check.SoftCheck;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsV3BasePopUp;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.DocumentsTab;
import utils.Logger;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class ShareForEditingPopUp extends DaDaDocsV3BasePopUp {

    public ShareForEditingPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(popUpBody, 4), "Share for editing popup is not displayed");
        checkEquals(getText(popUpHeader), "Share for editing options", "Wrong header for share for editing popup");
    }

    @Step
    public ShareForEditingPopUp addRecipients(String[] emails) {
        By inputRecipients = By.xpath("//input[@name='email']");

        Logger.info("Add recipient");
        checkTrue(isElementPresentAndDisplayed(inputRecipients, 2), "Email field is not present");
        clear(inputRecipients);
        for (String email : emails) {
            type(email);
            type(Keys.ENTER);
        }

        SoftCheck softCheck = new SoftCheck();
        for (int i = 0; i < emails.length; i++) {
            By email = By.xpath("//*[contains(@class, 'popup__container__')]//*[contains(@class, 'email__') and text()='" + emails[i] + "']");
            softCheck.checkTrue(isElementPresentAndDisplayed(email, 2), emails[i] + " email is not added");
        }
        softCheck.checkAll();
        isOpened();
        return this;
    }

    @Step
    public ShareForEditingPopUp copyLink() {
        By copyLink = By.xpath("//div[contains(@class, 'popup__container__')]//button[.='Copy Link']");

        Logger.info("Copy link for editing");
        checkTrue(isElementPresentAndDisplayed(copyLink, 2), "Copy link button is not displayed");
        click(copyLink);
        return this;
    }

    @Step("Add sign message subject - {0}, text -{1}")
    public ShareForEditingPopUp addShareMessage(String subject, String text) {
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
    public DocumentsTab shareLinkViaEmail() {
        By btnSendByEmail = By.xpath("//div[contains(@class, 'popup__container__')]//button[.='Share link via email']");

        Logger.info("SendToSign document");
        checkTrue(isElementPresentAndDisplayed(btnSendByEmail, 2), "SendToSign button is not displayed");
        click(btnSendByEmail);
        checkTrue(isElementNotDisplayed(btnSendByEmail, 5), "Send to sign button is still displayed");
        skipLoader();
        DocumentsTab documentsTab = new DocumentsTab(driver);
        documentsTab.isOpened();
        return documentsTab;
    }
}
