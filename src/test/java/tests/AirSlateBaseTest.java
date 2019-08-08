package tests;

import api.airslate.AirSlateRequests;
import api.airslate.responses.OrganizationResponse;
import api.airslate.responses.UserInfoResp;
import core.TestBase;
import data.airslate.AirSlateTestData.Environments;
import io.qameta.allure.Step;
import org.openqa.selenium.Cookie;
import org.testng.annotations.Optional;
import org.testng.annotations.*;
import pages.airslate.DomainSelectPage;
import pages.airslate.HomePage;
import pages.airslate.LoginPage;
import utils.AirSlateRequestsMan;
import utils.Logger;
import utils.TimeMan;

import java.util.*;
import java.util.function.Consumer;

import static core.check.Check.checkTrue;

public abstract class AirSlateBaseTest extends TestBase {
    protected String orgDomain;
    protected String oAuthURL;
    protected String wsUrl;
    private Environments environment;
    private String initialUrl;
    private UserInfoResp userResp;
    private OrganizationResponse orgResp;

    public UserInfoResp getUserResp() {
        return userResp;
    }

    public void setUserResp(UserInfoResp userResp) {
        this.userResp = userResp;
    }

    public OrganizationResponse getOrgResp() {
        return orgResp;
    }

    public void setOrgResp(OrganizationResponse orgResp) {
        this.orgResp = orgResp;
    }

    @Parameters({"environment", "orgDomain"})
    @BeforeSuite
    protected void beforeAirslateSuite(@Optional("DEV00") Environments environment,
                                       @Optional("my") String orgDomain) {
        setAll(orgDomain, environment);
    }

    @Parameters({"environment", "orgDomain"})
    @BeforeTest
    protected void beforeAirslateTest(@Optional("DEV00") Environments environment,
                                      @Optional("my") String orgDomain) {
        setAll(orgDomain, environment);
    }

    @Parameters({"environment", "orgDomain"})
    @BeforeClass
    protected void beforeAirslateClass(@Optional("DEV00") Environments environment,
                                       @Optional("my") String orgDomain) {
        setAll(orgDomain, environment);
    }

    @Parameters({"environment", "orgDomain"})
    @BeforeMethod
    protected void beforeAirslateMethod(@Optional("DEV00") Environments environment,
                                        @Optional("my") String orgDomain) {
        setAll(orgDomain, environment);
    }

    private void setAll(String orgDomain, Environments environment) {
        setOrgDomain(orgDomain);
        setEnvironment(environment);
        prepareURL(environment);
        setOAuthURL(environment.getDomainOAuth());
    }

    protected void setOAuthURL(String oAuthURL) {
        this.oAuthURL = oAuthURL;
    }

    protected void setWsUrl(String wsUrl) {
        this.wsUrl = wsUrl;
    }

    protected Environments getEnvironment() {
        return environment;
    }

    protected void setEnvironment(Environments environment) {
        this.environment = environment;
    }

    protected void setOrgDomain(String orgDomain) {
        this.orgDomain = orgDomain;
    }

    private void prepareURL(Environments environment) {
        String scheme;
        switch (environment) {
            case PROD:
            case RC:
            case STAGE:
                scheme = SECURED_CONNECTION_SCHEME;
                break;
            default:
                scheme = UNSECURED_CONNECTION_SCHEME;
                break;
        }
        testData.url = scheme + orgDomain + "." + environment.getDomain();
        initialUrl = scheme + "my." + environment.getDomain();
        testData.apiUrl = scheme + environment.getAPIDomain();
        this.oAuthURL = scheme + environment.getDomainOAuth();
        this.wsUrl = environment.getWsUrl();
    }

    protected void changeOrganization(String organization) {
        setOrgDomain(organization);
        prepareURL(environment);
    }

    @Step
    protected String getOrganizationDomainUI() {
        return getDriver().manage().getCookieNamed("airSlate.header.domain").getValue();
    }

    @Step
    public UserInfoResp register(String email, String password) {
        Logger.info("Register user: " + email);
        AirSlateRequests api = new AirSlateRequests(testData.apiUrl);
        UserInfoResp resp = api.qaUsers().register(email, password);
        TimeMan.sleep(2);
        checkTrue(resp != null, "Failed to reg new user");
        return resp;
    }

    public UserInfoResp register(String email) {
        return register(email, testData.password);
    }

    @Step
    public void registerWithCreateOrg(String email, String orgDomain) {
        Logger.info("Registration user: " + email);
        Logger.info("Create organization: " + orgDomain);
        setUserResp(new AirSlateRequests(testData.apiUrl).qaUsers().register(email, testData.password));
        AirSlateRequests api = new AirSlateRequests(testData.apiUrl, email, testData.password);
        setOrgResp(createOrganization(api, orgDomain));
    }

