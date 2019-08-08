package api.salesforce.requests.airslate;

import api.salesforce.entities.airslate.SetupWizardStage;
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

public class SetupWizard extends ApiBase {
    private String basePath;

    public SetupWizard(String instanceUrl, String packageNamespace) {
        super(instanceUrl);
        this.basePath = String.format("/services/apexrest%s/setupWizard", packageNamespace);
    }

    @Step("Getting current Wizard Stage")
    public HttpResponse getCurrentStage() {
        HttpResponse response = null;
        try {
            Logger.info("Getting current Setup Wizard Step");
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

    @Step("Setting Wizard Stage")
    public HttpResponse setCurrentStage(SetupWizardStage.Stage wizardStage) {
        HttpResponse response = null;
        try {
            Logger.info("Setting current stage " + wizardStage.toString());
            URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(basePath).build();

            String requestBody = new JSONObject()
                    .put("stage", wizardStage.getStageName())
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
}
