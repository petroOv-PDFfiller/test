package tests.salesforce.airSlate_app.api;

import api.salesforce.entities.airslate.ScheduledFlow;
import api.salesforce.responses.FindSchedulerResponse;
import com.airslate.api.models.slates.Slate;
import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import tests.salesforce.SalesforceAirSlateBaseTest;

import java.io.IOException;
import java.net.URISyntaxException;

import static utils.StringMan.getRandomString;

public class SchedulerTests extends SalesforceAirSlateBaseTest {
    private Slate flow;

    @BeforeTest
    public void initAirSlateAppSettings() throws IOException {
        configurateSalesforceOrg();
        flow = createFlowWithDocument(fileToUpload);
    }

    @Test
    public void createDailySOQLSchedulerTest() {
        ScheduledFlow.Flow sfFlow = new ScheduledFlow.Flow()
                .setId(flow.id).setName(flow.name);

        ScheduledFlow.Params params = new ScheduledFlow.Params()
                .setQuery("SELECT Id FROM Account LIMIT 2");

        ScheduledFlow.TimeParams timeParams = new ScheduledFlow.TimeParams();
        timeParams.setType("daily");
        timeParams.setDdd(new String[]{});
        timeParams.setTt("AM");
        timeParams.setHh("12");
        timeParams.setMm("00");
        String expectedSchedulerName = "Scheduler " + getRandomString(5);
        String expectedFormattedDate = "{\"ddd\":[],\"tt\":\"AM\",\"mm\":\"00\",\"hh\":\"12\",\"type\":\"daily\"}";
        String expectedFormattedParams = "{\"report\":null,\"query\":\"SELECT Id FROM Account LIMIT 2\"}";
        createScheduledFlow(sfFlow, expectedSchedulerName, ScheduledFlow.Option.SOQL_QUERY, params, timeParams);

        FindSchedulerResponse findSchedulerResponse = getSchedulerRecord(expectedSchedulerName);
        Assert.assertNotNull(findSchedulerResponse, "Response should not be null");
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(findSchedulerResponse.records[0].schedulerName, expectedSchedulerName, "Scheduler name");
        softAssert.assertEquals(findSchedulerResponse.records[0].date, expectedFormattedDate, "Scheduler formatted date");
        softAssert.assertEquals(findSchedulerResponse.records[0].params, expectedFormattedParams, "Scheduler formatted params");
        softAssert.assertAll();
    }

    @Test
    public void createWeeklySOQLSchedulerTest() {
        ScheduledFlow.Flow sfFlow = new ScheduledFlow.Flow()
                .setId(flow.id).setName(flow.name);

        ScheduledFlow.Params params = new ScheduledFlow.Params()
                .setQuery("SELECT Id, Name FROM Opportunity");

        ScheduledFlow.TimeParams timeParams = new ScheduledFlow.TimeParams();
        timeParams.setType("weekly");
        timeParams.setDdd(new String[]{"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"});
        timeParams.setTt("PM");
        timeParams.setHh("03");
        timeParams.setMm("56");

        String expectedSchedulerName = "Scheduler " + getRandomString(5);
        String expectedFormattedDate = "{\"ddd\":[\"MON\",\"TUE\",\"WED\",\"THU\",\"FRI\",\"SAT\",\"SUN\"],\"tt\":\"PM\",\"mm\":\"56\",\"hh\":\"03\",\"type\":\"weekly\"}";
        String expectedFormattedParams = "{\"report\":null,\"query\":\"SELECT Id, Name FROM Opportunity\"}";
        createScheduledFlow(sfFlow, expectedSchedulerName, ScheduledFlow.Option.SOQL_QUERY, params, timeParams);

        FindSchedulerResponse findSchedulerResponse = getSchedulerRecord(expectedSchedulerName);
        Assert.assertNotNull(findSchedulerResponse, "Response should not be null");
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(findSchedulerResponse.records[0].schedulerName, expectedSchedulerName, "Scheduler name");
        softAssert.assertEquals(findSchedulerResponse.records[0].date, expectedFormattedDate, "Scheduler formatted date");
        softAssert.assertEquals(findSchedulerResponse.records[0].params, expectedFormattedParams, "Scheduler formatted params");
        softAssert.assertAll();
    }

    @Test
    public void createDailyReportSchedulerTest() {
        ScheduledFlow.Flow sfFlow = new ScheduledFlow.Flow()
                .setId(flow.id).setName(flow.name);

        ScheduledFlow.Report report = new ScheduledFlow.Report()
                .setReportId("00O0N0000071YCPUA2")
                .setReportName("Account Report");
        ScheduledFlow.Params params = new ScheduledFlow.Params().setReport(report);

        ScheduledFlow.TimeParams timeParams = new ScheduledFlow.TimeParams();
        timeParams.setType("daily");
        timeParams.setDdd(new String[]{});
        timeParams.setTt("AM");
        timeParams.setHh("12");
        timeParams.setMm("00");
        String expectedSchedulerName = "Scheduler " + getRandomString(5);
        String expectedFormattedDate = "{\"ddd\":[],\"tt\":\"AM\",\"mm\":\"00\",\"hh\":\"12\",\"type\":\"daily\"}";
        String expectedFormattedParams = "{\"report\":{\"name\":\"Account Report\",\"id\":\"00O0N0000071YCPUA2\"},\"query\":null}";
        createScheduledFlow(sfFlow, expectedSchedulerName, ScheduledFlow.Option.SF_REPORT, params, timeParams);


        FindSchedulerResponse findSchedulerResponse = getSchedulerRecord(expectedSchedulerName);
        Assert.assertNotNull(findSchedulerResponse, "Response should not be null");
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(findSchedulerResponse.records[0].schedulerName, expectedSchedulerName, "Scheduler name");
        softAssert.assertEquals(findSchedulerResponse.records[0].date, expectedFormattedDate, "Scheduler formatted date");
        softAssert.assertEquals(findSchedulerResponse.records[0].params, expectedFormattedParams, "Scheduler formatted params");
        softAssert.assertAll();
    }

    @Step("Get Scheduler record WHERE Name = '{0}'")
    private FindSchedulerResponse getSchedulerRecord(String expectedSchedulerName) {
        String appNamespace = salesforceApi.getASAppNamespace();
        String query = String.format("SELECT Id, %1$sName__c, %1$sOption__c, %1$sDate__c, %1$sParams__c FROM %1$sScheduler__c WHERE %1$sName__c = '%2$s' LIMIT 1",
                appNamespace == null ? StringUtils.EMPTY : appNamespace + "__", expectedSchedulerName);
        try {
            return salesforceApi.recordsService().executeQuery(query, FindSchedulerResponse.class);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @AfterMethod
    public void deleteAllSchedulers() {
        deleteAllScheduledFlows();
    }
}
