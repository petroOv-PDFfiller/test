package api.salesforce;

import api.salesforce.entities.auth.AccessToken;
import api.salesforce.requests.airslate.*;
import org.apache.commons.lang3.StringUtils;

public class SalesforceAirslateApi extends SalesforceApi {
    private String packageNamespace;

    public SalesforceAirslateApi(String tokenUrl, String email, String password, String packageNamespace) {
        super.tokenUrl = tokenUrl;
        super.orgUserEmail = email;
        super.orgUserPassword = password;
        if (packageNamespace != null) {
            this.packageNamespace = "/" + packageNamespace;
        } else {
            this.packageNamespace = StringUtils.EMPTY;
        }
    }

    public SalesforceAirslateApi(AccessToken accessToken, String packageNamespace) {
        super.accessToken = accessToken;
        if (packageNamespace != null) {
            this.packageNamespace = "/" + packageNamespace;
        } else {
            this.packageNamespace = StringUtils.EMPTY;
        }
    }

    public Credentials credentials() {
        Credentials credentials = new Credentials(accessToken.instance_url, this.packageNamespace);
        credentials.setAuthorizationHeader(accessToken.token_type, accessToken.access_token);
        return credentials;
    }

    public Workspace workspace() {
        Workspace workspace = new Workspace(accessToken.instance_url, this.packageNamespace);
        workspace.setAuthorizationHeader(accessToken.token_type, accessToken.access_token);
        return workspace;
    }

    public SetupWizard setupWizard() {
        SetupWizard setupWizard = new SetupWizard(accessToken.instance_url, this.packageNamespace);
        setupWizard.setAuthorizationHeader(accessToken.token_type, accessToken.access_token);
        return setupWizard;
    }

    public ButtonConfig buttonConfig() {
        ButtonConfig buttonConfig = new ButtonConfig(accessToken.instance_url, this.packageNamespace);
        buttonConfig.setAuthorizationHeader(accessToken.token_type, accessToken.access_token);
        return buttonConfig;
    }

    public Settings settings() {
        Settings packageSettings = new Settings(accessToken.instance_url, this.packageNamespace);
        packageSettings.setAuthorizationHeader(accessToken.token_type, accessToken.access_token);
        return packageSettings;
    }

    public ObjectView objectsView() {
        ObjectView objectView = new ObjectView(accessToken.instance_url, this.packageNamespace);
        objectView.setAuthorizationHeader(accessToken.token_type, accessToken.access_token);
        return objectView;
    }

    public Survey survey() {
        Survey survey = new Survey(accessToken.instance_url, this.packageNamespace);
        survey.setAuthorizationHeader(accessToken.token_type, accessToken.access_token);
        return survey;
    }

    public Slates slates() {
        Slates slates = new Slates(accessToken.instance_url, this.packageNamespace);
        slates.setAuthorizationHeader(accessToken.token_type, accessToken.access_token);
        return slates;
    }

    public Schedule schedule() {
        Schedule schedule = new Schedule(accessToken.instance_url, this.packageNamespace);
        schedule.setAuthorizationHeader(accessToken.token_type, accessToken.access_token);
        return schedule;
    }
}
