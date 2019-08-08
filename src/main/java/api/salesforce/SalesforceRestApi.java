package api.salesforce;

import api.salesforce.entities.auth.AccessToken;
import api.salesforce.requests.Authentication;
import api.salesforce.requests.RecordsService;
import api.salesforce.requests.Tooling;
import api.salesforce.responses.FindRecordsIDResponse;
import core.AllureAttachments;
import io.qameta.allure.Step;
import lombok.Getter;
import utils.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

public class SalesforceRestApi extends SalesforceApi {

    public static boolean isOrgHasNamespace = false;
    public static boolean isPackageHasNamespace = false;
    @Getter
    private String aSAppNamespace = "pdffiller_sfree";
    @Getter
    private String orgNamespace = null;

    public SalesforceRestApi(String tokenUrl, String email, String password) {
        super.tokenUrl = tokenUrl;
        super.orgUserEmail = email;
        super.orgUserPassword = password;
    }

    public SalesforceRestApi(AccessToken accessToken) {
        super.accessToken = accessToken;
    }

    public RecordsService recordsService() {
        RecordsService recordsService = new RecordsService(accessToken.instance_url);
        recordsService.setAuthorizationHeader(accessToken.token_type, accessToken.access_token);
        return recordsService;
    }

    public Tooling tooling() {
        Tooling tooling = new Tooling(accessToken.instance_url);
        tooling.setAuthorizationHeader(accessToken.token_type, accessToken.access_token);
        return tooling;
    }

    @Step("Get airSlate app Namespace")
    private String getAirSlatePackageNamespace() {
        String soql = "SELECT NamespacePrefix FROM ApexPage WHERE Name = 'airslateApp' LIMIT 1";
        return executeNamespaceSoqlQuery(soql);
    }

    @Step("Get DaDaDocs app Namespace")
    private String getDaDaDocsPackageNamespace() {
        String soql = "SELECT NamespacePrefix FROM ApexPage WHERE Name = 'LightningDaDaDocs' LIMIT 1";
        return executeNamespaceSoqlQuery(soql);
    }

    @Step("Get Organization Namespace")
    private String getOrganizationNamespace() {
        String soql = "SELECT NamespacePrefix FROM Organization LIMIT 1";
        return executeNamespaceSoqlQuery(soql);
    }

    private String executeNamespaceSoqlQuery(String soql) {
        try {
            FindRecordsIDResponse response = recordsService().executeQuery(soql, FindRecordsIDResponse.class);
            if (response.records.length == 0) {
                return null;
            }
            String namespacePrefix = response.records[0].namespacePrefix;
            Logger.info("Actual namespace: " + namespacePrefix + " get by query: " + soql);
            return namespacePrefix;
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            AllureAttachments.jsonAttachment(e.getMessage(), "Error while getting namespace");
        }
        return null;
    }

    @Override
    public void auth() {
        if (!isAuthorized()) {
            Authentication auth = new Authentication(tokenUrl, orgUserEmail, orgUserPassword);
            this.accessToken = auth.getMetadataAccessToken();
        }
        orgNamespace = getOrganizationNamespace();
        isOrgHasNamespace = orgNamespace != null;
        aSAppNamespace = getAirSlatePackageNamespace();
        isPackageHasNamespace = aSAppNamespace != null || getDaDaDocsPackageNamespace() != null;
    }
}
