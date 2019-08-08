package pages.salesforce.app.DaDaDocs.full_app.pop_ups;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;

import static core.check.Check.checkTrue;

public class LinkToFillTemplatePopUp extends SalesAppBasePage {

    private By linkToFillPopUp = By.cssSelector("div[data-popup-name='popup__link-to-fill-data-collection']");
    private By btnGenerate = By.cssSelector("button.generate-link-to-fill");
    private By btnCopyLink = By.cssSelector("button.link-to-fill-data-collection__copy-btn");

    public LinkToFillTemplatePopUp(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(waitUntilPageLoaded(), "LinkToFillTemplatePopUp isn`t loaded");
        skipLoader();
        checkTrue(isElementPresentAndDisplayed(linkToFillPopUp, 10), "L2F template pop-up wasn't appeared");
    }

    public LinkToFillTemplatePopUp generateLinkToFill() {
        click(btnGenerate);
        isOpened();
        return this;
    }

    public LinkToFillTemplatePopUp copyLinkToFill() {
        click(btnCopyLink);
        isOpened();
        return this;
    }
}
