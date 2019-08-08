package pages.servicenow.app;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.servicenow.ServiceNowBasePage;

/**
 * Created by horobets on Aug 05, 2019
 */
public class DashboardPage extends ServiceNowBasePage {

    private By homepageGridBy = By.id("homepage_grid");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        super.isOpened();
        switchToPageContentFrame();
        isElementDisplayed(homepageGridBy);
        switchToDefaultContent();
    }
}
