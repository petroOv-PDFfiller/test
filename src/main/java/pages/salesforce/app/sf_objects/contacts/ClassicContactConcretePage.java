package pages.salesforce.app.sf_objects.contacts;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static core.check.Check.checkTrue;

public class ClassicContactConcretePage extends ContactConcretePage {

    private By detailsTab = By.xpath("//*[@class='mainTitle' and .='Contact Detail']");

    public ClassicContactConcretePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "Classic concrete contact page is not loaded");
        checkTrue(isElementPresent(detailsTab, 60), "Page for a contact is not opened");
    }

    @Step
    public boolean isUseDaDaDocsButtonIsPresent() {
        By btnUseDaDaDocs = By.xpath("//input[@value='Use DaDaDocs']");

        return isElementPresentAndDisplayed(btnUseDaDaDocs, 2);
    }
}
