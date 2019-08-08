package api.salesforce.requests.airslate;

import api.salesforce.entities.airslate.Slate;
import api.salesforce.requests.ApiBase;
import core.AllureAttachments;
import io.qameta.allure.Step;
import org.apache.http.HttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import utils.Logger;
import utils.StringMan;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Slates extends ApiBase {
    private String basePath;

    public Slates(String instanceUrl, String packageNamespace) {
        super(instanceUrl);
        this.basePath = String.format("/services/apexrest%s", packageNamespace);
    }

    /**
     * This method creates slate with first blank revision in airSlate
     * AND Salesforce record in SlatesActivity object.
     */
    @Step("Create new Slate")
    public HttpResponse createSlate(Slate slate) {
        HttpResponse response = null;
        String path = basePath + "/slates";
        try {
            Logger.info("Creating new slate in salesforce...");
            URIBuilder uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path);

            Logger.info("Request url: POST " + uri);
            Logger.info("Request body: \n" + slate.toString());
            AllureAttachments.jsonAttachment(uri.toString(), "Request url:");
            AllureAttachments.jsonAttachment(StringMan.jsonPrettyPrint(slate.toString()), "Request body");
            response = super.post(uri.build()).bodyString(slate.toString(), ContentType.APPLICATION_JSON)
                    .execute().returnResponse();
            return response;
        } catch (IOException | URISyntaxException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }

    /**
     * This method creates only Salesforce record in SlatesActivity object
     */
    @Step("Creating new Slate Activity")
    public HttpResponse createSlateActivity(Slate slate) {
        HttpResponse response = null;
        String path = basePath + "/slatesActivity";
        try {
            Logger.info("Creating new slateActivity...");
            URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path).build();

            Logger.info("Request url: POST " + uri);
            Logger.info("Request body: \n" + slate.toString());
            AllureAttachments.jsonAttachment(uri.toString(), "Request url: POST");
            AllureAttachments.jsonAttachment(StringMan.jsonPrettyPrint(slate.toString()), "Request body");
            response = super.post(uri).bodyString(slate.toString(), ContentType.APPLICATION_JSON)
                    .execute().returnResponse();
            return response;
        } catch (IOException | URISyntaxException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }

    @Step("Getting used Flows in record: {0}")
    public HttpResponse getUsedFlows(String recordId) {
        HttpResponse response = null;
        String path = basePath + "/slatesActivity/flow";
        try {
            Logger.info("Getting used Flows in Salesforce record...");
            URIBuilder uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path)
                    .setParameter("recordId", recordId);

            Logger.info("Request url: GET " + uri);
            AllureAttachments.jsonAttachment(uri.toString(), "Request url: GET");
            response = super.get(uri.build()).execute().returnResponse();
            return response;
        } catch (IOException | URISyntaxException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }

    @Step("Getting Slates from used Flow. recordId:{0} / flowId{1}")
    public HttpResponse getSlatesFromUsedFlow(String recordId, String flowId) {
        HttpResponse response = null;
        String path = basePath + "/slatesActivity/slate";
        try {
            Logger.info("Getting used Slates in Salesforce record...");
            URIBuilder uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path)
                    .setParameter("recordId", recordId)
                    .setParameter("flowId", flowId);

            Logger.info("Request url: GET " + uri);
            AllureAttachments.jsonAttachment(uri.toString(), "Request url: GET");
            response = super.get(uri.build()).execute().returnResponse();
            return response;
        } catch (IOException | URISyntaxException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }
}
