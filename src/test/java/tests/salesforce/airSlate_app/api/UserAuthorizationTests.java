package tests.salesforce.airSlate_app.api;

import api.salesforce.SalesforceAirslateApi;
import api.salesforce.entities.airslate.*;
import api.salesforce.util.HttpResponseUtils;
import com.airslate.api.models.users.User;
import data.TestData;
import listeners.WebTestListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.testng.annotations.*;
import tests.salesforce.SalesforceAirSlateBaseTest;

import java.io.IOException;
import java.net.URISyntaxException;

import static core.check.Check.checkEquals;
import static org.testng.Assert.assertEquals;

@Listeners({WebTestListener.class})
public class UserAuthorizationTests extends SalesforceAirSlateBaseTest {
    private User airSlateAdmin;

    @Parameters({"stUserUsername", "stUserPassword"})
    @BeforeTest
    public void setUp(@Optional("pdf_sf_aqa+as+guest@support.pdffiller.com") String stUserUsername,
                      @Optional("Likas_qwe1rty3") String stUserPassword) throws IOException, URISyntaxException {
        this.stUserUsername = stUserUsername;
        this.stUserPassword = stUserPassword;
    }

    @BeforeMethod
    public void createNewAirSlateUser() {
        airSlateAdmin = createAirSlateAdmin();
    }

