package pages.servicenow.app.objects.incident;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.servicenow.app.objects.ObjectPage;

import static core.check.Check.checkTrue;

/**
 * Created by horobets on Aug 05, 2019
 */
public class IncidentPage extends ObjectPage {
    private By incidentScrollBy = By.id("incident.form_scroll");

    public IncidentPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        super.isOpened();
        switchToPageContentFrame();
        checkTrue(isElementPresent(incidentScrollBy), String.format("%s was not found", incidentScrollBy.toString()));
        switchToDefaultContent();
    }
}
