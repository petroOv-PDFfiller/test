package pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups;

import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsV3BasePopUp;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class DuplicateTemplatePopUp extends DaDaDocsV3BasePopUp {

    public DuplicateTemplatePopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(popUpBody, 4), "Duplicate template popup is not displayed");
        checkEquals(getText(popUpHeader), "Duplicate settings", "Wrong header for duplicate template popup");
    }
}