    @Test
    public void admin_loginWith_validUserCredentials() {
        SalesforceAirslateUser sfAsUser = new SalesforceAirslateUser(airSlateAdmin.email, TestData.defaultPassword, true);
        try {
            HttpResponse authorizeUserResponse = salesforceAirslateApi.credentials().authorizeUser(sfAsUser);
            checkEquals(authorizeUserResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Response Status code");
        } finally {
            salesforceAirslateApi.credentials().disconnectUser();
        }
    }

    @Test
    public void admin_getConnectedUserCredentials() {
        try {
            connectAdmin(airSlateAdmin.email);

            HttpResponse credentialsResponse = salesforceAirslateApi.credentials().getUserCredentials();
            checkEquals(credentialsResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Response Status code");
            Credentials credentialsResponseBody = HttpResponseUtils.parseResponse(credentialsResponse, Credentials.class);
            assertEquals(credentialsResponseBody.getLogin(), airSlateAdmin.email, "Connected user");
        } finally {
            salesforceAirslateApi.credentials().disconnectUser();
        }
    }

    @Test
    public void admin_loginWith_invalidUserEmail() {
        SalesforceAirslateUser sfAsUser = new SalesforceAirslateUser("invalidemail", "somePassword", true);
        HttpResponse authorizeUserResponse = salesforceAirslateApi.credentials().authorizeUser(sfAsUser);
        checkEquals(authorizeUserResponse.getStatusLine().getStatusCode(), HttpStatus.SC_BAD_REQUEST, "Response Status code");
        ErrorResponse authorizeUserResponseBody = HttpResponseUtils.parseResponse(authorizeUserResponse, ErrorResponse.class);
        assertEquals(authorizeUserResponseBody.getErrorType(), ResponseBody.ErrorType.INVALID_EMAIL, "INVALID_EMAIL");
    }

    @Test
    public void admin_loginWith_invalidUserPassword() {
        SalesforceAirslateUser sfAsUser = new SalesforceAirslateUser(airSlateAdmin.email, "invalidpassword", true);
        HttpResponse authorizeUserResponse = salesforceAirslateApi.credentials().authorizeUser(sfAsUser);
        checkEquals(authorizeUserResponse.getStatusLine().getStatusCode(), HttpStatus.SC_BAD_REQUEST, "Response Status code");
        ErrorResponse authorizeUserResponseBody = HttpResponseUtils.parseResponse(authorizeUserResponse, ErrorResponse.class);
        assertEquals(authorizeUserResponseBody.getErrorType(), ResponseBody.ErrorType.INVALID_CREDENTIALS, "INVALID_CREDENTIALS");
    }

    @Test
    public void admin_loginWith_emptyEmailAndPassword() {
        SalesforceAirslateUser sfAsUser = new SalesforceAirslateUser(StringUtils.EMPTY, StringUtils.EMPTY, true);
        HttpResponse authorizeUserResponse = salesforceAirslateApi.credentials().authorizeUser(sfAsUser);
        checkEquals(authorizeUserResponse.getStatusLine().getStatusCode(), HttpStatus.SC_BAD_REQUEST, "Response Status code");
        ErrorResponse authorizeUserResponseBody = HttpResponseUtils.parseResponse(authorizeUserResponse, ErrorResponse.class);
        assertEquals(authorizeUserResponseBody.getErrorType(), ResponseBody.ErrorType.BAD_REQUEST, "BAD_REQUEST");
    }

    @Test
    public void admin_validUserAuthorization_isAdmin_false() {
        SalesforceAirslateUser sfAsUser = new SalesforceAirslateUser(airSlateAdmin.email, TestData.defaultPassword, false);
        WorkspaceConfig workspaceConfig = new WorkspaceConfig("SOMEID", "SOMESUBDOMAIN");
        try {
            HttpResponse authorizeUserResponse = salesforceAirslateApi.credentials().authorizeUser(sfAsUser);
            checkEquals(authorizeUserResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Response Status code");

            HttpResponse workspaceResponse = salesforceAirslateApi.workspace().connectWorkspace(workspaceConfig);
            checkEquals(workspaceResponse.getStatusLine().getStatusCode(), HttpStatus.SC_BAD_REQUEST, "Response Status code");
            ErrorResponse connectWorkspaceResponseBody = HttpResponseUtils.parseResponse(workspaceResponse, ErrorResponse.class);
            assertEquals(connectWorkspaceResponseBody.getErrorType(), ResponseBody.ErrorType.NOT_ADMIN, "NOT_ADMIN");
        } finally {
            salesforceAirslateApi.credentials().disconnectUser();
        }
    }

    @Test
    public void standardUser_loginWith_duplicateLogin() {
        User airSlateUser = createAirSlateUser();
        SalesforceAirslateUser sfAirSlateUser = new SalesforceAirslateUser(airSlateUser.email, TestData.defaultPassword, true);
        salesforceAirslateApi.credentials().authorizeUser(sfAirSlateUser);
        SalesforceAirslateApi standardUserApi = new SalesforceAirslateApi(baseUrl, stUserUsername, stUserPassword, salesforceApi.getASAppNamespace());
        try {
            standardUserApi.auth();
            HttpResponse authorizeUserResponse = standardUserApi.credentials().authorizeUser(sfAirSlateUser);
            checkEquals(authorizeUserResponse.getStatusLine().getStatusCode(), HttpStatus.SC_BAD_REQUEST, "Response Status code");
            ErrorResponse authorizeUserResponseBody = HttpResponseUtils.parseResponse(authorizeUserResponse, ErrorResponse.class);
            assertEquals(authorizeUserResponseBody.getErrorType(), ResponseBody.ErrorType.DUPLICATE_LOGIN, "DUPLICATE_LOGIN");
        } finally {
            try {
                salesforceAirslateApi.credentials().disconnectUser();
            } finally {
                standardUserApi.credentials().disconnectUser();
            }
        }
    }

    @Test
    public void standardUser_loginWith_validCredentials() {
        SalesforceAirslateUser sfAirSlateUser = new SalesforceAirslateUser(airSlateAdmin.email, TestData.defaultPassword, false);
        SalesforceAirslateApi standardUserApi = new SalesforceAirslateApi(baseUrl, stUserUsername, stUserPassword, salesforceApi.getASAppNamespace());
        try {
            standardUserApi.auth();
            HttpResponse authorizeUserResponse = standardUserApi.credentials().authorizeUser(sfAirSlateUser);
            assertEquals(authorizeUserResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK, "Response Status code");
        } finally {
            standardUserApi.credentials().disconnectUser();
        }
    }
}
