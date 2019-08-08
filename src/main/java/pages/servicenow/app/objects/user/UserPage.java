package pages.servicenow.app.objects.user;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.servicenow.app.objects.ObjectPage;

import static core.check.Check.checkTrue;

/**
 * Created by horobets on Aug 02, 2019
 */
public class UserPage extends ObjectPage {
    private By userScrollBy = By.id("sys_user.form_scroll");

    public UserPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        super.isOpened();
        switchToPageContentFrame();
        checkTrue(isElementPresent(userScrollBy), String.format("%s was not found", userScrollBy.toString()));
        switchToDefaultContent();
    }
}
