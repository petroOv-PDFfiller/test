package pages.salesforce.app.DaDaDocs.full_app.pop_ups;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.SalesAppBasePage;

import static core.check.Check.checkTrue;

public class MyAccountWidget extends SalesAppBasePage {

    private By linkSwitchToSaleforceClassic = By.xpath("//*[@class='profile-link-label switch-to-aloha uiOutputURL']");
    private By widgetMyAccount = By.xpath("//*[@class='oneUserProfileCard']");

    public MyAccountWidget(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        checkTrue(isElementPresent(widgetMyAccount, 4), "widgetMyAccount is not present");
    }

    @Step("Switch to saleforce classic")
    public void switchToSaleforceClassic() {
        checkTrue(isElementPresent(linkSwitchToSaleforceClassic, 4), "linkSwitchToSaleforceClassic is not present");
        click(linkSwitchToSaleforceClassic);
    }
}
