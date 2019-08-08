package api.salesforce.requests;

import api.HttpBase;
import core.AllureAttachments;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import utils.Logger;
import utils.StringMan;

import java.io.IOException;

public abstract class ApiBase extends HttpBase {
    public ApiBase(String instanceUrl) {
        super(instanceUrl);
    }

    public void setAuthorizationHeader(String tokenType, String token) {
        setHeader("Authorization", tokenType + " " + token);
    }

    @Override
    protected String getResponseBody(HttpResponse resp) throws ParseException, IOException {
        String respBody = EntityUtils.toString(resp.getEntity());
        String response;
        if (respBody.startsWith("\"") && respBody.endsWith("\"")) {
            response = respBody.substring(1, respBody.length() - 1);
        } else {
            response = respBody;
        }
        Logger.info("RESPONSE:\n" + StringMan.jsonPrettyPrint(response));
        AllureAttachments.jsonAttachment(StringMan.jsonPrettyPrint(response), "Response");
        return response;
    }
}
