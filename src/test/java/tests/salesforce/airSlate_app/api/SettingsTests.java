package tests.salesforce.airSlate_app.api;

import api.salesforce.entities.airslate.PackageSettings;
import api.salesforce.util.HttpResponseUtils;
import com.airslate.api.models.onboarding.Organization;
import com.airslate.api.models.users.User;
import listeners.WebTestListener;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import tests.salesforce.SalesforceAirSlateBaseTest;

import java.io.IOException;
import java.net.URISyntaxException;

import static core.check.Check.checkEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

@Listeners({WebTestListener.class})
public class SettingsTests extends SalesforceAirSlateBaseTest {
    private User user;
    private Organization organization;

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        salesforceAirslateApi.workspace().disconnectWorkspace();
        salesforceAirslateApi.credentials().disconnectUser();
        user = createAirSlateAdmin();
        organization = createOrganization(user);
    }

    @Test(priority = 1)
    public void admin_getPackageSettings_emptySubdomain() {
        HttpResponse settingsResponse = salesforceAirslateApi.settings().getSettings();
        PackageSettings settingsResponseBody = HttpResponseUtils.parseResponse(settingsResponse, PackageSettings.class);
        checkEquals(settingsResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Response Status code");
        assertTrue(settingsResponseBody.isSFAdmin(), "User is Salesforce admin");
        assertNull(settingsResponseBody.getSubdomainUrl(), "No subdomain");
    }

    @Test(priority = 2)
    public void admin_getPackageSettings() {
        connectAdmin(user.email);
        connectWorkspace(organization);
        HttpResponse settingsResponse = salesforceAirslateApi.settings().getSettings();
        PackageSettings settingsResponseBody = HttpResponseUtils.parseResponse(settingsResponse, PackageSettings.class);
        checkEquals(settingsResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Response Status code");
        assertTrue(settingsResponseBody.isSFAdmin(), "User is Salesforce admin");
        assertTrue(settingsResponseBody.getSubdomainUrl().contains(organization.subdomain), "Subdomain");
    }
}
