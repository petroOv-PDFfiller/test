package pages.servicenow.app.objects.company;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.servicenow.app.objects.ObjectPage;

import static core.check.Check.checkTrue;

/**
 * Created by horobets on Aug 01, 2019
 */
public class CompanyPage extends ObjectPage {
    private By companyScrollBy = By.id("core_company.form_scroll");

    public CompanyPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        super.isOpened();
        switchToPageContentFrame();
        checkTrue(isElementPresent(companyScrollBy), String.format("%s was not found", companyScrollBy.toString()));
        switchToDefaultContent();
    }
}