    @Step
    public void registerWithCreateOrg(String email, String orgDomain, String firstName, String lastName, String username) {
        Logger.info("Registration user: " + email);
        Logger.info("Create organization: " + orgDomain);
        userResp = new AirSlateRequests(testData.apiUrl).qaUsers().register(email, testData.password, firstName, lastName, username);
        AirSlateRequests api = new AirSlateRequests(testData.apiUrl, email, testData.password);
        orgResp = createOrganization(api, orgDomain);
    }

    @Step
    public void registerWithInviteToOrg(String email, String ownerEmail, String orgDomain) {
        AirSlateRequests apiOwner = new AirSlateRequests(testData.apiUrl, ownerEmail, testData.password, orgDomain);
        AirSlateRequestsMan requestsMan = new AirSlateRequestsMan(apiOwner);
        String orgId = requestsMan.getOrgIDByDomain(orgDomain);

        String teamMateID = apiOwner.organization().inviteToOrganization(orgId, email).data[0].id;
        String token = apiOwner.qaUsers().getOrganizationToken(orgId, teamMateID).meta.token;
        setOrgResp(apiOwner.organization().inviteConfirm(token, orgId, email, testData.password));
    }

    @Step
    public void joinToOrg(String email, String ownerEmail, String orgDomain) {
        AirSlateRequests api = new AirSlateRequests(testData.apiUrl, email, testData.password);
        String userId = api.users().userInfo().data.id;

        AirSlateRequests apiOwner = new AirSlateRequests(testData.apiUrl, ownerEmail, testData.password, orgDomain);
        AirSlateRequestsMan requestsMan = new AirSlateRequestsMan(apiOwner);
        String orgId = requestsMan.getOrgIDByDomain(orgDomain);

        setOrgResp(apiOwner.organization().getOrganization(orgId));
        api.organizationUsers().addUserToOrganization(userId, orgId);
    }

    @Step
    public void registerWithJoinToOrg(String email, String firstName, String lastName, String username, String ownerEmail, String orgDomain) {
        setUserResp(new AirSlateRequests(testData.apiUrl).qaUsers().register(email, testData.password, firstName, lastName, username));

        AirSlateRequests api = new AirSlateRequests(testData.apiUrl, email, testData.password);
        String userId = userResp.data.id;

        AirSlateRequests apiOwner = new AirSlateRequests(testData.apiUrl, ownerEmail, testData.password, orgDomain);
        AirSlateRequestsMan requestsMan = new AirSlateRequestsMan(apiOwner);
        String orgId = requestsMan.getOrgIDByDomain(orgDomain);

        setOrgResp(apiOwner.organization().getOrganization(orgId));
        api.organizationUsers().addUserToOrganization(userId, orgId);
    }

    @Step
    public void registerWithJoinToOrg(String email, String ownerEmail, String orgDomain) {
        registerWithJoinToOrg(email, "Auto", "Test", "Username" + new Date().getTime(), ownerEmail, orgDomain);
    }

    public String registerJoinToHeadOrg(String email) {
        final String ownerEmail = "pd+join@support.pdffiller.com";
        final String orgDomain = "autotestsheadorganization";
        registerWithJoinToOrg(email, ownerEmail, orgDomain);
        setOrgDomain(orgDomain);
        return orgDomain;
    }

    @Step
    public OrganizationResponse createOrganization(AirSlateRequests api, String orgDomain) {
        OrganizationResponse resp = api.organization().createOrganization(orgDomain);

        checkTrue(resp != null, "Failed to reg new organization");
        changeOrganization(orgDomain);
        return resp;
    }

    @Step("Login [{1}]")
    public HomePage login(String email, String password, String workSpaceName) {
        getUrl(initialUrl + "/login");

        HomePage homePage = new HomePage(getDriver());

        if (homePage.isLogged()) {
            homePage.isOpened();
            return homePage;
        }

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.isOpened();

        if (!workSpaceName.isEmpty()) {
            DomainSelectPage domainSelectPage = loginPage.loginToAirSlate(email, password, true);
            homePage = domainSelectPage.launchWorkspace(workSpaceName);
            changeOrganization(workSpaceName);
        } else {
            DomainSelectPage domainSelectPage = loginPage.loginToAirSlate(email, password, true);
            homePage = domainSelectPage.launchWorkspace("My Workspace");
        }
        return homePage;
    }

    @Step
    public LoginPage goToLoginPage() {
        getUrl(testData.url + "/login");
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.isOpened();
        return loginPage;
    }

