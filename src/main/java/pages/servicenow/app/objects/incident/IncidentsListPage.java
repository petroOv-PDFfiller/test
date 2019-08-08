package pages.servicenow.app.objects.incident;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.servicenow.app.objects.ObjectsListPage;
import pages.servicenow.app.objects.ServiceNowObject;

import static core.check.Check.checkTrue;

/**
 * Created by horobets on Aug 05, 2019
 */
public class IncidentsListPage extends ObjectsListPage {
    private By navIncidentsListBy = By.id("list_nav_incident");
    private By toggleImageBy = By.id("incident_filter_toggle_image");

    public IncidentsListPage(WebDriver driver) {
        super(driver);
        objectType = ServiceNowObject.INCIDENT;
    }

    @Override
    public void isOpened() {
        super.isOpened();
        switchToPageContentFrame();
        checkTrue(isElementDisplayed(navIncidentsListBy), String.format("%s is not presented", navIncidentsListBy.toString()));
        checkTrue(isElementDisplayed(toggleImageBy), String.format("%s is not presented", toggleImageBy.toString()));
        switchToDefaultContent();
    }
}
