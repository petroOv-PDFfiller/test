package utils.salesforce;

import api.salesforce.SalesforceAirslateApi;
import api.salesforce.SalesforceRestApi;
import api.salesforce.entities.SalesforceObject;
import api.salesforce.entities.airslate.*;
import api.salesforce.responses.FindRecordsIDResponse;
import api.salesforce.util.HttpResponseUtils;
import com.airslate.api.models.onboarding.Organization;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import core.check.Check;
import data.TestData;
import io.qameta.allure.Step;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import utils.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static api.salesforce.entities.SalesforceObject.USER_CONFIGURATION;

public class SalesforceAirSlateMan {

    @Step("Delete User Config for {1}")
    public void deleteUserConfigurationByUsername(SalesforceRestApi salesforceRestApi, String username) {
        String userConfigurationQuery = "SELECT id FROM " + USER_CONFIGURATION.getAPIName() + " WHERE OwnerId IN (SELECT id FROM User WHERE Username = '" + username + "')";
        try {
            new SalesforceMan().clearRecords(salesforceRestApi, USER_CONFIGURATION, userConfigurationQuery);
        } catch (Exception e) {
            Logger.warning("Error on deletion");
        }
    }

    @Step("Delete user config token for {1}")
    public void eraseUserConfigurationToken(SalesforceRestApi salesforceRestApi, String username) {
        String userConfigurationQuery = "SELECT id FROM " + USER_CONFIGURATION.getAPIName() + " WHERE OwnerId IN (SELECT id FROM User WHERE Username = '" + username + "')";
        String accessToken = SalesforceRestApi.isPackageHasNamespace ? salesforceRestApi.getASAppNamespace() + "__AccessToken__c" : "AccessToken__c";
        try {
            FindRecordsIDResponse findRecordsIDResponse = salesforceRestApi.recordsService().
                    executeQuery(userConfigurationQuery, FindRecordsIDResponse.class);
            for (int i = 0; i < findRecordsIDResponse.totalSize; i++) {
                salesforceRestApi.recordsService().updateRecord(USER_CONFIGURATION, findRecordsIDResponse.records[i].Id, ImmutableMap.of(accessToken, ""));
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    @Step("Set SetupWizard stage: {1}")
    public void setSetupWizardStage(SalesforceAirslateApi salesforceAirSlateApi, SetupWizardStage.Stage stage) {
        HttpResponse response = salesforceAirSlateApi.setupWizard().setCurrentStage(stage);
        Check.checkEquals(response.getStatusLine().getStatusCode(), 200, "SetupWizard stage is not set");
    }

    @Step("Connect airSlate app admin with email: {1}")
    public void connectAdmin(SalesforceAirslateApi salesforceAirSlateApi, String email) {
        HttpResponse response = salesforceAirSlateApi.credentials().authorizeUser(new SalesforceAirslateUser(email, TestData.defaultPassword, true));
        Check.checkEquals(response.getStatusLine().getStatusCode(), 200, "Admin is not connected");
    }

    @Step("Connect airSlate app user with email: {email}")
    public void connectStandardUser(SalesforceRestApi salesforceRestApi, String baseUrl, String salesforceUsername, String salesforcePassword, String email) {
        SalesforceAirslateApi salesforceAirslateApi = new SalesforceAirslateApi(baseUrl, salesforceUsername, salesforcePassword, salesforceRestApi.getASAppNamespace());
        salesforceAirslateApi.auth();
        salesforceAirslateApi.credentials().authorizeUser(new SalesforceAirslateUser(email, TestData.defaultPassword, false));
    }

    @Step("Disconnecting connected airSlate app admin account")
    public void disconnectAdmin(SalesforceAirslateApi salesforceAirSlateApi) {
        HttpResponse response = salesforceAirSlateApi.credentials().disconnectUser();
        Check.checkEquals(response.getStatusLine().getStatusCode(), 200, "Admin is not connected");
    }

    @Step("Connecting app workspace: {organization.subdomain}")
    public void connectWorkspace(SalesforceAirslateApi salesforceAirSlateApi, Organization organization) {
        HttpResponse response = salesforceAirSlateApi.workspace().connectWorkspace(new WorkspaceConfig(organization.id, organization.subdomain));
        Check.checkEquals(response.getStatusLine().getStatusCode(), 200, "Workspace is not connected");
    }

    @Step("Disconnecting connected Workspace")
    public void disconnectWorkspace(SalesforceAirslateApi salesforceAirSlateApi) {
        HttpResponse response = salesforceAirSlateApi.workspace().disconnectWorkspace();
        Check.checkEquals(response.getStatusLine().getStatusCode(), 200, "Workspace is not connected");
    }

    @Step("Creating new Custom Button")
    public CustomButton createCustomButton(SalesforceAirslateApi salesforceAirSlateApi,
                                           CustomButton customButton,
                                           String flowId,
                                           String flowName,
                                           CustomButton.Mode mode,
                                           String description,
                                           String label,
                                           CustomButton.Action action,
                                           List<SalesforceObject> layouts,
                                           List<SalesforceObject> listViews) {
        CustomButton.CBParameters params = customButton.new CBParameters();
        params.setFlowId(flowId);
        params.setFlowName(flowName);
        params.setMode(mode);

        customButton.setDescription(description);
        customButton.setLabel(label);
        customButton.setAction(action);
        customButton.setLayoutsNew(CollectionUtils.emptyIfNull(layouts).stream().map(layout -> String.format("%s-%s Layout", layout.getAPIName(), layout.getObjectName())).collect(Collectors.toList()));
        customButton.setObjectsNew(CollectionUtils.emptyIfNull(listViews).stream().map(listview -> String.format("%s", listview.getAPIName())).collect(Collectors.toList()));
        customButton.setParams(params);

        HttpResponse response = salesforceAirSlateApi.buttonConfig().insertCustomButton(customButton);
        Check.checkEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Status code");
        Check.checkEquals((int) getAllCustomButtons(salesforceAirSlateApi).stream().filter(button -> button.getLabel().equals(label)).count(),
                1, "Custom button " + label + " is not created");
        return getAllCustomButtons(salesforceAirSlateApi).stream().filter(button -> button.getLabel().equals(label)).findFirst().orElse(null);
    }

    @Step("Deleting all created Custom Buttons")
    public void deleteAllCustomButtons(SalesforceAirslateApi salesforceAirSlateApi) {
        List<CustomButton> buttons = getAllCustomButtons(salesforceAirSlateApi);
        buttons.forEach(button -> salesforceAirSlateApi.buttonConfig().deleteCustomButton(button.getId()));
        Check.checkEquals(getAllCustomButtons(salesforceAirSlateApi).size(), 0, "CustomButton is still exists");
    }

    @Step("Getting Custom Button list")
    private List<CustomButton> getAllCustomButtons(SalesforceAirslateApi salesforceAirSlateApi) {
        HttpResponse response = salesforceAirSlateApi
                .buttonConfig()
                .getCustomButtonList(null);
        try {
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Check.checkEquals(response.getStatusLine().getStatusCode(), 200, "Incorrect status code");
        ResponseBody<List<CustomButton>> buttonsResponse = HttpResponseUtils.parseResponseWithCollection(response, CustomButton.class);
        if (buttonsResponse == null) {
            return Collections.emptyList();
        } else {
            return buttonsResponse.getData();
        }
    }

    @Step("Creating new Scheduled FLOW")
    public void createScheduledFlow(SalesforceAirslateApi salesforceAirSlateApi,
                                    ScheduledFlow.Flow flow,
                                    String name,
                                    ScheduledFlow.Option option,
                                    ScheduledFlow.Params params,
                                    ScheduledFlow.TimeParams timeParams) {
        ScheduledFlow scheduledFlow = new ScheduledFlow();
        scheduledFlow.setFlow(flow);
        scheduledFlow.setName(name);
        scheduledFlow.setOption(option);
        scheduledFlow.setParams(params);
        scheduledFlow.setTimeParams(timeParams);

        HttpResponse response = salesforceAirSlateApi.schedule().createScheduledFlow(scheduledFlow);
        Check.checkEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Status code");
    }

    @Step("Getting Scheduled FLOWs list")
    private List<ScheduledFlow> getAllScheduledFlows(SalesforceAirslateApi salesforceAirSlateApi) {
        HttpResponse response = salesforceAirSlateApi
                .schedule()
                .getScheduledFlowsList(Arrays.asList(new BasicNameValuePair("page", "1"),
                        new BasicNameValuePair("orderby", "name"),
                        new BasicNameValuePair("perpage", "100"),
                        new BasicNameValuePair("order", "asc"),
                        new BasicNameValuePair("filter[0][field]", "search"),
                        new BasicNameValuePair("filter[0][operation]", "like"),
                        new BasicNameValuePair("filter[0][value]", "")));
        try {
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Check.checkEquals(response.getStatusLine().getStatusCode(), 200, "Incorrect status code");
        ResponseBody<List<ScheduledFlow>> flowResponse = HttpResponseUtils.parseResponseWithCollection(response, ScheduledFlow.class);
        if (flowResponse == null) {
            return Collections.emptyList();
        } else {
            return flowResponse.getData();
        }
    }

    @Step("Deleting all Scheduled FLOWs")
    public void deleteAllScheduledFlows(SalesforceAirslateApi salesforceAirSlateApi) {
        List<ScheduledFlow> flows = getAllScheduledFlows(salesforceAirSlateApi);
        flows.forEach(flow -> salesforceAirSlateApi.schedule().deleteScheduledFlow(flow.getId()));
        Check.checkEquals(getAllScheduledFlows(salesforceAirSlateApi).size(), 0, "Scheduled flows are still exists");
    }

    public void createBlankFlowPacketFromContext(SalesforceRestApi salesforceRestApi, String baseUrl, String recordId, SalesforceObject object, String salesforceUsername, String password, com.airslate.api.models.slates.Slate flow, String buttonLabel) {
        SalesforceAirslateApi salesforceAirslateApi = new SalesforceAirslateApi(baseUrl, salesforceUsername, password, salesforceRestApi.getASAppNamespace());
        salesforceAirslateApi.auth();
        Slate salesforceSlate = new Slate();
        salesforceSlate.setFlowId(flow.id);
        salesforceSlate.setFlowName(flow.name);
        salesforceSlate.setObjectName(object.getAPIName());
        salesforceSlate.setRecordIds(ImmutableList.of(recordId));
        salesforceSlate.setCustomButtonName(buttonLabel);

        List<Slate> before = HttpResponseUtils.parseResponse(salesforceAirslateApi.slates().getSlatesFromUsedFlow(recordId, flow.id), SlateActivityResponse.class).getData();

        HttpResponse response = salesforceAirslateApi.slates().createSlate(salesforceSlate);
        System.out.println(response.getStatusLine());
        List<Slate> slates = HttpResponseUtils.parseResponse(salesforceAirslateApi.slates().getSlatesFromUsedFlow(recordId, flow.id), SlateActivityResponse.class)
                .getData()
                .stream()
                .filter(slate -> !before.contains(slate))
                .collect(Collectors.toList());
        Check.checkTrue(slates.size() > 0, "No one new slate is present");
    }
}