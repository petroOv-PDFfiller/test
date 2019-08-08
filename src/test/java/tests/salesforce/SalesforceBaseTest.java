package tests.salesforce;

import api.salesforce.SalesforceRestApi;
import api.salesforce.entities.SalesforceObject;
import api.salesforce.entities.auth.AccessToken;
import api.salesforce.metadata.MetadataApiMan;
import base_tests.PDFfillerTest;
import com.sforce.soap.partner.LoginResult;
import data.airslate.AirSlateTestData;
import io.qameta.allure.Step;
import listeners.WebTestListener;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;
import org.testng.annotations.*;
import pages.salesforce.app.SalesAppBasePage;
import utils.Logger;
import utils.salesforce.SalesforceMan;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;

import static api.salesforce.entities.SalesforceObject.*;
import static api.salesforce.metadata.MetadataLoginUtil.login;
import static com.codeborne.selenide.WebDriverRunner.url;
import static core.check.Check.checkTrue;

@Listeners({WebTestListener.class})
public abstract class SalesforceBaseTest extends PDFfillerTest {

    final SalesforceMan salesforceMan = new SalesforceMan();
    protected String baseUrl = "https://login.salesforce.com/";
    protected MetadataApiMan metadataApi;
    protected String salesforceDefaultLogin;
    protected String salesforceDefaultPassword;
    protected SalesforceRestApi salesforceApi;
    protected String dxOwnerEmail;
    protected String stUserUsername;
    protected String stUserPassword;
    String accessToken;
    String instanceUrl;

    @Parameters({"email", "testPassword", "dxOwnerEmail", "stUserUsername", "stUserPassword", "useApi"})
    @BeforeTest
    public void setUpCredentials(@Optional("pdf_sf_aqa+as@support.pdffiller.com") String email,
                                 @Optional("qwe1rty2") String testPassword,
                                 @Optional("") String dxOwnerEmail,
                                 @Optional("pdf_sf_aqa+as+guest@support.pdffiller.com") String stUserUsername,
                                 @Optional("Likas_qwe1rty3") String stUserPassword,
                                 @Optional("true") boolean useApi) throws IOException, URISyntaxException {
        salesforceDefaultLogin = email;
        salesforceDefaultPassword = testPassword;
        this.dxOwnerEmail = dxOwnerEmail;
        this.stUserUsername = stUserUsername;
        this.stUserPassword = stUserPassword;
        if (isDXOrg()) {
            baseUrl = baseUrl.replace("login.", "test.");
        }
        if (useApi) {
            initApi();
        }
    }

    private void getAccessToken() {
        LoginResult loginResult = login(salesforceDefaultLogin, salesforceDefaultPassword, baseUrl);
        this.instanceUrl = Objects.requireNonNull(loginResult).getMetadataServerUrl().substring(0,
                loginResult.getMetadataServerUrl().indexOf("/services/Soap/"));
        this.accessToken = loginResult.getSessionId();
        metadataApi = new MetadataApiMan(loginResult);

    }

    protected void initApi() throws IOException, URISyntaxException {
        getAccessToken();
        salesforceApi = new SalesforceRestApi(new AccessToken(accessToken, instanceUrl, "Bearer"));
        salesforceApi.auth();
    }

    public SalesAppBasePage loginWithDefaultCredentials() {
        return loginToSalesforce(salesforceDefaultLogin, salesforceDefaultPassword);
    }

    private SalesAppBasePage loginToSalesforce(String email, String password) {
        return loginToSalesforce(email, password, getDriver());
    }

    protected SalesAppBasePage loginToSalesforce(String email, String password, WebDriver driver) {
        salesforceMan.loginToSalesforce(email, password, driver, baseUrl, isDXOrg(), dxOwnerEmail);
        SalesAppBasePage salesAppBasePage = new SalesAppBasePage(driver);
        salesAppBasePage.isOpened();
        return salesAppBasePage;
    }

    @Step("Refreshing page")
    protected void refreshPage() {
        Logger.info("Refreshing the page");
        getDriver().navigate().refresh();
        checkTrue(new SalesAppBasePage(getDriver()).waitUntilPageLoaded(), "Page is not loaded after refresh");
    }

    @Override
    public void getUrl(String url) {
        if (url().equals(url)) {
            refreshPage();
        } else {
            super.getUrl(url);
            checkTrue(new SalesAppBasePage(getDriver()).waitUntilPageLoaded(), url + " Url is not loaded");
        }
    }

    @Step
    @AfterSuite
    public void clearTestRecords() {
        try {
            initApi();
            salesforceMan.clearRecords(salesforceApi, ACCOUNT);
            salesforceMan.clearRecords(salesforceApi, OPPORTUNITY);
            salesforceMan.clearRecords(salesforceApi, CONTACT);
        } catch (Exception e) {
            Logger.error("Cannot clear records");
        }
    }

    protected void clearRecords(SalesforceObject objectType) throws IOException, URISyntaxException {
        salesforceMan.clearRecords(salesforceApi, objectType);
    }

    void clearRecords(SalesforceObject type, String query) throws IOException, URISyntaxException {
        salesforceMan.clearRecords(salesforceApi, type, query);
    }

    @Step("Setting UTC time zone for {0}")
    protected void setUserUTCTimeZone(String username) throws IOException, URISyntaxException {
        salesforceMan.setUserUTCTimeZone(salesforceApi, username);
    }

    String getAirSlateApiURL(AirSlateTestData.Environments environment) {
        return getAirSlateScheme(environment) + environment.getAPIDomain();
    }

    String getAirSlateScheme(AirSlateTestData.Environments environment) {
        switch (environment) {
            case PROD:
            case RC:
            case STAGE:
                return SECURED_CONNECTION_SCHEME;
            default:
                return UNSECURED_CONNECTION_SCHEME;
        }
    }

    protected String getUniqueRecordName(SalesforceObject object) {
        return salesforceMan.getUniqueRecordName(object);
    }

    boolean isDXOrg() {
        return !(dxOwnerEmail == null || dxOwnerEmail.isEmpty());
    }

    protected String createAccountRecord() {
        return salesforceMan.createAccountRecord(salesforceApi);
    }

    @Step("Creating new Account record")
    protected String createAccountRecord(final Map<String, String> params) {
        return salesforceMan.createAccountRecord(salesforceApi, params);
    }

    @Step("Creating {0} Account records")
    protected void createAccountRecordsBulk(int recordsCount) {
        salesforceMan.createAccountRecordsBulk(salesforceApi, recordsCount);
    }

    @Step
    protected String createContactObject(String contactFirstName, String contactLastName) throws URISyntaxException, IOException {
        return salesforceMan.createContactObject(salesforceApi, contactFirstName, contactLastName);
    }

    AirSlateTestData.Environments getEnvironment() {
        try {
            return AirSlateTestData.Environments.valueOf(Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("environment"));
        } catch (SecurityException | NullPointerException | IllegalArgumentException e) {
            return AirSlateTestData.Environments.PROD;
        }
    }
}