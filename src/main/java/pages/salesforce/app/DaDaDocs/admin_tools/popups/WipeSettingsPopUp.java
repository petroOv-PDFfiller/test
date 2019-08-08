package pages.salesforce.app.DaDaDocs.admin_tools.popups;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.sf_objects.SalesforceBasePopUp;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class WipeSettingsPopUp extends SalesforceBasePopUp {

    private By title = By.xpath("//div[contains(@class, 'popup_opened__')]//span[contains(@class, 'popup__title__')]");
    private By body = By.xpath("//div[contains(@class, 'popup_opened__')]//div[contains(@class, 'popup__text__')]/div");

    public WipeSettingsPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        popUpBody = By.xpath("//div[contains(@class, 'popup_opened__')]");
        checkTrue(isElementPresentAndDisplayed(popUpBody, 10), "Wipe settings pop up is not opened");
        checkEquals(getText(title), "Wipe DaDaDocs settings", "Wrong Wipe settings pop up text");
        checkEquals(getText(body), "This will remove DaDaDocs from all Layouts, and delete all user licenses " +
                        "for DaDaDocs. Are you sure?",
                "Wrong Wipe settings pop up body text");
    }
}
