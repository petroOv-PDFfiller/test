package utils.salesforce;

import api.salesforce.SalesforceRestApi;
import api.salesforce.entities.SalesforceObject;
import api.salesforce.metadata.MetadataApiMan;
import api.salesforce.responses.CreateRecordResponse;
import api.salesforce.responses.ExecuteApexResponse;
import api.salesforce.responses.FindRecordsIDResponse;
import com.codeborne.selenide.WebDriverRunner;
import com.google.common.collect.ImmutableMap;
import com.sforce.soap.metadata.FilterScope;
import com.sforce.soap.metadata.ListView;
import com.sforce.soap.metadata.Metadata;
import com.sforce.soap.metadata.UpsertResult;
import com.sforce.ws.ConnectionException;
import core.check.Check;
import data.TestData;
import data.salesforce.SalesforceTestData;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import pages.salesforce.app.*;
import utils.ImapMan;
import utils.Logger;
import utils.StringMan;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static api.salesforce.entities.SalesforceObject.*;
import static core.check.Check.checkFail;
import static core.check.Check.checkTrue;

public class SalesforceMan {

    public void loginToSalesforce(String email, String password, WebDriver driver, String loginUrl, boolean isDxOrg, String dxOwnerEmail) {
        driver.get(loginUrl);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.isOpened();
        loginPage.loginToSalesforce(email, password);

        if (WebDriverRunner.url().contains("EmailVerificationFinishUi")) {
            if (isDxOrg) {
                verifyEmail(driver, dxOwnerEmail);
            } else {
                verifyEmail(driver, email);
            }
        } else if (WebDriverRunner.url().contains("AddPhoneNumber")) {
            verifyMobile(driver);
        } else if (WebDriverRunner.url().contains("ChangePassword")) {
            cancelPasswordChange(driver);
        } else if (WebDriverRunner.url().contains("RemoteAccessAuthorizationPage")) {
            allowAccess(driver);
        }
    }

    @Step("Setting airSlate environment for App")
    public void setAirSlateEnvironmentURL(SalesforceRestApi salesforceApi, String airSlateApiUrl) {
        if (salesforceApi != null) {
            ExecuteApexResponse apexResponse;
            String airSlateSettings = "AirSlateSettings__c";
            String apiUrl__c = "API_URL__c";
            String namespace = salesforceApi.getASAppNamespace();

            if (namespace != null) {
                airSlateSettings = namespace + "__" + airSlateSettings;
                apiUrl__c = namespace + "__" + apiUrl__c;
            }
            String apex = String.format("%s settings = [SELECT Id FROM %s LIMIT 1];" +
                    "settings.%s = '%s';" +
                    "update settings;", airSlateSettings, airSlateSettings, apiUrl__c, airSlateApiUrl);
            try {
                apexResponse = salesforceApi.tooling().executeApex(apex);
                checkTrue(apexResponse.isSuccess(), "Cannot set airSlate url");
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
                checkFail("Error: cannot set airSlate url");
            }
        }
    }

    @Step("GET User record id for {1}")
    public String getUserId(SalesforceRestApi salesforceApi, String username) {
        String userQuery = "SELECT id FROM User WHERE Username = '" + username + "'";
        FindRecordsIDResponse response = null;
        try {
            response = salesforceApi.recordsService().executeQuery(userQuery, FindRecordsIDResponse.class);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        if (response == null || response.totalSize < 1) {
            checkFail("User is not present");
        }
        return response.records[0].Id;
    }

    @Step
    public String addListViewTOObject(SalesforceObject object, MetadataApiMan metadataApiMan) throws ConnectionException {
        ListView listView = new ListView();
        String name = "ListView";
        listView.setFilterScope(FilterScope.Everything);
        listView.setLabel(name);
        listView.setFullName(object.getAPIName() + "." + name);
        UpsertResult[] upsertResult = metadataApiMan.updateMetadata(new Metadata[]{listView});
        checkTrue(upsertResult[0].isSuccess(), "Can not add list viee to " + object.getObjectName());
        return name;
    }

    @Step("Creating Opportunity record")
    public String createOpportunityRecord(SalesforceRestApi salesforceRestApi) throws URISyntaxException, IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, String> opportunityParameters = new HashMap<>();
        opportunityParameters.put("Name", new SalesforceMan().getUniqueRecordName(OPPORTUNITY));
        opportunityParameters.put("StageName", SalesforceTestData.SalesforceOpportunityStages.PROSPECTING);
        opportunityParameters.put("CloseDate", dateFormat.format(new Date()));
        return salesforceRestApi.recordsService().createRecord(OPPORTUNITY, opportunityParameters).id;
    }


