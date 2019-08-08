package pages.salesforce.app.sf_objects.cases;

import org.openqa.selenium.WebDriver;
import pages.salesforce.app.sf_objects.SalesforceObjectPage;
import pages.salesforce.enums.SalesTab;

public class CasesPage extends SalesforceObjectPage {

    public CasesPage(WebDriver driver) {
        super(driver);
        pageName = SalesTab.CASES.getNameNewLayout();
    }
}
