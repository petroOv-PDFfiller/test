package api.salesforce.requests.dadadocs;

import api.salesforce.entities.dadadocs.templates.SalesforceTemplate;
import api.salesforce.requests.ApiBase;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.List;

import static core.check.Check.checkEquals;

public class Templates extends ApiBase {

    private String basePath;
    private ObjectMapper objectMapper;

    public Templates(String instanceUrl, String packageNamespace) {
        super(instanceUrl);
        this.basePath = String.format("/services/apexrest/%s/documents", packageNamespace);
        this.objectMapper = new ObjectMapper();
    }

    /**
     * @param recordId salesforce record id
     * @return List of SalesforceTemplate entity
     */
    @Step
    public List<SalesforceTemplate> getAllTemplates(String recordId) throws URISyntaxException, IOException {
        Logger.info("Getting all uploaded documents");
        List<SalesforceTemplate> sfTemplates;
        String path = basePath + "/all_templates";
        URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path)
                .setParameter("parentId", recordId).build();

        Logger.info("Request url: " + uri);
        AllureAttachments.jsonAttachment(uri.toString(), "Request_uri");
        HttpResponse httpResponse = super.get(uri).execute().returnResponse();
        String response = super.getResponseBody(httpResponse);

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        checkEquals(statusCode, HttpStatus.SC_OK, "Status code");

        String parsedResponse = response.replace("\\\"", "\"");
        sfTemplates = objectMapper.readValue(parsedResponse, new TypeReference<List<SalesforceTemplate>>() {
        });
        return sfTemplates;
    }
}