    @Step
    private void verifyEmail(WebDriver driver, String orgOwnerEmail) {
        try {
            Logger.info("Verification code is requested");
            EmailVerifyPage emailVerifyPage = new EmailVerifyPage(driver);
            emailVerifyPage.isOpened();
            String code = ImapMan.getSalesforceEmailVerifyCode(orgOwnerEmail, TestData.defaultPassword);
            emailVerifyPage.verify(code);
        } finally {
            Check.checkTrue(ImapMan.deleteValidationMessages(orgOwnerEmail, TestData.defaultPassword), "Mailbox is not cleaned");
        }
    }

    @Step
    private void allowAccess(WebDriver driver) {
        Logger.info("Allow remote access");
        AllowRemoteAccessPage accessPage = new AllowRemoteAccessPage(driver);
        accessPage.isOpened();
        accessPage.allowAccess();
    }

    @Step
    private void verifyMobile(WebDriver driver) {
        Logger.info("Phone registration page is opened");
        PhoneVerifyPage phoneVerifyPage = new PhoneVerifyPage(driver);
        phoneVerifyPage.isOpened();
        phoneVerifyPage.declineVerification();
    }

    @Step
    private void cancelPasswordChange(WebDriver driver) {
        Logger.info("Change password page is opened");
        ChangePasswordPage passwordPage = new ChangePasswordPage(driver);
        passwordPage.isOpened();
        passwordPage.cancelPasswordChange();
    }

    public void clearRecords(SalesforceRestApi salesforceRestApi, SalesforceObject objectType) throws IOException, URISyntaxException {
        String SELECT_ID_FROM = "SELECT id FROM ";
        String WHERE_QUERY_PART = " Where name LIKE '";
        if (objectType.equals(CONTACT)) {
            WHERE_QUERY_PART = WHERE_QUERY_PART.replace("name", "LastName");
        }
        String query = SELECT_ID_FROM + objectType.getAPIName() + WHERE_QUERY_PART + objectType.getUniquePrefix() + "%'";
        clearRecords(salesforceRestApi, objectType, query);
    }

    public void clearRecords(SalesforceRestApi salesforceRestApi, SalesforceObject type, String query) throws IOException, URISyntaxException {
        if (!salesforceRestApi.isAuthorized()) {
            salesforceRestApi.auth();
        }
        FindRecordsIDResponse findRecordsIDResponse = salesforceRestApi.recordsService().
                executeQuery(query, FindRecordsIDResponse.class);
        for (int i = 0; i < findRecordsIDResponse.totalSize; i++) {
            String id = findRecordsIDResponse.records[i].Id;
            salesforceRestApi.recordsService().deleteRecord(type, id);
        }
    }

    @Step("Setting UTC time zone for {1}")
    public void setUserUTCTimeZone(SalesforceRestApi salesforceRestApi, String username) throws IOException, URISyntaxException {
        String query = String.format("SELECT id FROM %s Where Username = '%s'", USER.getAPIName(), username);

        FindRecordsIDResponse findRecordsIDResponse = salesforceRestApi.recordsService().
                executeQuery(query, FindRecordsIDResponse.class);
        System.out.println(findRecordsIDResponse.totalSize);
        if (findRecordsIDResponse.records.length > 0) {
            salesforceRestApi.recordsService()
                    .updateRecord(USER,
                            findRecordsIDResponse.records[0].Id,
                            ImmutableMap.of("TimeZoneSidKey", "GMT", "LocaleSidKey", "en_GB"));
        } else {
            Logger.error(username + " user is not found");
        }
    }

    public String getUniqueRecordName(SalesforceObject object) {
        return object.getUniquePrefix() + StringMan.getRandomString(8);
    }

    public String createAccountRecord(SalesforceRestApi salesforceRestApi) {
        return createAccountRecord(salesforceRestApi, null);
    }

    @Step("Creating new Account record")
    public String createAccountRecord(SalesforceRestApi salesforceRestApi, final Map<String, String> params) {
        Map<String, String> accountParams = new HashMap<>();
        if (params != null) {
            accountParams.putAll(params);
        }
        accountParams.putAll(ImmutableMap.of("Name", getUniqueRecordName(ACCOUNT)));
        CreateRecordResponse response;
        try {
            response = salesforceRestApi.recordsService().createRecord(ACCOUNT, accountParams);
            return response.id;
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Step
    public String createContactObject(SalesforceRestApi salesforceRestApi, String contactFirstName, String contactLastName) throws URISyntaxException, IOException {
        CreateRecordResponse response = salesforceRestApi.recordsService().createRecord(CONTACT, ImmutableMap.of("LastName", contactLastName, "FirstName", contactFirstName));
        return response.id;
    }

    @Step
    public void createAccountRecordsBulk(SalesforceRestApi salesforceRestApi, int recordsCount) {
        String apexCode = "List<Account> recordsList = new List<Account>();" +
                "for(Integer i= 0; i <" + recordsCount + "; i++)" +
                "{recordsList.add(new Account(Name='Account " + StringMan.getRandomString(5) + "'));}" +
                "insert recordsList;";
        try {
            salesforceRestApi.tooling().executeApex(apexCode);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}