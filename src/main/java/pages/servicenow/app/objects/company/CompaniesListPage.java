package pages.servicenow.app.objects.company;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.servicenow.app.objects.ObjectsListPage;
import pages.servicenow.app.objects.ServiceNowObject;

import static core.check.Check.checkTrue;

/**
 * Created by horobets on Aug 01, 2019
 */
public class CompaniesListPage extends ObjectsListPage {
    private By navCompaniesListBy = By.id("list_nav_core_company");
    private By toggleImageBy = By.id("core_company_filter_toggle_image");

    public CompaniesListPage(WebDriver driver) {
        super(driver);
        objectType = ServiceNowObject.COMPANY;
    }

    @Override
    public void isOpened() {
        super.isOpened();
        switchToPageContentFrame();
        checkTrue(isElementDisplayed(navCompaniesListBy), String.format("%s is not presented", navCompaniesListBy.toString()));
        checkTrue(isElementDisplayed(toggleImageBy), String.format("%s is not presented", toggleImageBy.toString()));
        switchToDefaultContent();
    }
}
