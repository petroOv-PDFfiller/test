package pages.servicenow.app.objects.user;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.servicenow.app.objects.ObjectsListPage;
import pages.servicenow.app.objects.ServiceNowObject;

import static core.check.Check.checkTrue;

/**
 * Created by horobets on Aug 02, 2019
 */
public class UsersListPage extends ObjectsListPage {

    private By navUsersListBy = By.id("list_nav_sys_user");
    private By toggleImageBy = By.id("sys_user_filter_toggle_image");

    public UsersListPage(WebDriver driver) {
        super(driver);
        objectType = ServiceNowObject.USER;
    }

    @Override
    public void isOpened() {
        super.isOpened();
        switchToPageContentFrame();
        checkTrue(isElementDisplayed(navUsersListBy), String.format("%s is not presented", navUsersListBy.toString()));
        checkTrue(isElementDisplayed(toggleImageBy), String.format("%s is not presented", toggleImageBy.toString()));
        switchToDefaultContent();
    }

}
