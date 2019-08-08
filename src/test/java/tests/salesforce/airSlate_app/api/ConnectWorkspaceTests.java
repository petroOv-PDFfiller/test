package tests.salesforce.airSlate_app.api;

import api.salesforce.entities.airslate.SalesforceAirslateUser;
import api.salesforce.entities.airslate.WorkspaceConfig;
import api.salesforce.util.HttpResponseUtils;
import com.airslate.api.models.onboarding.Organization;
import com.airslate.api.models.users.User;
import data.TestData;
import io.qameta.allure.Step;
import listeners.WebTestListener;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import tests.salesforce.SalesforceAirSlateBaseTest;

import java.io.IOException;

import static core.check.Check.checkEquals;
import static org.testng.Assert.assertEquals;

@Listeners({WebTestListener.class})
public class ConnectWorkspaceTests extends SalesforceAirSlateBaseTest {
    private Organization organization;

    @BeforeMethod
    public void initAirSlateAppSettings() throws IOException {
        User airSlateAdmin = createAirSlateAdmin();
        organization = createOrganization(airSlateAdmin);
        SalesforceAirslateUser sfAsUser = new SalesforceAirslateUser(airSlateAdmin.email, TestData.defaultPassword, true);
        connectAirSlateAppUser(sfAsUser);
    }

    @Test
    public void admin_connect_ExistingWorkspace() {
        WorkspaceConfig workspace = new WorkspaceConfig(organization.id, organization.subdomain);
        HttpResponse apiResponse = salesforceAirslateApi.workspace().connectWorkspace(workspace);

        WorkspaceConfig workspaceConfig = HttpResponseUtils.parseResponse(apiResponse, WorkspaceConfig.class);
        assertEquals(apiResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Response Status code");
        assertEquals(workspaceConfig.getWorkspaceSubdomain(), organization.subdomain, "Subdomain");
        assertEquals(workspaceConfig.getWorkspaceId(), organization.id, "Workspace ID");
    }

    @Test
    public void admin_getConnectedWorkspaceInfo() {
        connectAirSlateAppWorkspace(new WorkspaceConfig(organization.id, organization.subdomain));

        HttpResponse apiResponse = salesforceAirslateApi.workspace().getWorkspaceConfig();
        WorkspaceConfig workspaceConfig = HttpResponseUtils.parseResponse(apiResponse, WorkspaceConfig.class);

        checkEquals(apiResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Response Status code");
        assertEquals(workspaceConfig.getWorkspaceId(), organization.id, "Workspace Id");
        assertEquals(workspaceConfig.getWorkspaceSubdomain(), organization.subdomain, "Workspace subdomain");
    }

    @Test
    public void admin_disconnect_ExistingWorkspace() {
        connectAirSlateAppWorkspace(new WorkspaceConfig(organization.id, organization.subdomain));

        HttpResponse disconnectWorkspaceResponse = salesforceAirslateApi.workspace().disconnectWorkspace();
        checkEquals(disconnectWorkspaceResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Response Status code");
    }

    @Step("Connecting salesforce airSlate user: {0}")
    private void connectAirSlateAppUser(SalesforceAirslateUser sfUser) {
        HttpResponse authorizeUserResponse = salesforceAirslateApi.credentials().authorizeUser(sfUser);
        checkEquals(authorizeUserResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Response Status code");
    }

    @Step("Connecting airSlate workspace: {0}")
    private void connectAirSlateAppWorkspace(WorkspaceConfig workspaceConfig) {
        HttpResponse connectWorkspace = salesforceAirslateApi.workspace().connectWorkspace(workspaceConfig);
        checkEquals(connectWorkspace.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Response Status code");
    }

    @AfterMethod
    public void clearSfAppSettings() {
        try {
            salesforceAirslateApi.credentials().disconnectUser();
        } finally {
            salesforceAirslateApi.workspace().disconnectWorkspace();
        }
    }
}
