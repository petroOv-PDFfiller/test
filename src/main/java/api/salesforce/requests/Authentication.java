package api.salesforce.requests;

import api.salesforce.entities.auth.AccessToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.force.api.ApiConfig;
import com.force.api.ForceApi;
import com.sforce.soap.partner.LoginResult;
import core.AllureAttachments;
import io.qameta.allure.Step;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import utils.Logger;
import utils.StringMan;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static api.salesforce.metadata.MetadataLoginUtil.login;
import static core.check.Check.checkEquals;
import static core.check.Check.checkFail;

public class Authentication extends ApiBase {
    private static final String PROD_CLIENT_ID = "3MVG9CEn_O3jvv0wlu7LcnWgj8ZATi6xpbxOQSYKOlsa8AA2sIlurlVWH5M9mnuctVwsTDSFhON1xgfZTBu8p";
    private static final String PROD_CLIENT_SECRET = "6599599494045424374";
    private static final String GRANT_TYPE = "password";
    private String client_id;
    private String client_secret;
    private String tokenUrl;
    private String username;
    private String password;

    public Authentication(String tokenUrl, String email, String password) {
        super(tokenUrl);
        this.tokenUrl = StringMan.removeSchemeFromUrl(tokenUrl);
        this.client_secret = "";
        this.client_id = "";
        this.username = email;
        this.password = password;
        if (tokenUrl.contains("login.salesforce.com")) {
            this.client_secret = PROD_CLIENT_SECRET;
            this.client_id = PROD_CLIENT_ID;
        }
    }

    @Step
    public AccessToken getAccessToken() throws URISyntaxException, IOException {
        Logger.info("Getting access token");
        AccessToken accessTokenObj;
        URI uri = getURI();

        List<NameValuePair> bodyParameters = new ArrayList<>();
        bodyParameters.add(new BasicNameValuePair("grant_type", GRANT_TYPE));
        bodyParameters.add(new BasicNameValuePair("client_id", client_id));
        bodyParameters.add(new BasicNameValuePair("client_secret", client_secret));
        bodyParameters.add(new BasicNameValuePair("username", username));
        bodyParameters.add(new BasicNameValuePair("password", password));

        Logger.info("Request url: " + uri);
        AllureAttachments.jsonAttachment(uri.toString(), "Request_uri");
        AllureAttachments.textAttachment(StringMan.nameValuePairPrettyPrint(bodyParameters), "Request body");

        HttpResponse httpResponse = super.post(uri)
                .bodyForm(bodyParameters)
                .execute().returnResponse();
        String response = getResponseBody(httpResponse);

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        checkEquals(statusCode, HttpStatus.SC_OK, "Status code");

        accessTokenObj = new ObjectMapper().readValue(response, AccessToken.class);
        Logger.info("Access token: " + accessTokenObj.access_token);

        return accessTokenObj;
    }

    @Step
    public AccessToken getForceAccessToken() {
        ForceApi api = new ForceApi(new ApiConfig()
                .setUsername(username)
                .setPassword(password)
                .setForceURL(getURI().toString()));
        return new AccessToken(api.getSession().getAccessToken(), api.getSession().getApiEndpoint(), "Bearer");
    }

    @Step
    public AccessToken getMetadataAccessToken() {
        try {
            LoginResult loginResult = login(username, password, Objects.requireNonNull(getURI()).toString());
            return new AccessToken(loginResult.getSessionId(), loginResult.getMetadataServerUrl().substring(0,
                    loginResult.getMetadataServerUrl().indexOf("/services/Soap/")), "Bearer");
        } catch (Exception e) {
            e.printStackTrace();
            checkFail("cannot connect to API");
        }
        return null;
    }

    private URI getURI() {
        try {
            return new URIBuilder().setScheme(scheme).setHost(tokenUrl).build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
