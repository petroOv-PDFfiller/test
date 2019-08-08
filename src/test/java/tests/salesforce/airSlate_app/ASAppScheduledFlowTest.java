package tests.salesforce.airSlate_app;

import api.salesforce.responses.FindRecordsIDResponse;
import com.airslate.api.models.slates.Slate;
import com.google.common.collect.ImmutableMap;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import listeners.WebTestListener;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.admin_tools.ASAppAdminToolsPage;
import pages.salesforce.app.airSlate_app.admin_tools.entities.ScheduleFlow;
import pages.salesforce.app.airSlate_app.admin_tools.tabs.ScheduledFlowsTab;
import pages.salesforce.app.airSlate_app.admin_tools.wizards.scheduled_flow.BatchSettingsWizardPage;
import pages.salesforce.app.airSlate_app.admin_tools.wizards.scheduled_flow.FrequencyWizardPage;
import pages.salesforce.app.airSlate_app.admin_tools.wizards.scheduled_flow.ScheduleInfoWizardPage;
import tests.salesforce.SalesforceAirSlateBaseTest;
import utils.StringMan;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static api.salesforce.SalesforceRestApi.isPackageHasNamespace;
import static api.salesforce.entities.SalesforceObject.USER_CONFIGURATION;
import static data.salesforce.SalesforceTestData.ASAppCustomButtonSortOrder.ASC;
import static data.salesforce.SalesforceTestData.ASAppCustomButtonSortOrder.DESC;
import static java.util.Collections.singletonList;
import static org.awaitility.Awaitility.await;
import static org.testng.Assert.*;
import static pages.salesforce.enums.admin_tools.ASAppAdminToolTabs.SCHEDULED_FLOWS;
import static utils.StringMan.getRandomString;

@Feature("airSlate app: scheduled flows")
@Listeners(WebTestListener.class)
public class ASAppScheduledFlowTest extends SalesforceAirSlateBaseTest {

    private final static String NAME = "Schedule name";
    private final static String FLOW = "Flow to run";
    private String adminToolURL;
    private ASAppAdminToolsPage adminToolsPage;
    private Slate firstFlow;
    private Slate secondFlow;
    private Date startFlowDate;
    private int numberOfSlates = -1;
    private Date startWeeklyFlowDate;
    private int numberOfSlatesWeekly = -1;
    private String weeklyFlowName;
    private int weeklyFlowHours;
    private int weeklyFlowMinutes;
    private int weeklyFlowMeridiem;
    private String dailyFlowName;
    private String accountWebsite = "asAutoTest" + StringMan.getRandomString(5);
    private String soqlQuery = "Select id From Account Where Website = '" + accountWebsite + "'";

