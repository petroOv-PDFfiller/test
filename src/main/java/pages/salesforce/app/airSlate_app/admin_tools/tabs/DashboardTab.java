package pages.salesforce.app.airSlate_app.admin_tools.tabs;

import org.openqa.selenium.WebDriver;
import pages.salesforce.app.airSlate_app.admin_tools.ASAppAdminToolsPage;

import static core.check.Check.checkEquals;
import static pages.salesforce.enums.admin_tools.ASAppAdminToolTabs.DASHBOARD;

public class DashboardTab extends ASAppAdminToolsPage {
    public DashboardTab(WebDriver driver) {
        super(driver);
    }

    @Override
    public void isOpened() {
        skipAdminToolLoader(20);
        checkEquals(getActiveTabName(), DASHBOARD.getName(), "Dashboard tab is not opened");
    }
}
