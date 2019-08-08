package pages.salesforce.app.airSlate_app.setup;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;

import static core.check.Check.checkTrue;

public class ASAppCustomSettingsPage extends SalesAppBasePage {

    private By title = By.xpath("//*[@class = 'pageDescription' and text() = 'airSlate Settings']");

    public ASAppCustomSettingsPage(WebDriver driver) {
        super(driver);
        iframe = By.xpath("//iframe[contains(@title,'Custom Setting airSlate Settings')]");
    }

    @Override
    public void isOpened() {
        switchToDefaultContent();
        checkTrue(waitUntilPageLoaded(), "ASApp custom setting page is not loaded");
        checkTrue(isElementPresentAndDisplayed(iframe, 10), "Iframe is not displayed");
        checkTrue(switchToFrame(iframe), "Cannot switch to ASApp custom setting frame");
        checkTrue(isElementPresent(title, 30), "Title is not displayed");
    }

    @Step
    public ASAppCustomSettingsEditPage editSettings() {
        By btnEdit = By.id("CS_list:CS_Form:theDetailPageBlock:thePageBlockButtons:edit");

        checkIsElementDisplayed(btnEdit, 5, "Edit button");
        click(btnEdit);
        checkTrue(isElementDisappeared(title, 10), "Cannot edit ASApp settings");
        return initExpectedPage(ASAppCustomSettingsEditPage.class);
    }
}
