package pages.salesforce.app.sf_objects.campaigns;

import org.openqa.selenium.WebDriver;
import pages.salesforce.app.sf_objects.SalesforceObjectPage;
import pages.salesforce.enums.SalesTab;

public class CampaignsPage extends SalesforceObjectPage {

    public CampaignsPage(WebDriver driver) {
        super(driver);
        pageName = SalesTab.CAMPAIGNS.getNameNewLayout();
    }

}
