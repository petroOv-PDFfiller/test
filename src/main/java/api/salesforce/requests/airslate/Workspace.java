package api.salesforce.requests.airslate;

import api.salesforce.entities.airslate.WorkspaceConfig;
import api.salesforce.requests.ApiBase;
import core.AllureAttachments;
import io.qameta.allure.Step;
import org.apache.http.HttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Logger;
import utils.StringMan;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Workspace extends ApiBase {
    private String basePath;

    public Workspace(String instanceUrl, String packageNamespace) {
        super(instanceUrl);
        this.basePath = String.format("/services/apexrest%s/workspaceConfig", packageNamespace);
    }

    @Step("Connect Workspace")
    public HttpResponse connectWorkspace(WorkspaceConfig workspaceConfig) {
        HttpResponse response = null;
        try {
            Logger.info("Connecting airSlate workspace...");
            URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(basePath).build();

            String requestBody = new JSONObject()
                    .put("workspaceId", workspaceConfig.getWorkspaceId())
                    .put("workspaceSubdomain", workspaceConfig.getWorkspaceSubdomain())
                    .toString();
            Logger.info("Request url: POST " + uri);
            Logger.info("Request body: " + requestBody);
            AllureAttachments.jsonAttachment(uri.toString(), "Request url: POST ");
            AllureAttachments.jsonAttachment(StringMan.jsonPrettyPrint(requestBody), "Request body");
            response = super.post(uri).bodyString(requestBody, ContentType.APPLICATION_JSON)
                    .execute().returnResponse();
            return response;
        } catch (IOException | URISyntaxException | JSONException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }

    @Step("Disconnect Workspace")
    public HttpResponse disconnectWorkspace() {
        HttpResponse response = null;
        try {
            Logger.info("Disconnecting airSlate workspace");
            URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(basePath).build();

            Logger.info("Request url: DELETE " + uri);
            AllureAttachments.jsonAttachment(uri.toString(), "Request url: DELETE ");
            response = super.delete(uri).execute().returnResponse();
            return response;
        } catch (IOException | URISyntaxException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }

    @Step("Getting Workspace Config")
    public HttpResponse getWorkspaceConfig() {
        HttpResponse response = null;
        try {
            Logger.info("Getting connected airSlate workspace information");
            URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(basePath).build();

            Logger.info("Request url: GET " + uri);
            AllureAttachments.jsonAttachment(uri.toString(), "Request url: GET ");
            response = super.get(uri).execute().returnResponse();
            return response;
        } catch (IOException | URISyntaxException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }
}
