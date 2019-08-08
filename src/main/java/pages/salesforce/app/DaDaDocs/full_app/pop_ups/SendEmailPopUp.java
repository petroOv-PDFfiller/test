package pages.salesforce.app.DaDaDocs.full_app.pop_ups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.salesforce.app.SalesAppBasePage;

import java.util.List;

import static core.check.Check.checkTrue;

public class SendEmailPopUp extends SalesAppBasePage {

    private By sendEmailButton = By.xpath("//button[text()='Send']");

    public SendEmailPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(waitUntilPageLoaded(), "SendEmailPopUp isn`t loaded");
        skipLoader();
        checkTrue(isElementPresent(sendEmailButton, 60), "Send email pop-up wasn't appeared");
    }

    @Step
    public void removeAllRecipients() {
        String removeRecipientButtonXpath = "//button[contains(@class,'recipients__button-delete')]";
        By removeRecipientButton = By.xpath(removeRecipientButtonXpath);
        List<WebElement> removeRecipientButtons = driver.findElements(removeRecipientButton);

        for (int i = 1; i <= removeRecipientButtons.size(); i++) {
            click(By.xpath(removeRecipientButtonXpath + "[" + i + "]"));
        }

        checkTrue(isElementNotDisplayed(removeRecipientButton, 2), "Recipients still presented on the page");

    }

    @Step
    public void addRecipient(String email, String name) {
        By emailField = By.xpath("//input[@type='email']");
        By nameField = By.xpath("//input[@placeholder='Name']");
        By addButton = By.xpath("//button[contains(text(), 'Add')]");
        checkTrue(isElementDisplayed(emailField, 5), "Email field is not displayed");
        type(emailField, email);
        type(nameField, name);
        click(addButton);
        checkTrue(isContactDisplayed(email, name), "PDFfiller recipient " + email + " is not presented");
    }

    public boolean isContactDisplayed(String email, String name) {
        By contactWithNameAndEmail = By.xpath("//li[@class='recipients__list-item'][descendant::*[contains(@class, 'email')][text()='" + email + "']]" +
                "[descendant::*[contains(@class, 'name')][text()='" + name + "']]");
        return isElementDisplayed(contactWithNameAndEmail, 5);
    }

    @Step
    public void sendEmail() {
        checkTrue(isElementDisplayed(sendEmailButton), "Share via email button is not displayed");
        hoverOverAndClick(sendEmailButton);
        skipLoader();
        By successAlert = By.xpath("//p[text()='Document sent to recipients']");

        checkTrue(isElementDisplayed(successAlert, 5), "Success alert wasn't appear");
    }
}
