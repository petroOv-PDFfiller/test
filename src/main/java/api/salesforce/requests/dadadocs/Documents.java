package api.salesforce.requests.dadadocs;

import api.salesforce.entities.dadadocs.documents.LinkToFillResponse;
import api.salesforce.entities.dadadocs.documents.PreviewResponse;
import api.salesforce.entities.dadadocs.documents.SalesforceDocument;
import api.salesforce.requests.ApiBase;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.AllureAttachments;
import io.qameta.allure.Step;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static core.check.Check.checkEquals;

public class Documents extends ApiBase {
    private String basePath;
    private ObjectMapper objectMapper;

    public Documents(String instanceUrl, String packageNamespace) {
        super(instanceUrl);
        this.basePath = String.format("/services/apexrest/%s/documents", packageNamespace);
        this.objectMapper = new ObjectMapper();
    }

    /**
     * @param recordId salesforce record id
     * @return List of SalesforceDocument entity
     */
    @Step
    public List<SalesforceDocument> getSfDocumentsByRecordId(String recordId) throws URISyntaxException, IOException {
        Logger.info("Getting all uploaded documents");
        List<SalesforceDocument> sfDocumentsAndProjects;
        String path = basePath + "/SfDocumentsAndProjects";
        URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path)
                .setParameter("parentId", recordId).build();

        Logger.info("Request url: " + uri);
        AllureAttachments.jsonAttachment(uri.toString(), "Request_uri");
        HttpResponse httpResponse = super.get(uri).execute().returnResponse();
        String response = super.getResponseBody(httpResponse);

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        checkEquals(statusCode, HttpStatus.SC_OK, "Status code");

