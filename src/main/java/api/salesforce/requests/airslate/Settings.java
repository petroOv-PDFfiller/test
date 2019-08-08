package api.salesforce.requests.airslate;

import api.salesforce.requests.ApiBase;
import core.AllureAttachments;
import io.qameta.allure.Step;
import org.apache.http.HttpResponse;
import org.apache.http.client.utils.URIBuilder;
import utils.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Settings extends ApiBase {
    private String basePath;

    public Settings(String instanceUrl, String packageNamespace) {
        super(instanceUrl);
        this.basePath = String.format("/services/apexrest%s/settings", packageNamespace);
    }

    @Step("Getting package Settings")
    public HttpResponse getSettings() {
        HttpResponse response = null;
        try {
            Logger.info("Getting package settings ...");
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