    @Step
    public LoginPage logout() {
        getUrl(testData.url + "/logout");
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.isOpened();
        return loginPage;
    }

    public HomePage login(String email, String password) {
        return login(email, password, "");
    }

    @Step
    public AirSlateRequests loginApi(String email) {
        return new AirSlateRequests(testData.apiUrl, email, testData.password);
    }

    @Step
    public AirSlateRequests loginApi(String email, String orgDomain) {
        return new AirSlateRequests(testData.apiUrl, email, testData.password, orgDomain);
    }

    @Step
    public HomePage autoLogin(String email, String password, String organization) {
        Logger.info("AutoLogin for: " + email + " in Org: " + organization);
        changeOrganization(organization);
        AirSlateRequests api = new AirSlateRequests(testData.apiUrl, email, password);
        api.users().patchFeedbackTtl();

        Set<Cookie> cookies = new HashSet<>();
        Consumer<Cookie> consumer = cookie -> getDriver().manage().addCookie(cookie);

        String authToken = api.getAuthToken().meta.access_token;
        String timestamp = String.valueOf(new Date().getTime());
        String domain = "." + environment.getDomain();

        if (organization.equals("my")) {
            organization = getSelfOrgSubdomain(api);
        }
        HomePage homePage = new HomePage(getDriver());

        getDriver().manage().deleteAllCookies();
        getUrl(testData.url);
        checkTrue(homePage.waitUntilPageLoaded(), "Page is not loaded");

        String currentCookieOrg;
        try {
            currentCookieOrg = getDriver().manage().getCookieNamed("airSlate.header.domain").toString();
        } catch (NullPointerException e) {
            currentCookieOrg = organization;
        }

        if (!currentCookieOrg.contains(organization)) {
            getDriver().manage().deleteAllCookies();
            refreshPage();
            checkTrue(homePage.waitUntilPageLoaded(), "Page is not loaded");
        }

        cookies.add(new Cookie("airSlate.header.domain", organization, domain, "/", null));
        cookies.add(new Cookie("airSlate.last_activity", timestamp, domain, "/", null));
        cookies.add(new Cookie("airSlate.session.token", authToken, domain, "/", null));
        cookies.forEach(consumer);

        getUrl(testData.url);
        homePage.isOpened();

        // TODO Perhaps need delete this block.
//        if (!homePage.logged()) {
//            homePage.logout(testData.url);
//            getUrl(testData.url);
//        }
//        getDriver().manage().deleteAllCookies();
//        cookies.forEach(consumer);
        return homePage;
    }

    public HomePage autoLogin(String email, String password) {
        return autoLogin(email, password, orgDomain);
    }

    public List<String> getListOrganizations(String hostUrl, String email, String password) {
        AirSlateRequests airSlateRequests = new AirSlateRequests(hostUrl, email, password);
        return getListOrganizations(airSlateRequests);
    }

    @Step
    public List<String> getListOrganizations(AirSlateRequests api) {
        UserInfoResp.OrgData[] orgData = api.users().userInfo().data.relationships.organizations.data;
        List<String> orgId = new ArrayList<>();

        for (UserInfoResp.OrgData org : orgData) {
            orgId.add(org.id);
        }
        return orgId;
    }

    @Step
    public List<String> getListOrganizationSubdomains(AirSlateRequests api) {
        List<String> orgSubdomains = new ArrayList<>();

        for (String orgId : getListOrganizations(api)) {
            orgSubdomains.add(api.organization().getOrganization(orgId).data.attributes.subdomain);
        }
        return orgSubdomains;
    }

    @Step
    public String getSelfOrgDomainId(AirSlateRequests api) {
        return getListOrganizations(api).get(0);
    }

    @Step
    public String getSelfOrgSubdomain(AirSlateRequests api) {
        return getListOrganizationSubdomains(api).get(0);
    }

    @Step
    public void deleteUserFromOrganization(String email, String ownerEmail, String orgDomain) {
        Logger.info("Delete user [" + email + "] from organization [" + orgDomain + "]");
        AirSlateRequests apiOwner = loginApi(ownerEmail);
        AirSlateRequestsMan airSlateRequestsMan = new AirSlateRequestsMan(apiOwner);
        String orgId = airSlateRequestsMan.getOrgIDByDomain(orgDomain);

        AirSlateRequests apiUser = loginApi(email);
        String userId = apiUser.users().userInfo().data.id;

//        apiOwner.organization().deleteTeamMate(orgId, userId);
        apiUser = loginApi(email);

        apiUser.qaUsers().deleteUser(userId, apiUser.getAuthToken().meta.access_token);
    }
}
