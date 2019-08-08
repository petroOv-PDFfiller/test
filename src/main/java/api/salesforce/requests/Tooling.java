package api.salesforce.requests;

import api.salesforce.responses.ExecuteApexResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.AllureAttachments;
import io.qameta.allure.Step;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import utils.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static core.check.Check.checkEquals;

public class Tooling extends ApiBase {
    private String basePath;
    private ObjectMapper objectMapper;

    public Tooling(String instanceUrl) {
        super(instanceUrl);
        this.basePath = "/services/data/v44.0";
        this.objectMapper = new ObjectMapper();
    }

    /**
     * @param anonymousApex apex code block
     * @return ExecuteApexResponse
     */
    @Step("Executing APEX Code: {0}")
    public ExecuteApexResponse executeApex(String anonymousApex) throws URISyntaxException, IOException {
        Logger.info("Executing anonymous apex...");
        String path = basePath + "/tooling/executeAnonymous/";

        URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path)
                .addParameter("anonymousBody", anonymousApex)
                .build();
        Logger.info("Request url: " + uri);
        AllureAttachments.jsonAttachment(uri.toString(), "Request url");

        HttpResponse httpResponse = get(uri).execute().returnResponse();
        String response = getResponseBody(httpResponse);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        checkEquals(statusCode, HttpStatus.SC_OK, "Status code");

        return objectMapper.readValue(response.replace("\\\"", "\""), ExecuteApexResponse.class);
    }
}
