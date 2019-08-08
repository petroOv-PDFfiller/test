package api.salesforce.util;

import api.salesforce.entities.airslate.ResponseBody;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.AllureAttachments;
import io.qameta.allure.Step;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import utils.JsonMan;
import utils.Logger;
import utils.StringMan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpResponseUtils {
    private static ObjectMapper mapper = JsonMan.getMapperInstance();

    @Step("Extract response")
    public static <T> T parseResponse(HttpResponse response, Class<T> clazz) {
        String responseAsString = printResponse(response);
        try {
            return JsonMan.convertJsonToPojo(responseAsString, clazz);
        } catch (Exception e) {
            attachErrorMessage(responseAsString, e);
            throw new AssertionError("Could not parse response");
        }
    }

    @Step("Extract response")
    public static <T> List<T> parseResponseAsList(HttpResponse response, Class<T> innerCollectionType) {
        String responseAsString = printResponse(response);
        try {
            JavaType collection = mapper.getTypeFactory().constructCollectionType(List.class, innerCollectionType);
            return JsonMan.convertJsonToPojo(responseAsString, collection);
        } catch (Exception e) {
            attachErrorMessage(responseAsString, e);
            throw new AssertionError("Could not parse response");
        }
    }

    @Step("Extract response")
    public static <T> ResponseBody<T> parseResponseWithCollection(HttpResponse response, Class innerCollectionType) {
        String responseAsString = printResponse(response);
        try {
            JavaType collection = mapper.getTypeFactory().constructCollectionType(ArrayList.class, innerCollectionType);
            JavaType type = mapper.getTypeFactory().constructParametricType(ResponseBody.class, collection);
            return JsonMan.convertJsonToPojo(responseAsString, type);
        } catch (Exception e) {
            attachErrorMessage(responseAsString, e);
            throw new AssertionError("Could not parse response");
        }
    }

    private static String getResponseAsString(HttpResponse response) {
        if (response == null) {
            throw new AssertionError("Response should not be null");
        }
        String respBody = null;
        try {
            respBody = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String responseAsString;
        if (respBody.startsWith("\"") && respBody.endsWith("\"")) {
            responseAsString = respBody.substring(1, respBody.length() - 1);
        } else {
            responseAsString = respBody;
        }
        return responseAsString;
    }

    private static String printResponse(HttpResponse response) {
        String actualStatusLine = response.getStatusLine().toString();
        AllureAttachments.jsonAttachment(actualStatusLine, "Status line");
        String responseAsString = getResponseAsString(response);
        Logger.info("Response body: " + responseAsString);
        AllureAttachments.jsonAttachment(StringMan.jsonPrettyPrint(responseAsString), "Response");
        return responseAsString;
    }

    private static void attachErrorMessage(String responseAsString, Exception exception) {
        Logger.info("Response body: " + responseAsString);
        Logger.error(exception.getMessage());
        AllureAttachments.jsonAttachment(StringMan.jsonPrettyPrint(responseAsString), "Response");
    }
}
