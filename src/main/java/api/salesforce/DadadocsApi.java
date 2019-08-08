package api.salesforce;

import api.salesforce.requests.dadadocs.Documents;
import api.salesforce.requests.dadadocs.Templates;

public class DadadocsApi extends SalesforceApi {
    private String packageNamespace;

    public DadadocsApi(String tokenUrl, String email, String password, String packageNamespace) {
        super.tokenUrl = tokenUrl;
        super.orgUserEmail = email;
        super.orgUserPassword = password;
        this.packageNamespace = packageNamespace;
    }

    public Documents documents() {
        Documents documents = new Documents(accessToken.instance_url, packageNamespace);
        documents.setAuthorizationHeader(accessToken.token_type, accessToken.access_token);
        return documents;
    }

    public Templates templates() {
        Templates templates = new Templates(accessToken.instance_url, packageNamespace);
        templates.setAuthorizationHeader(accessToken.token_type, accessToken.access_token);
        return templates;
    }
}
