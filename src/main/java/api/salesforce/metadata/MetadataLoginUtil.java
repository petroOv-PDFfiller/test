package api.salesforce.metadata;

import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.partner.LoginResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

import static core.check.Check.checkFail;

public class MetadataLoginUtil {

    public static LoginResult login(String username, String password, String baseUrl, int apiVersion) throws ConnectionException {
        if (baseUrl.lastIndexOf('/') != (baseUrl.length() - 1)) {
            baseUrl += "/";
        }
        baseUrl += "services/Soap/u/" + apiVersion + ".0";
        return loginToSalesforce(username, password, baseUrl);
    }

    public static LoginResult login(String username, String password, String baseUrl) {
        try {
            return login(username, password, baseUrl, 45);
        } catch (ConnectionException e) {
            e.printStackTrace();
            checkFail("Cannot connect to salesforce API" +
                    "\n username: " + username +
                    "\npassword: " + password +
                    "\nURL: " + baseUrl);
        }
        return null;
    }

    public static MetadataConnection createMetadataConnection(
            final LoginResult loginResult) throws ConnectionException {
        final ConnectorConfig config = new ConnectorConfig();
        config.setServiceEndpoint(loginResult.getMetadataServerUrl());
        config.setSessionId(loginResult.getSessionId());
        if (loginResult.getPasswordExpired()) {
            loginResult.setPasswordExpired(false);
        }
        return new MetadataConnection(config);
    }

    private static LoginResult loginToSalesforce(
            final String username,
            final String password,
            final String loginUrl) throws ConnectionException {
        final ConnectorConfig config = new ConnectorConfig();
        config.setAuthEndpoint(loginUrl);
        config.setServiceEndpoint(loginUrl);
        config.setManualLogin(true);
        return (new PartnerConnection(config)).login(username, password);
    }
}