package tests.servicenow;

import base_tests.PDFfillerTest;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import pages.servicenow.app.DashboardPage;
import pages.servicenow.app.LoginPage;

/**
 * Created by horobets on Aug 01, 2019
 */
@Listeners({WebTestListener.class})
public abstract class ServiceNowBaseTest extends PDFfillerTest {

    protected String instanceUrl;
    protected String serviceNowUsername;
    protected String serviceNowPassword;

    protected WebDriver driver;

    @Parameters({"servicenow_instanceUrl", "servicenow_username", "servicenow_password"})
    @BeforeTest
    public void setUpCredentials(@Optional("https://dev54901.service-now.com") String servicenow_instanceUrl,
                                 @Optional("admin") String servicenow_username,
                                 @Optional("gowDCLf3FmG6ch1") String servicenow_password) {
        instanceUrl = servicenow_instanceUrl;
        serviceNowUsername = servicenow_username;
        serviceNowPassword = servicenow_password;

        driver = getDriver();
    }

    protected DashboardPage signInToServiceNow() {

        getUrl(this.instanceUrl);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.isOpened();

        loginPage.loginToServiceNow(serviceNowUsername, serviceNowPassword);

        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.isOpened();
        return dashboardPage;
    }
}
