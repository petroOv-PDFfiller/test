package pages.salesforce.app.DaDaDocs;

import io.qameta.allure.Step;
import org.openqa.selenium.*;
import pages.salesforce.app.SalesAppBasePage;
import utils.Logger;
import utils.TimeMan;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static core.check.Check.checkTrue;
import static org.awaitility.Awaitility.await;

public class CustomButtonS2SPage extends SalesAppBasePage {

    public PreviewTab previewTab;
    private By sendButton = By.xpath("//button[text()='Send']");

    public CustomButtonS2SPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        By loader = By.xpath("//*[@class='g-loader__label'][text()='Generating PDF document from template...']");
        checkTrue(waitUntilPageLoaded(), "OrderFormPage isn`t loaded");
        waitForSalesforceLoading();

        if (!isElementDisplayed(sendButton, 2)) {
            switchToFrame(iframe);
            if (isElementPresentAndDisplayed(loader, 3)) {
                checkTrue(isElementNotDisplayed(loader, 60), "Order form page was not loaded");
            }
            checkTrue(isElementDisplayed(sendButton, 30), "Order form page was not loaded");
        }
    }

    public void isOpenedBeforeRedirect() {
        By loader = By.xpath("//*[@class='g-loader__label'][text()='Generating PDF document from template...']");
        checkTrue(waitUntilPageLoaded(), "OrderFormPage isn`t loaded");
        waitForSalesforceLoading();

        switchToFrame(iframe, 25);
        if (isElementPresentAndDisplayed(loader, 3)) {
            try {
                checkTrue(isElementNotDisplayed(loader, 60), "Order form page was not loaded");
            } catch (WebDriverException e) {
                Logger.error("iframe disappeared");
            }
        }
    }

    public PreviewTab switchToPreview() {
        Logger.info("Switching frame to preview tab");

        previewTab = new PreviewTab(driver);
        previewTab.isOpened();

        return previewTab;
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
        By nameField = By.xpath("//input[@placeholder='Recipient Name']");
        checkTrue(isElementDisplayed(emailField, 5), "Email field is not displayed");
        TimeMan.sleep(5);
        setAttribute(driver.findElement(emailField), "value", "email");
        type(nameField, name);
        type(emailField, email);
    }

    public void setAttribute(WebElement element, String attName, String attValue) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);",
                element, attName, attValue);
    }

    public boolean isContactDisplayed(String email, String name) {
        By contactWithNameAndEmail = By.xpath("//li[@class='recipients__list-item'][descendant::*[contains(@class, 'email')][text()='" + email + "']]" +
                "[descendant::*[contains(@class, 'name')][text()='" + name + "']]");
        return isElementPresentAndDisplayed(contactWithNameAndEmail, 5);
    }

    public void clickSendButton() {
        By loader = By.xpath("//*[@class='g-loader__label'][text()='loading...']");
        checkTrue(isElementDisplayed(sendButton, 5), "Send button is not presented");
        click(sendButton);
        if (isElementDisplayed(loader, 2)) {
            checkTrue(isElementNotDisplayed(loader, 60), "Form sent was not performed correctly");
            TimeMan.sleep(3);
        }
    }

    public String getDocumentName() {
        By documentNameSpan = By.xpath("//*[@class='recipients-container']/span");
        checkTrue(isElementDisplayed(documentNameSpan, 5), "Document name is not presented on the page");
        await().ignoreExceptions().atMost(1, TimeUnit.MINUTES).until(() -> !getText(documentNameSpan).equals("\n                "));
        return getText(documentNameSpan);
    }
}
