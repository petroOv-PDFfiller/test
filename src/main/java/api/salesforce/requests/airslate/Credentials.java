package api.salesforce.requests.airslate;

import api.salesforce.entities.airslate.SalesforceAirslateUser;
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

public class Credentials extends ApiBase {
    private String basePath;

    public Credentials(String instanceUrl, String packageNamespace) {
        super(instanceUrl);
        this.basePath = String.format("/services/apexrest%s/userConfig/credentials", packageNamespace);
    }

    @Step("Authorizing airSlate app user")
    public HttpResponse authorizeUser(SalesforceAirslateUser user) {
        HttpResponse response = null;
        try {
            Logger.info("Authorizing airSlate user...");
            URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(basePath).build();

            String requestBody = new JSONObject()
                    .put("login", user.getLogin())
                    .put("password", user.getPassword())
                    .put("isAdmin", user.isAdmin())
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

    @Step("Disconnecting app user")
    public HttpResponse disconnectUser() {
        HttpResponse response = null;
        try {
            Logger.info("Disconnecting airSlate user...");
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

    @Step("Getting user credentials")
    public HttpResponse getUserCredentials() {
        HttpResponse response = null;
        try {
            Logger.info("Getting connected user credentials...");
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
