package tests.salesforce;

import api.salesforce.SalesforceRestApi;
import api.salesforce.entities.SalesforceObject;
import core.AssertionException;
import data.TestData;
import data.salesforce.SalesforceOrg;
import imap.ImapClient;
import io.qameta.allure.Step;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static api.salesforce.entities.SalesforceObject.*;
import static java.util.Arrays.asList;

@Listeners({WebTestListener.class, ImapListener.class})
public class ClearOrgsTest extends SalesforceBaseTest {

    @Test(dataProviderClass = SalesforceOrg.class, dataProvider = "orgList")
    public void clearEmails(SalesforceOrg salesforceOrg) {
        ImapClient imapClient = new ImapClient(salesforceOrg.getAdminUsername(), TestData.defaultPassword);
        imapClient.deleteAllMessages();
        if (salesforceOrg.getStandardUserUsername() != null) {
            imapClient = new ImapClient(salesforceOrg.getStandardUserUsername(), TestData.defaultPassword);
            imapClient.deleteAllMessages();
        }

    }

    @Test(dataProviderClass = SalesforceOrg.class, dataProvider = "orgList")
    public void clearDaDaDocsData(SalesforceOrg salesforceOrg) {
        if (salesforceOrg.getDaDaDocsPackageVersion() != null) {
            salesforceApi = new SalesforceRestApi(baseUrl, salesforceOrg.getAdminUsername(), salesforceOrg.getAdminPassword());
            salesforceApi.auth();
            clearDaDaDocsRecords();
        }
    }

    @Step
    private void clearDaDaDocsRecords() {
        List<SalesforceObject> objects = asList(RELATION_DOCUMENT_ROLES, CONTENT_DOCUMENT, DOCUMENT, FILE_TEMPLATE);
        objects.forEach(object -> {
            String query = "Select id From " + object.getAPIName();
            try {
                clearRecords(object, query);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    @Test(dataProviderClass = SalesforceOrg.class, dataProvider = "orgList")
    public void clearAirSlateData(SalesforceOrg salesforceOrg) {
        if (salesforceOrg.getAirSlatePackageVersion() != null) {
            salesforceApi = new SalesforceRestApi(baseUrl, salesforceOrg.getAdminUsername(), salesforceOrg.getAdminPassword());
            salesforceApi.auth();
            clearAirSlateRecords();
            salesforceApi = new SalesforceRestApi(baseUrl, salesforceOrg.getStandardUserUsername(), salesforceOrg.getStandardUserPassword());
            salesforceApi.auth();
            clearAirSlateRecords();
        }
    }

    @Step
    private void clearAirSlateRecords() {
        List<SalesforceObject> objects = asList(EMAIL_MESSAGE, SLATES_ACTIVITY);
        objects.forEach(object -> {
            String query = "Select id From " + object.getAPIName();
            try {
                clearRecords(object, query);
            } catch (IOException | URISyntaxException | AssertionException e) {
                e.printStackTrace();
            }
        });
    }
}
