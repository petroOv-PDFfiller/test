package pages.salesforce.app.DaDaDocs.full_app.V3.tabs;

import org.openqa.selenium.WebDriver;
import pages.salesforce.app.DaDaDocs.full_app.V3.DaDaDocsFullAppV3;

import static core.check.Check.checkEquals;
import static pages.salesforce.enums.V3.DaDaDocsV3Tabs.AUDIT_TRAIL_TAB;


public class AuditTrailTab extends DaDaDocsFullAppV3 {

    public AuditTrailTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        skipLoader();
        checkEquals(getActiveTabName(), AUDIT_TRAIL_TAB.getName(), "Audit trail tab page is not opened");
    }
}
