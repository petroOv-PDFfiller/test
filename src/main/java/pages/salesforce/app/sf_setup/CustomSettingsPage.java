package pages.salesforce.app.sf_setup;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.setup.ASAppCustomSettingsPage;

import static core.check.Check.checkTrue;

public class CustomSettingsPage extends SalesAppBasePage {

    private By content = By.xpath("//div[@class='content']");

    public CustomSettingsPage(WebDriver driver) {
        super(driver);
        iframe = By.xpath("//iframe[contains(@title, 'Custom Settings')]");
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "Custom settings page is not loaded");
        checkTrue(isElementPresentAndDisplayed(iframe, 10), "Iframe is not displayed");
        checkTrue(switchToFrame(iframe), "Cannot switch to custom settings frame");
        checkTrue(isElementPresent(content, 30), "content is not present");
    }

    @Step
    public ASAppCustomSettingsPage manageASAppSettings() {
        By btnManage = By.xpath("//a[text() ='airSlate Settings']/ancestor::tr//a[text()='Manage']");

        checkTrue(isElementPresent(btnManage, 10), "Manage button is not displayed");
        click(btnManage);
        checkTrue(isElementDisappeared(btnManage, 10), "Mange button is not disappeared");
        return initExpectedPage(ASAppCustomSettingsPage.class);
    }
}
