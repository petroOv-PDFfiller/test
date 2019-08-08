package api.salesforce.requests;

import api.salesforce.entities.SalesforceObject;
import api.salesforce.responses.CreateRecordResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.AllureAttachments;
import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.json.simple.JSONObject;
import utils.Logger;
import utils.StringMan;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static core.check.Check.checkEquals;

public class RecordsService extends ApiBase {

    private String basePath;
    private ObjectMapper objectMapper;

    public RecordsService(String instanceUrl) {
        super(instanceUrl);
        this.basePath = "/services/data/v44.0";
        this.objectMapper = new ObjectMapper();
    }

    /**
     * @param sObject    Salesforce object type.For example: Account
     * @param objectData Map with sf object fields.
     * @return createRecordResponse response of create record operation
     */
    @Step("Creating new {0} record")
    public CreateRecordResponse createRecord(SalesforceObject sObject, Map<String, String> objectData) throws URISyntaxException, IOException {
        Logger.info("Creating a record of " + sObject.getAPIName() + " object");
        CreateRecordResponse createRecordResponse;
        String path = basePath + "/sobjects/" + sObject.getAPIName();

        URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path).build();
        Logger.info("Request url: " + uri);
        AllureAttachments.jsonAttachment(uri.toString(), "Request url");

        JSONObject object = new JSONObject();
        object.putAll(objectData);
        HttpResponse httpResponse = post(uri).bodyString(object.toJSONString(), ContentType.APPLICATION_JSON)
                .execute().returnResponse();
        String response = getResponseBody(httpResponse);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        checkEquals(statusCode, HttpStatus.SC_CREATED, "Cannot create Salesforce record.\n Status code");

        String parsedResponse = response.replace("\\\"", "\"");
        createRecordResponse = objectMapper.readValue(parsedResponse, CreateRecordResponse.class);
        return createRecordResponse;
    }

    /**
     * @param sObject  Salesforce object type.For example: Account
     * @param recordId salesforce record id to delete
     */
    @Step("Deleting {0} record with ID: {1}")
    public void deleteRecord(SalesforceObject sObject, String recordId) throws URISyntaxException, IOException {
        Logger.info("Deleting record " + recordId + " of object " + sObject.getAPIName());
        String path = basePath + "/sobjects/" + sObject.getAPIName() + "/" + recordId;

        URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path).build();
        Logger.info("Request url: " + uri);
        AllureAttachments.jsonAttachment(uri.toString(), "Request url");

        HttpResponse httpResponse = delete(uri).execute().returnResponse();
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        checkEquals(statusCode, HttpStatus.SC_NO_CONTENT, "Status code");
    }

    /**
     * @param sObject  Salesforce object type.For example: Account
     * @param recordId salesforce record id to delete
     * @param fields   List of object field names
     * @return Map with record field values
     */
    @Step("Getting {0} record fields with ID: {1}")
    public Map<String, Object> getRecordFields(SalesforceObject sObject, String recordId, List<String> fields) throws URISyntaxException, IOException {
        Logger.info("Getting record " + sObject.getAPIName() + "[" + recordId + "] info");
        String path = basePath + "/sobjects/" + sObject.getAPIName() + "/" + recordId;

        URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path)
                .addParameter("fields", StringUtils.join(fields, ",")).build();
        Logger.info("Request url: " + uri);
        AllureAttachments.jsonAttachment(uri.toString(), "Request url");

        HttpResponse httpResponse = get(uri).execute().returnResponse();
        String response = getResponseBody(httpResponse);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        checkEquals(statusCode, HttpStatus.SC_OK, "Status code");

        return objectMapper.readValue(response.replace("\\\"", "\""), new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * @param sObject      Salesforce object type.For example: Account
     * @param dataToUpdate Map with sf object fields to update value.
     */
    @Step("Updating {0} record with ID: {1}")
    public void updateRecord(SalesforceObject sObject, String recordId, Map<String, String> dataToUpdate) throws URISyntaxException, IOException {
        Logger.info("Updating record " + sObject.getAPIName() + "[" + recordId + "] info");
        String path = basePath + "/sobjects/" + sObject.getAPIName() + "/" + recordId;
        URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path).build();
        Logger.info("Request url: " + uri);
        AllureAttachments.jsonAttachment(uri.toString(), "Request url");

        String bodyJsonData = objectMapper.writeValueAsString(dataToUpdate);
        AllureAttachments.jsonAttachment(bodyJsonData, "Request body json");
        HttpResponse httpResponse = patch(uri).bodyString(bodyJsonData, ContentType.APPLICATION_JSON)
                .execute().returnResponse();

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        checkEquals(statusCode, HttpStatus.SC_NO_CONTENT, "Status code");
    }

    /**
     * @param pathToFile Path to file to be uploaded into Salesforce
     * @param sfFileName File name that will be displayed in Salesforce
     * @param recordId   Record Id for uploading file in
     */
    @Step
    public void uploadFileToRecord(String pathToFile, String sfFileName, String recordId) throws URISyntaxException, IOException {
        Logger.info("Uploading file " + pathToFile + "to record [" + recordId + "]");
        String path = basePath + "/sobjects/Attachment";

        URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path).build();
        Logger.info("Request url: " + uri);
        AllureAttachments.jsonAttachment(uri.toString(), "Request url");

        Map<String, String> body = new HashMap<>();
        body.put("Name", sfFileName);
        body.put("parentId", recordId);
        body.put("Body", StringMan.encodeFileToBase64Binary(pathToFile));

        JSONObject object = new JSONObject();
        object.putAll(body);
        HttpResponse httpResponse = post(uri).bodyString(object.toJSONString(), ContentType.APPLICATION_JSON)
                .execute().returnResponse();
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        checkEquals(statusCode, HttpStatus.SC_CREATED, "Status code");
    }

    /**
     * @param query  SOQL query to be executed
     * @param tClass Class for parsing response
     */
    @Step("Executing SOQL query: {0}")
    public <T> T executeQuery(String query, Class<T> tClass) throws URISyntaxException, IOException {
        Logger.info("Executing SOQL query [" + query + "]");
        String path = basePath + "/query";

        URI uri = new URIBuilder().setScheme(scheme).setHost(host).setPath(path)
                .setParameter("q", query)
                .build();

        Logger.info("Request url: " + uri);
        AllureAttachments.jsonAttachment(uri.toString(), "Request url");

        HttpResponse httpResponse = get(uri).execute().returnResponse();
        String response = getResponseBody(httpResponse);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        checkEquals(statusCode, HttpStatus.SC_OK, "Status code");

        return objectMapper.readValue(response, tClass);
    }
}
