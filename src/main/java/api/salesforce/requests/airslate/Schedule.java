package api.salesforce.requests.airslate;

import api.salesforce.entities.airslate.ScheduledFlow;
import api.salesforce.requests.ApiBase;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import core.AllureAttachments;
import io.qameta.allure.Step;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import utils.Logger;
import utils.StringMan;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class Schedule extends ApiBase {
    private String basePath;

    public Schedule(String instanceUrl, String packageNamespace) {
        super(instanceUrl);
        this.basePath = String.format("/services/apexrest%s/schedule", packageNamespace);
    }

    @Step("Create new Scheduled Flow")
    public HttpResponse createScheduledFlow(ScheduledFlow scheduledFlow) {
        HttpResponse response = null;
        try {
            Logger.info("Creating new scheduled flow...");
            URIBuilder uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(basePath);

            Logger.info("Request url: POST " + uri);
            Logger.info("Request body: \n" + scheduledFlow.toString());
            AllureAttachments.jsonAttachment(uri.toString(), "Request url");
            AllureAttachments.jsonAttachment(StringMan.jsonPrettyPrint(scheduledFlow.toString()), "Request body");
            response = super.post(uri.build()).bodyString(scheduledFlow.toString(), ContentType.APPLICATION_JSON)
                    .execute().returnResponse();
            return response;
        } catch (IOException | URISyntaxException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }

    @Step("Getting Scheduled Flows list")
    public HttpResponse getScheduledFlowsList(List<NameValuePair> queryParams) {
        HttpResponse response = null;
        try {
            Logger.info("Getting Scheduled flows list...");
            URIBuilder uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(basePath);
            if (queryParams != null) {
                uri.setParameters(queryParams);
            }

            Logger.info("Request url: GET " + uri);
            AllureAttachments.jsonAttachment(uri.toString(), "Request url:");
            response = super.get(uri.build()).execute().returnResponse();
            return response;
        } catch (IOException | URISyntaxException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }

    @Step("Delete Scheduled Flow with ID: {0}")
    public HttpResponse deleteScheduledFlow(String scheduleId) {
        HttpResponse response = null;
        try {
            Logger.info("Deleting scheduled flow...");
            URIBuilder uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(basePath)
                    .setParameter("scheduleId", scheduleId);

            Logger.info("Request url: DELETE " + uri);
            AllureAttachments.jsonAttachment(uri.toString(), "Request url: ");
            response = super.delete(uri.build()).execute().returnResponse();
            return response;
        } catch (IOException | URISyntaxException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }

    @Step("Activate Scheduled Flow with ID: {0}")
    public HttpResponse activateScheduledFlow(String scheduleId) {
        String activatePath = basePath + "/activate";
        HttpResponse response = null;
        try {
            Logger.info("Activating scheduled flow...");
            URIBuilder uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(activatePath);
            ObjectNode jsonBody = JsonNodeFactory.instance.objectNode();
            jsonBody.put("scheduleId", scheduleId);

            Logger.info("Request url: POST " + uri);
            AllureAttachments.jsonAttachment(uri.toString(), "Request url: ");
            response = super.post(uri.build()).bodyString(jsonBody.toString(), ContentType.APPLICATION_JSON)
                    .execute().returnResponse();
            return response;
        } catch (IOException | URISyntaxException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }

    @Step("Deactivate Scheduled Flow with ID: {0}")
    public HttpResponse deactivateScheduledFlow(String scheduleId) {
        String deactivatePath = basePath + "/deactivate";
        HttpResponse response = null;
        try {
            Logger.info("Deactivating scheduled flow...");
            URIBuilder uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(deactivatePath);
            ObjectNode jsonBody = JsonNodeFactory.instance.objectNode();
            jsonBody.put("scheduleId", scheduleId);

            Logger.info("Request url: POST " + uri);
            AllureAttachments.jsonAttachment(uri.toString(), "Request url: ");
            response = super.post(uri.build()).bodyString(jsonBody.toString(), ContentType.APPLICATION_JSON)
                    .execute().returnResponse();
            return response;
        } catch (IOException | URISyntaxException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }
}
