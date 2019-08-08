package pages.salesforce.app.airSlate_app.setup;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;

import static core.check.Check.checkTrue;

public class ASAppCustomSettingsEditPage extends SalesAppBasePage {

    private By title = By.xpath("//*[@class = 'pageType noSecondHeader' and text() = 'airSlate Settings Edit']");

    public ASAppCustomSettingsEditPage(WebDriver driver) {
        super(driver);
        iframe = By.xpath("//iframe[contains(@title,'airSlate Settings')]");
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "ASApp custom setting edit page is not loaded");
        checkTrue(isElementPresentAndDisplayed(iframe, 10), "Iframe is not displayed");
        checkTrue(switchToFrame(iframe), "Cannot switch to ASApp custom setting edit frame");
        checkTrue(isElementPresent(title, 30), "Title is not displayed");
    }

    @Step
    public void setApiURL(String url) {
        By inputUrl = By.xpath("//input[contains(@id, 'API_URL__c')]");
        By btnSave = By.xpath("//input[@value='Save']");

        checkTrue(isElementDisplayed(inputUrl, 5), "Url input is not displayed");
        type(inputUrl, url);
        checkIsElementDisplayed(btnSave, 5, "Save button");
        click(btnSave);
        checkTrue(isElementDisappeared(title, 10), "Settings is not saved");
    }
}
