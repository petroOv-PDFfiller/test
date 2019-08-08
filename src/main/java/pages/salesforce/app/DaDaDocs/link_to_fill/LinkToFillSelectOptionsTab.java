package pages.salesforce.app.DaDaDocs.link_to_fill;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static core.check.Check.checkTrue;

public class LinkToFillSelectOptionsTab extends LinkToFillBasePage {

    private By passwordField = By.xpath("//input[@type='password']");

    public LinkToFillSelectOptionsTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        skipLoader();
        checkTrue(isElementDisplayed(passwordField, 60), "l2f customize tab was not loaded");
    }
}
