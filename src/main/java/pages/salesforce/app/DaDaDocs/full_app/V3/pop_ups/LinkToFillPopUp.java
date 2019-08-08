package pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsV3BasePopUp;
import pages.salesforce.app.SalesAppBasePage;
import utils.Logger;

import static core.check.Check.checkEquals;
import static core.check.Check.checkTrue;

public class LinkToFillPopUp extends DaDaDocsV3BasePopUp {

    public LinkToFillPopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresentAndDisplayed(popUpBody, 4), "link2fill template popup is not displayed");
        checkEquals(getText(popUpHeader), "LinkToFill Options", "Wrong header for link2fill template popup");
    }

    @Step
    public <T extends SalesAppBasePage> T closePopUp(Class<T> expectedPage) {
        Logger.info("Close popUp");
        checkIsElementDisplayed(btnClosePopUp, 2, "Close button in " + getClass().getSimpleName());
        clickJS(btnClosePopUp);
        return initExpectedPage(expectedPage);
    }

    @Step
    public LinkToFillPopUp copyLink() {
        By copyLink = By.xpath("//div[contains(@class, 'popup__container__')]//button[.='Copy Link']");

        Logger.info("Copy l2f link");
        checkTrue(isElementPresentAndDisplayed(copyLink, 2), "Copy link button is not displayed");
        hoverOverAndClick(copyLink);
        isOpened();
        return this;
    }
}
