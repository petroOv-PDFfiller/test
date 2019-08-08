package pages.salesforce.app.DaDaDocs.full_app.pop_ups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;
import utils.Logger;
import utils.SystemMan;

import java.util.List;

import static core.check.Check.checkTrue;

public class SharePopUp extends SalesforceBasePopUp {

    private By shareViaEmailButton = By.xpath("//button[contains(.,'Share link via email')]");

    public SharePopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        skipLoader();
        checkTrue(isElementPresentAndDisplayed(shareViaEmailButton, 60), "Share pop-up wasn't appeared");
    }

    @Step
    public void removeAllRecipients() {
        String btnRemoveRecipientXpath = "//button[contains(@class,'button-deconste')]";
        By btnRemoveRecipient = By.xpath(btnRemoveRecipientXpath);

        if (isElementPresentAndDisplayed(btnRemoveRecipient, 7)) {
            List<WebElement> removeRecipientButtons = driver.findElements(btnRemoveRecipient);
            for (int i = 1; i <= removeRecipientButtons.size(); i++) {
                hoverOverAndClick(By.xpath(btnRemoveRecipientXpath + "[" + i + "]"));
            }
        }

        checkTrue(isElementNotDisplayed(btnRemoveRecipient, 2), "Recipients still presented on the page");

    }

    @Step
    public void addRecipient(String email, String name) {
        By emailField = By.xpath("//input[@type='email']");
        By nameField = By.xpath("//input[@placeholder='Recipient Name']");
        By addButton = By.xpath("//button[contains(text(), 'Add')]");
        checkTrue(isElementDisplayed(emailField, 5), "Email field is not displayed");
        type(emailField, email);
        type(nameField, name);
        hoverOverAndClick(addButton);
        checkTrue(isContactDisplayed(email, name), "PDFfiller recipient " + email + " is not presented");
    }

    public boolean isContactDisplayed(String email, String name) {
        By contactWithNameAndEmail = By.xpath("//li[@class='recipients__list-item'][descendant::*[contains(@class, 'email')][text()='" + email + "']]" +
                "[descendant::*[contains(@class, 'name')][text()='" + name + "']]");
        return isElementDisplayed(contactWithNameAndEmail, 5);
    }

    @Step
    public DaDaDocsFullApp shareViaEmail() {
        checkTrue(isElementDisplayed(shareViaEmailButton), "Share via email button is not displayed");
        hoverOverAndClick(shareViaEmailButton);
        skipLoader();
        By successAlert = By.xpath("//p[text()='The link has been sent out']");

        checkTrue(isElementDisplayed(successAlert, 5), "Success alert wasn't appear");
        DaDaDocsFullApp daDaDocsFullApp = new DaDaDocsFullApp(driver);
        daDaDocsFullApp.isOpened();

        return daDaDocsFullApp;
    }

    @Step
    public String copyLink() {
        By copyLinkButton = By.xpath("//button[contains(.,'Copy link')]");
        checkTrue(isElementPresentAndDisplayed(copyLinkButton, 5), "Copy link button is not displayed");
        skipPopUpLoader();
        hoverOverAndClick(copyLinkButton);

        String copiedLink = SystemMan.getClipboardValue();
        checkTrue(copiedLink.length() > 0, "Nothing was copied as link");
        Logger.info("Shared link " + copiedLink);

        return copiedLink;
    }

    public void skipPopUpLoader() {
        By popUpLoader = By.xpath("//div[contains(@class, 'g-wrap-loading--popup')]");
        checkTrue(isElementNotDisplayed(popUpLoader, 15), "Tab loader is displayed");
    }
}
