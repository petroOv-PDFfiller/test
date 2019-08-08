package pages.salesforce.app.airSlate_app.custom_button;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;
import static data.salesforce.SalesforceTestData.ASAppPageTexts.OUCH;
import static data.salesforce.SalesforceTestData.ASAppPageTexts.WE_VE_NOTIFIED_YOUR_ADMIN_ABOUT_THE_PROBLEM;

public class RunningFlowErrorPage extends SalesAppBasePage {

    private By title = By.xpath("//*[contains(@class, 'title__') and text()='" + OUCH + "']");

    public RunningFlowErrorPage(WebDriver driver) {
        super(driver);
        loader = By.xpath("//*[contains(@class, 'loaderWrapper__')]");
    }

    @Override
    public void isOpened() {
        By subtitle = By.xpath("//*[contains(@class, 'subtitle__')]");

        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "running flow error page is not loaded");
        if (isElementPresent(iframe)) {
            checkIsElementDisplayed(iframe, 10, "running flow error page  frame");
            checkTrue(switchToFrame(iframe, 15), "Cannot switch to running flow error page ");
        }
        checkTrue(isElementNotDisplayed(loader, 10), "Loader is not hidden");
        checkIsElementDisplayed(title, 20, "running flow error page  title");
        checkIsElementDisplayed(subtitle, 5, "running flow error page subtitle");
        checkEquals(getAttribute(subtitle, "textContent"), WE_VE_NOTIFIED_YOUR_ADMIN_ABOUT_THE_PROBLEM, "Incorrect subtitle text");
    }
}