        String parsedResponse = response.replace("\\\"", "\"");
        sfDocumentsAndProjects = objectMapper.readValue(parsedResponse, new TypeReference<List<SalesforceDocument>>() {
        });
        return sfDocumentsAndProjects;
    }

    /**
     * @param objectId salesforce document id
     * @return PDFfiller project_id as String
     */
    @Step
    public String upload(String objectId) throws URISyntaxException, IOException {
        Logger.info("Uploading document");
        String path = basePath + "/upload";
        URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path)
                .setParameter("objectId", objectId).build();

        Logger.info("Request url: " + uri);
        AllureAttachments.jsonAttachment(uri.toString(), "Request_uri");
        HttpResponse httpResponse = super.get(uri).execute().returnResponse();
        String response = super.getResponseBody(httpResponse);

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        checkEquals(statusCode, HttpStatus.SC_OK, "Status code");
        return response;
    }

    /**
     * @param projectId  PDFfiller project_id
     * @param accessType access type to document. allowed 1/0
     * @return List of SalesforceDocument entity
     */
    @Step
    public String shareViaLink(String projectId, int accessType) throws URISyntaxException, IOException {
        Logger.info("Getting share link");
        String path = basePath + "/shareViaLink";
        URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path)
                .setParameter("projectId", projectId)
                .setParameter("accessType", String.valueOf(accessType))
                .build();

        Logger.info("Request url: " + uri);
        AllureAttachments.jsonAttachment(uri.toString(), "Request_uri");
        HttpResponse httpResponse = super.get(uri).execute().returnResponse();
        String response = super.getResponseBody(httpResponse);

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        checkEquals(statusCode, HttpStatus.SC_OK, "Status code");
        return response;
    }


    /**
     * @param projectId    pdffiller projectId
     * @param emailTo      target email to send doc
     * @param emailSubject email subject
     * @param emailBody    email body message
     * @return "success" or "error" String
     */
    @Step
    public String sendByEmail(String projectId, String emailTo, String emailSubject, String emailBody)
            throws URISyntaxException, IOException, JSONException {
        Logger.info("Sending document by email");
        String path = basePath + "/emailExport";
        URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path).build();

        String body = new JSONObject()
                .put("projectId", projectId)
                .put("to", emailTo)
                .put("subject", emailSubject)
                .put("message", emailBody)
                .toString();
        StringEntity entity = new StringEntity(body);
        entity.setContentType("application/json");

        Logger.info("Request url: " + uri);
        AllureAttachments.jsonAttachment(uri.toString(), "Request_uri");
        AllureAttachments.jsonAttachment(body, "Request body");
        HttpResponse httpResponse = super.post(uri).body(entity).execute().returnResponse();
        String response = super.getResponseBody(httpResponse);

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        checkEquals(statusCode, HttpStatus.SC_OK, "Status code");
        return response;
    }

    /**
     * @param objectId salesforce document id
     * @param newName  new document name
     * @return "success" or "error" String
     */
    @Step
    public String rename(String newName, String objectId) throws URISyntaxException, IOException {
        Logger.info("Renaming document");
        String path = basePath + "/rename";
        URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path)
                .setParameter("objectId", objectId)
                .setParameter("newName", newName)
                .build();

        Logger.info("Request url: " + uri);
        AllureAttachments.jsonAttachment(uri.toString(), "Request_uri");
        HttpResponse httpResponse = super.get(uri).execute().returnResponse();
        String response = super.getResponseBody(httpResponse);

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        checkEquals(statusCode, HttpStatus.SC_OK, "Status code");
        return response;
    }

    /**
     * @param objectId salesforce document id
     */
    @Step
    public void delete(String objectId) throws URISyntaxException, IOException {
        Logger.info("Deleting document");
        String path = basePath + "/document";
        URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path)
                .setParameter("objectId", objectId)
                .build();

        Logger.info("Request url: " + uri);
        AllureAttachments.jsonAttachment(uri.toString(), "Request_uri");
        HttpResponse httpResponse = super.delete(uri).execute().returnResponse();

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        checkEquals(statusCode, HttpStatus.SC_OK, "Status code");
    }

    /**
     * @param documentId salesforce document id
     * @param parentId   record id which document relates to
     * @return PreviewResponse entity
     */
    @Step
    public PreviewResponse preview(String documentId, String parentId, String itemType, boolean storeAsFile) throws URISyntaxException, IOException {
        Logger.info("Recieving preview url for document");
        PreviewResponse previewResponse;
        String path = basePath + "/preview_url";
        URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path)
                .setParameter("documentId", documentId)
                .setParameter("parentId", parentId)
                .setParameter("itemType", itemType)
                .setParameter("storeAsFile", String.valueOf(storeAsFile))
                .build();

        Logger.info("Request url: " + uri);
        AllureAttachments.jsonAttachment(uri.toString(), "Request_uri");
        HttpResponse httpResponse = super.get(uri).execute().returnResponse();
        String response = super.getResponseBody(httpResponse);

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        checkEquals(statusCode, HttpStatus.SC_OK, "Status code");

        String parsedResponse = response.replace("\\\"", "\"");
        previewResponse = objectMapper.readValue(parsedResponse, PreviewResponse.class);
        return previewResponse;
    }

    /**
     * @param documentId pdffiller project id
     * @param parentId   record id to which document will attached to
     * @return LinkToFillResponse entity
     */
    public LinkToFillResponse getLinkToFill(String documentId, String parentId) throws URISyntaxException, IOException {
        Logger.info("Recieving link to fill");
        String path = basePath + "/l2f_link";
        LinkToFillResponse linkToFillResponse;
        URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path)
                .setParameter("documentId", documentId)
                .setParameter("parentId", parentId)
                .build();

        Logger.info("Request url: " + uri);
        AllureAttachments.jsonAttachment(uri.toString(), "Request_uri");
        HttpResponse httpResponse = super.get(uri).execute().returnResponse();
        String response = super.getResponseBody(httpResponse);

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        checkEquals(statusCode, HttpStatus.SC_OK, "Status code");
        String parsedResponse = response.replace("\\\"", "\"");

        linkToFillResponse = objectMapper.readValue(parsedResponse, LinkToFillResponse.class);
        return linkToFillResponse;
    }
}