    @DataProvider(name = "sortPairwise")
    public static Object[][] sortPairwise() {
        return new Object[][]{{NAME, ASC, Comparator.comparing(o -> ((ScheduleFlow) o).getName().toLowerCase())},
                {NAME, DESC, (Comparator<ScheduleFlow>) (o1, o2) -> o2.getName().toLowerCase().compareTo(o1.getName().toLowerCase())},
                {FLOW, ASC, Comparator.comparing(o -> ((ScheduleFlow) o).getFlow().toLowerCase())},
                {FLOW, DESC, (Comparator<ScheduleFlow>) (o1, o2) -> o2.getFlow().toLowerCase().compareTo(o1.getFlow().toLowerCase())}};
    }

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        SalesAppBasePage salesAppBasePage = loginWithDefaultCredentials();
        setUserUTCTimeZone(salesforceDefaultLogin);
        configurateSalesforceOrg();
        firstFlow = createFlowWithDocument(fileToUpload);
        secondFlow = createFlowWithDocument(fileToUpload);
        adminToolsPage = getAdminToolsPage(salesAppBasePage);
        adminToolURL = getDriver().getCurrentUrl();
        createAccountRecord(ImmutableMap.of("Website", accountWebsite));
    }

    @BeforeMethod
    public void getAdminToolPage() {
        getUrl(adminToolURL);
        adminToolsPage.isOpened();
    }

    @Story("Should create daily flow")
    @Test
    public void createDailyScheduledFlow() throws IOException {
        String scheduleName = getRandomString(5);

        ScheduledFlowsTab scheduledFlowsPage = adminToolsPage.openTab(SCHEDULED_FLOWS);
        ScheduleInfoWizardPage infoWizardPage = scheduledFlowsPage.createSchedule();
        FrequencyWizardPage frequencyWizardPage = infoWizardPage.setScheduleName(scheduleName)
                .openSelectFlowDropdown()
                .selectFlow(firstFlow.name)
                .navigateToNextTab();
        Calendar calendar = getUTCCalendar();
        calendar.add(Calendar.MINUTE, 2);
        BatchSettingsWizardPage batchSettingsWizardPage = frequencyWizardPage.openSelectFrequencyDropdown()
                .selectFrequency("Daily")
                .setHours(calendar.get(Calendar.HOUR))
                .setMinutes(calendar.get(Calendar.MINUTE))
                .setMeridiem(calendar.get(Calendar.AM_PM))
                .navigateToNextTab();
        numberOfSlates = getSlates(firstFlow.id).size();
        scheduledFlowsPage = batchSettingsWizardPage.enterSoqlQuery(soqlQuery).navigateToNextTab();
        assertTrue(scheduledFlowsPage.isFlowPresent(scheduleName), "Daily flow is not created");
        startFlowDate = calendar.getTime();
        dailyFlowName = scheduleName;
        eraseUserConfigurationToken(salesforceDefaultLogin);
    }

    @Story("Should create weekly flow")
    @Test
    public void createWeeklyScheduledFlow() throws IOException {
        String scheduleName = getRandomString(5);

        ScheduledFlowsTab scheduledFlowsPage = adminToolsPage.openTab(SCHEDULED_FLOWS);
        ScheduleInfoWizardPage infoWizardPage = scheduledFlowsPage.createSchedule();
        FrequencyWizardPage frequencyWizardPage = infoWizardPage
                .setScheduleName(scheduleName)
                .openSelectFlowDropdown()
                .selectFlow(secondFlow.name)
                .navigateToNextTab();
        Calendar calendar = getUTCCalendar();
        calendar.add(Calendar.MINUTE, 2);
        weeklyFlowHours = calendar.get(Calendar.HOUR);
        weeklyFlowMinutes = calendar.get(Calendar.MINUTE);
        int weeklyFlowDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        weeklyFlowMeridiem = calendar.get(Calendar.AM_PM);
        BatchSettingsWizardPage batchSettingsWizardPage = frequencyWizardPage.openSelectFrequencyDropdown()
                .selectFrequency("Weekly")
                .setDay(weeklyFlowDayOfWeek)
                .setHours(weeklyFlowHours)
                .setMinutes(weeklyFlowMinutes)
                .setMeridiem(weeklyFlowMeridiem)
                .navigateToNextTab();
        numberOfSlatesWeekly = getSlates(secondFlow.id).size();
        scheduledFlowsPage = batchSettingsWizardPage.enterSoqlQuery(soqlQuery).navigateToNextTab();
        assertTrue(scheduledFlowsPage.isFlowPresent(scheduleName), "Weekly flow is not created");
        startWeeklyFlowDate = calendar.getTime();
        weeklyFlowName = scheduleName;
    }

    @Story("Should correctly sort by all columns in schedule flows list")
    @Test(dataProvider = "sortPairwise", priority = 1)
    public void sortByScheduleFlows(String field,
                                    String order,
                                    Comparator<ScheduleFlow> flowComparator) {
        ScheduledFlowsTab scheduledFlowsPage = adminToolsPage.openTab(SCHEDULED_FLOWS);

        if (!(field.equals(NAME) && order.equals(ASC))) {
            scheduledFlowsPage.sortBy(field, order);
        }
        List<ScheduleFlow> flows = scheduledFlowsPage.getAllFlows();
        List<ScheduleFlow> sorted = flows.stream()
                .sorted(flowComparator)
                .collect(Collectors.toList());
        assertEquals(sorted, flows, "Incorrect sort by " + field + " by " + order);
    }

    @Story("Should run daily flow")
    @Test(priority = 2)
    public void checkDailyFlow() {
        assertNotNull(startFlowDate, "Daily Schedule time is not set");
        assertNotEquals(numberOfSlates, -1, "Daily Schedule flow is not set");
        refreshPage();
        await().atMost(115, TimeUnit.SECONDS).until(() -> new Date().getTime() > startFlowDate.getTime());
        refreshPage();
        assertTrue(waitForSlates(numberOfSlates + 1, firstFlow.id), "Daily Schedule flow is not working");
        assertNotNull(getUserConfigurationToken(salesforceDefaultLogin), "Access token is not updated");
    }

    @Story("Should run weekly flow")
    @Test(priority = 2)
    public void checkWeeklyFlow() {
        assertNotNull(startWeeklyFlowDate, "Weekly Schedule time is not set");
        assertNotEquals(numberOfSlatesWeekly, -1, "Weekly Schedule flow is not set");
        refreshPage();
        await().atMost(115, TimeUnit.SECONDS).until(() -> new Date().getTime() > startWeeklyFlowDate.getTime());
        refreshPage();
        assertTrue(waitForSlates(numberOfSlatesWeekly + 1, secondFlow.id), "Weekly Schedule flow is not working");
    }

    @Story("Check 'edit flow' wizard")
    @Test(priority = 3)
    public void editWeeklyScheduleFlow() {
        assertNotNull(weeklyFlowName, "Weekly Schedule time is not set");

        ScheduledFlowsTab scheduledFlowsPage = adminToolsPage.openTab(SCHEDULED_FLOWS);
        ScheduleInfoWizardPage infoWizardPage = scheduledFlowsPage.editFlow(weeklyFlowName);
        infoWizardPage.setScheduleName("");
        try {
            infoWizardPage.navigateToNextTab();
        } catch (Exception e) {
            assertTrue(infoWizardPage.isNameErrorDisplayed(), "");
        }
        weeklyFlowName = "new" + getRandomString(5);
        FrequencyWizardPage frequencyWizardPage = infoWizardPage.setScheduleName(weeklyFlowName)
                .navigateToNextTab();
        frequencyWizardPage.openSelectFrequencyDropdown().selectFrequency("Daily");

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(frequencyWizardPage.getHours(), weeklyFlowHours, "Hours value changed");
        softAssert.assertEquals(frequencyWizardPage.getMinutes(), weeklyFlowMinutes, "Minutes value changed");
        softAssert.assertEquals(frequencyWizardPage.getMeridiem(), weeklyFlowMeridiem, "Meridiem value changed");

        scheduledFlowsPage = ((BatchSettingsWizardPage) frequencyWizardPage.navigateToNextTab()).navigateToNextTab();
        softAssert.assertTrue(scheduledFlowsPage.isFlowPresent(weeklyFlowName), "Schedule flow is not deleted");
        softAssert.assertAll();
    }

    @Story("Should delete flow")
    @Test(priority = 4)
    public void deleteDailyScheduleFlow() {
        assertNotNull(dailyFlowName, "Daily flow name is not set");

        ScheduledFlowsTab scheduledFlowsPage = adminToolsPage.openTab(SCHEDULED_FLOWS);
        scheduledFlowsPage = scheduledFlowsPage.deleteFlow(dailyFlowName)
                .cancel()
                .deleteFlow(dailyFlowName)
                .closePopUp(ScheduledFlowsTab.class);
        scheduledFlowsPage.deleteFlow(dailyFlowName)
                .remove();
        assertFalse(scheduledFlowsPage.isFlowPresent(dailyFlowName), "Schedule flow is not deleted");
    }

    private Object getUserConfigurationToken(String username) {
        String userConfigurationQuery = "SELECT id FROM " + USER_CONFIGURATION.getAPIName() + " WHERE OwnerId IN (SELECT id FROM User WHERE Username = '" + username + "')";
        String accessToken = isPackageHasNamespace ? salesforceApi.getASAppNamespace() + "__AccessToken__c" : "AccessToken__c";
        try {
            FindRecordsIDResponse findRecordsIDResponse = salesforceApi.recordsService().
                    executeQuery(userConfigurationQuery, FindRecordsIDResponse.class);
            if (findRecordsIDResponse.totalSize > 0) {
                Map<String, Object> result = salesforceApi.recordsService().getRecordFields(USER_CONFIGURATION, findRecordsIDResponse.records[0].Id, singletonList(accessToken));
                return result.get(accessToken);
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
