package tests.servicenow.servicenow_signnow_app;

import org.testng.annotations.BeforeTest;
import pages.servicenow.ServiceNowBasePage;
import tests.servicenow.ServiceNowBaseTest;

/**
 * Created by horobets on Aug 05, 2019
 */
public abstract class ServiceNowSignNowBaseTest extends ServiceNowBaseTest {

    protected ServiceNowBasePage serviceNowBasePage;

    @BeforeTest
    public void setUp() {
        serviceNowBasePage = signInToServiceNow();
    }

}
