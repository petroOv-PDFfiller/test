package api.salesforce.requests.airslate;

import api.salesforce.entities.airslate.CustomButton;
import api.salesforce.requests.ApiBase;
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

public class ButtonConfig extends ApiBase {
    private String basePath;

    public ButtonConfig(String instanceUrl, String packageNamespace) {
        super(instanceUrl);
        this.basePath = String.format("/services/apexrest%s/buttonConfig", packageNamespace);
    }

    @Step("Add custom button")
    public HttpResponse insertCustomButton(CustomButton customButton) {
        HttpResponse response = null;
        try {
            Logger.info("Adding new Custom Button: \n" + customButton.buttonToCreateString());
            URIBuilder uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(basePath);

            String requestBody = customButton.toString();

            Logger.info("Request url: POST " + uri);
            Logger.info("Request body: " + requestBody);
            AllureAttachments.jsonAttachment(uri.toString(), "Request url: POST ");
            AllureAttachments.jsonAttachment(StringMan.jsonPrettyPrint(requestBody), "Request body");
            response = super.post(uri.build()).bodyString(requestBody, ContentType.APPLICATION_JSON)
                    .execute().returnResponse();
            return response;
        } catch (IOException | URISyntaxException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }

    @Step("Delete Custom Button with ID: {0}")
    public HttpResponse deleteCustomButton(String buttonId) {
        HttpResponse response = null;
        try {
            Logger.info("Deleting custom button...");
            URIBuilder uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(basePath)
                    .setParameter("buttonId", buttonId);

            Logger.info("Request url: DELETE " + uri);
            AllureAttachments.jsonAttachment(uri.toString(), "Request url: DELETE");
            response = super.delete(uri.build()).execute().returnResponse();
            return response;
        } catch (IOException | URISyntaxException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }

    @Step("Getting custom button list")
    public HttpResponse getCustomButtonList(List<NameValuePair> queryParams) {
        HttpResponse response = null;
        try {
            Logger.info("Getting custom button list...");
            URIBuilder uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(basePath);
            if (queryParams != null) {
                uri.setParameters(queryParams);
            }

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
