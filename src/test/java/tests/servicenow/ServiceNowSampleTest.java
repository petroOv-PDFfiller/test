package tests.servicenow;

import io.qameta.allure.Story;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.servicenow.ServiceNowBasePage;
import pages.servicenow.app.ServiceNowTab;
import pages.servicenow.app.objects.company.CompaniesListPage;
import pages.servicenow.app.objects.company.CompanyPage;
import pages.servicenow.app.objects.incident.IncidentPage;
import pages.servicenow.app.objects.incident.IncidentsListPage;
import pages.servicenow.app.objects.user.UserPage;
import pages.servicenow.app.objects.user.UsersListPage;

/**
 * Created by horobets on Aug 05, 2019
 */
public class ServiceNowSampleTest extends ServiceNowBaseTest {

    protected ServiceNowBasePage serviceNowBasePage;

    @BeforeTest
    public void setUp() {
        serviceNowBasePage = signInToServiceNow();
    }

    @Story("ServiceNow Sample Test")
    @Test
    public void serviceNowObjectsSampleTest() {

        SoftAssert softAssert = new SoftAssert();
        CompaniesListPage vendorsPage = serviceNowBasePage.openTab(ServiceNowTab.COMPANIES);
        CompanyPage companyPage = vendorsPage.openObject(3);
        softAssert.assertNotNull(companyPage, "companyPage is not opened");

        UsersListPage usersPage = serviceNowBasePage.openTab(ServiceNowTab.USERS);
        UserPage userPage = usersPage.openObject(3);
        softAssert.assertNotNull(userPage, "userPage is not opened");

        IncidentsListPage incidentsListPage = serviceNowBasePage.openTab(ServiceNowTab.INCIDENTS);
        IncidentPage incidentPage = incidentsListPage.openObject(1);
        softAssert.assertNotNull(incidentPage, "incidentPage is not opened");

        softAssert.assertAll();
    }
}
