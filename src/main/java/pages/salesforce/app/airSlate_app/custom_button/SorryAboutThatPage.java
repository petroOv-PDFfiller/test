package pages.salesforce.app.airSlate_app.custom_button;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;

import static com.codeborne.selenide.Selenide.$;
import static core.check.Check.checkTrue;

public class SorryAboutThatPage extends SalesAppBasePage {

    private By title = By.xpath("//*[contains(@class, 'title__') and text()='Sorry about that']");

    public SorryAboutThatPage(WebDriver driver) {
        super(driver);
        loader = By.xpath("//*[contains(@class, 'loaderWrapper__')]");
    }

    @Override
    public void isOpened() {
        if (isElementPresent(iframe)) {
            checkIsElementDisplayed(iframe, 10, "Sorry about that page frame");
            checkTrue(switchToFrame(iframe, 15), "Cannot switch to Sorry about that page frame");
        }
        $(loader).waitUntil(Condition.hidden, 120000);
        checkIsElementDisplayed(title, 20, "Sorry about that page frame content");
    }
}
