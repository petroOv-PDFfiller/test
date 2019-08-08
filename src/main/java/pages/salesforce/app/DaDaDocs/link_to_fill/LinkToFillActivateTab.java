package pages.salesforce.app.DaDaDocs.link_to_fill;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.DaDaDocsFullApp;

import static core.check.Check.checkTrue;

public class LinkToFillActivateTab extends LinkToFillBasePage {

    private By btnDone = By.xpath("//span[text()='Done']");

    public LinkToFillActivateTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        skipLoader();
        checkTrue(isElementDisplayed(btnDone, 60), "l2f activate tab was not loaded");
    }

    @Step
    public void activateLinkToFill(boolean isActive) {
        String actionText;
        if (isActive) {
            actionText = "Active";
        } else {
            actionText = "Inactive";
        }
        By switchButton = By.xpath("//*[contains(@class,'l2f-btn-switch')][text()='" + actionText + "']");
        if (!isElementContainsStringInClass(switchButton, "active")) {
            checkTrue(isElementDisplayed(switchButton, 5), actionText + " switch button is not presented");
            By okButton = By.xpath("//button[text()='Ok']");
            click(switchButton);
            checkTrue(isElementDisplayed(okButton, 5), "Dialogue window wasn't appear");
            click(okButton);
            isOpened();
        }
    }

    @Step
    public String getLinkToFillUrl() {
        By l2fLocator = By.xpath("//p[contains(text(),'This URL')]/following-sibling::*[contains(@class,'form-group')]//input");
        checkTrue(isElementPresent(l2fLocator, 5), "Link to fill url is absent");

        return getAttribute(l2fLocator, "value");
    }

    @Step
    public DaDaDocsFullApp clickDoneButton() {
        checkTrue(isElementDisplayed(btnDone, 5), "Done button is not presented");
        click(btnDone);
        skipLoader();
        DaDaDocsFullApp daDaDocsFullApp = new DaDaDocsFullApp(driver);
        daDaDocsFullApp.isOpened();

        return daDaDocsFullApp;
    }
}
