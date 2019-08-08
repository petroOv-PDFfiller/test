package api.salesforce.requests.airslate;

import api.salesforce.entities.airslate.ObjectsView;
import api.salesforce.requests.ApiBase;
import core.AllureAttachments;
import io.qameta.allure.Step;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import utils.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class ObjectView extends ApiBase {
    private String basePath;

    public ObjectView(String instanceUrl, String packageNamespace) {
        super(instanceUrl);
        this.basePath = String.format("/services/apexrest%s/", packageNamespace);
    }

    @Step("Getting object view {0}")
    public HttpResponse getObjectView(ObjectsView.View objectView, List<NameValuePair> queryParameters) {
        HttpResponse response = null;
        String path = basePath + objectView.getViewName();
        try {
            Logger.info("Getting available objects view ...");
            URIBuilder uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path);
            if (queryParameters != null) {
                uri.addParameters(queryParameters);
            }

            Logger.info("Request url: " + uri);
            AllureAttachments.jsonAttachment(uri.toString(), "Request url: ");
            response = super.get(uri.build()).execute().returnResponse();
            return response;
        } catch (IOException | URISyntaxException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }
}
