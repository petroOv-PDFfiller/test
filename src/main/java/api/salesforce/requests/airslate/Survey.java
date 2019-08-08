package api.salesforce.requests.airslate;

import api.salesforce.entities.airslate.FeedbackSurvey;
import api.salesforce.requests.ApiBase;
import core.AllureAttachments;
import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
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

public class Survey extends ApiBase {
    private String basePath;

    public Survey(String instanceUrl, String packageNamespace) {
        super(instanceUrl);
        this.basePath = String.format("/services/apexrest%s/survey", packageNamespace);
    }

    @Step("Sending Feedback Survey")
    public HttpResponse sendFeedbackSurvey(FeedbackSurvey survey) {
        HttpResponse response = null;
        try {
            Logger.info("Sending feedback survey ...");
            URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(basePath).build();

            JSONObject requestBody = new JSONObject()
                    .put("subject", survey.getSubject())
                    .put("message", survey.getMessage());
            if (survey.getSubject().equals(StringUtils.EMPTY)) {
                requestBody.remove("subject");
            }
            Logger.info("Request url: POST " + uri);
            Logger.info("Request body: " + requestBody);
            AllureAttachments.jsonAttachment(uri.toString(), "Request url: POST ");
            AllureAttachments.jsonAttachment(StringMan.jsonPrettyPrint(requestBody.toString()), "Request body");
            response = super.post(uri).bodyString(requestBody.toString(), ContentType.APPLICATION_JSON)
                    .execute().returnResponse();
            return response;
        } catch (IOException | URISyntaxException | JSONException e) {
            Logger.error(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }
}
