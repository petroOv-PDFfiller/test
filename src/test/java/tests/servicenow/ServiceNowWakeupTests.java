package tests.servicenow;

import io.qameta.allure.Story;
import org.springframework.util.Assert;
import org.testng.annotations.Test;
import pages.servicenow.app.DashboardPage;

/**
 * Created by horobets on Aug 06, 2019
 */
public class ServiceNowWakeupTests extends ServiceNowBaseTest {

    @Story("ServiceNow Instance Wakeup")
    @Test
    public void serviceNowWakeupTest() {
        DashboardPage dashboardPage = signInToServiceNow();
        Assert.notNull(dashboardPage, "Dashboard Page instance is null");
    }
}
