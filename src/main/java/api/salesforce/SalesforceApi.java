package api.salesforce;

import api.salesforce.entities.auth.AccessToken;
import api.salesforce.requests.Authentication;

public abstract class SalesforceApi {
    protected String orgUserEmail;
    protected String orgUserPassword;
    protected String tokenUrl;
    protected AccessToken accessToken;

    public void auth() {
        if (!isAuthorized()) {
            Authentication auth = new Authentication(tokenUrl, orgUserEmail, orgUserPassword);
            this.accessToken = auth.getMetadataAccessToken();
        }
    }

    public boolean isAuthorized() {
        return accessToken != null;
    }
}
