package tests.salesforce.airSlate_app.api;

import api.salesforce.entities.SalesforceObject;
import api.salesforce.entities.airslate.CustomButton;
import api.salesforce.responses.FindAirSlateButtonResponse;
import api.salesforce.responses.FindOpportunityResponse;
import io.qameta.allure.Step;
import listeners.WebTestListener;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import tests.salesforce.SalesforceAirSlateBaseTest;

import java.io.IOException;
import java.net.URISyntaxException;

import static java.util.Collections.singletonList;
import static utils.StringMan.getRandomString;

@Listeners({WebTestListener.class})
public class CustomButtonTests extends SalesforceAirSlateBaseTest {
    private String dummyFlowId = "F899C25A-5000-0000-0000BA29";

    @BeforeTest
    public void initAirSlateAppSettings() {
        configurateSalesforceOrg();
    }

    @Test
    public void createSlate_buttonTest() {
        String expectedFlowName = getRandomString(255);
        String expectedParams = String.format("{\"mode\":\"default\",\"flowName\":\"%1$s\",\"flowId\":\"%2$s\"}", expectedFlowName, dummyFlowId);
        String cbDescription = "Some description";
        String cbLabel = "Create Slate " + getRandomString(5);
        createCustomButton(dummyFlowId,
                expectedFlowName,
                CustomButton.Mode.DEFAULT,
                cbDescription, cbLabel,
                CustomButton.Action.RUN_FLOW,
                singletonList(SalesforceObject.CAMPAIGN), null);

        FindAirSlateButtonResponse findAirSlateButtonResponse = getAirSlateButtonRecord(cbLabel);
        Assert.assertNotNull(findAirSlateButtonResponse, "Response should not be null");
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(findAirSlateButtonResponse.records[0].label, cbLabel, "Custom Button label");
        softAssert.assertFalse(findAirSlateButtonResponse.records[0].isActive, "Custom Button isActive");
        softAssert.assertEquals(findAirSlateButtonResponse.records[0].params, expectedParams, "Custom Button formatted params");
        softAssert.assertAll();
    }

    @Test
    public void createAndOpenSlate_buttonTest() {
        String expectedFlowName = getRandomString(1);
        String expectedParams = String.format("{\"mode\":\"open_slate\",\"flowName\":\"%1$s\",\"flowId\":\"%2$s\"}", expectedFlowName, dummyFlowId);
        String cbDescription = getRandomString(255);
        String cbLabel = "Create Slate " + getRandomString(5);
        createCustomButton(dummyFlowId,
                expectedFlowName,
                CustomButton.Mode.OPEN_SLATE,
                cbDescription, cbLabel,
                CustomButton.Action.RUN_FLOW,
                singletonList(SalesforceObject.OPPORTUNITY), null);

        FindAirSlateButtonResponse findAirSlateButtonResponse = getAirSlateButtonRecord(cbLabel);
        Assert.assertNotNull(findAirSlateButtonResponse, "Response should not be null");
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(findAirSlateButtonResponse.records[0].label, cbLabel, "Custom Button label");
        softAssert.assertFalse(findAirSlateButtonResponse.records[0].isActive, "Custom Button isActive");
        softAssert.assertEquals(findAirSlateButtonResponse.records[0].params, expectedParams, "Custom Button formatted params");
        softAssert.assertAll();
    }

    @Test
    public void createAndSendSlate_buttonTest() {
        String expectedFlowName = getRandomString(50);
        String expectedParams = String.format("{\"mode\":\"send_slate\",\"flowName\":\"%1$s\",\"flowId\":\"%2$s\"}", expectedFlowName, dummyFlowId);
        String cbDescription = getRandomString(255);
        String cbLabel = "CreateAndSend Slate " + getRandomString(5);
        createCustomButton(dummyFlowId,
                expectedFlowName,
                CustomButton.Mode.SEND_SLATE,
                cbDescription, cbLabel,
                CustomButton.Action.RUN_FLOW,
                singletonList(SalesforceObject.LEAD), null);

        FindAirSlateButtonResponse findAirSlateButtonResponse = getAirSlateButtonRecord(cbLabel);
        Assert.assertNotNull(findAirSlateButtonResponse, "Response should not be null");
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(findAirSlateButtonResponse.records[0].label, cbLabel, "Custom Button label");
        softAssert.assertFalse(findAirSlateButtonResponse.records[0].isActive, "Custom Button isActive");
        softAssert.assertEquals(findAirSlateButtonResponse.records[0].params, expectedParams, "Custom Button formatted params");
        softAssert.assertAll();
    }

    @Test
    public void invokeProcess_buttonTest() throws IOException, URISyntaxException {
        String expectedFlowName = getRandomString(256);
        String expectedParams = String.format("{\"mode\":null,\"flowName\":\"%1$s\",\"flowId\":\"%2$s\"}", expectedFlowName, dummyFlowId);
        String cbLabelAndDescription = getRandomString(255);
        createCustomButton(dummyFlowId,
                expectedFlowName,
                null,
                cbLabelAndDescription, cbLabelAndDescription,
                CustomButton.Action.INVOKE_PROCESS,
                singletonList(SalesforceObject.OPPORTUNITY), null);

        FindAirSlateButtonResponse findAirSlateButtonResponse = getAirSlateButtonRecord(cbLabelAndDescription);
        Assert.assertNotNull(findAirSlateButtonResponse, "Response should not be null");
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(findAirSlateButtonResponse.records[0].label, cbLabelAndDescription, "Custom Button label");
        softAssert.assertFalse(findAirSlateButtonResponse.records[0].isActive, "Custom Button isActive");
        softAssert.assertEquals(findAirSlateButtonResponse.records[0].params, expectedParams, "Custom Button formatted params");
        softAssert.assertAll();

        String query = "SELECT Id, airSlate_Invoke_Process__c FROM Opportunity LIMIT 1";
        FindOpportunityResponse findOpportunityResponse = salesforceApi.recordsService().executeQuery(query, FindOpportunityResponse.class);
        Assert.assertNotNull(findOpportunityResponse, "Response should not be null");
        Assert.assertNull(findOpportunityResponse.records.get(0).airSlateInvokeProcess, "airSlate_Invoke_Process field is present formatted params");
    }

    @Step("Getting airSlate_Button__c record WHERE Label = '{0}'")
    private FindAirSlateButtonResponse getAirSlateButtonRecord(String expectedButtonLabel) {
        String appNamespace = salesforceApi.getASAppNamespace();
        String query = String.format("SELECT Id, %1$sLabel__c, %1$sParams__c, %1$sIsActive__c FROM %1$sairSlate_Button__c WHERE %1$sLabel__c = '%2$s' LIMIT 1",
                appNamespace == null ? StringUtils.EMPTY : appNamespace + "__", expectedButtonLabel);
        try {
            return salesforceApi.recordsService().executeQuery(query, FindAirSlateButtonResponse.class);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @AfterMethod
    public void deleteAllButtons() {
        deleteAllCustomButtons();
    }
}
