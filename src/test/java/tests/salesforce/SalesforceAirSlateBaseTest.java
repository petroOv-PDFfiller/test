package tests.salesforce;

import api.salesforce.SalesforceAirslateApi;
import api.salesforce.entities.SalesforceObject;
import api.salesforce.entities.airslate.CustomButton;
import api.salesforce.entities.airslate.ScheduledFlow;
import api.salesforce.entities.airslate.SetupWizardStage;
import api.salesforce.entities.auth.AccessToken;
import com.airslate.api.AirslateRestClient;
import com.airslate.api.models.directInvite.OrganizationUser;
import com.airslate.api.models.documents.Document;
import com.airslate.api.models.files.BaseFile;
import com.airslate.api.models.onboarding.Organization;
import com.airslate.api.models.packets.Packet;
import com.airslate.api.models.packets.PacketRevision;
import com.airslate.api.models.slates.Slate;
import com.airslate.api.models.users.User;
import com.sforce.ws.ConnectionException;
import data.airslate.AirSlateTestData;
import io.qameta.allure.Step;
import listeners.BrowserLogsListener;
import listeners.ImapListener;
import listeners.WebTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Listeners;
import pages.salesforce.app.SalesAppBasePage;
import pages.salesforce.app.airSlate_app.admin_tools.ASAppAdminToolsPage;
import utils.airslate.AirSlateApiMan;
import utils.salesforce.SalesforceAirSlateMan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import static utils.StringMan.getRandomString;

@Listeners({WebTestListener.class, ImapListener.class, BrowserLogsListener.class})
public abstract class SalesforceAirSlateBaseTest extends SalesforceBaseTest {

    protected final static String defaultAirSlateUserEmail = "pdf_sf_rnt+as@support.pdffiller.com";
    protected static File fileToUpload = new File("src/test/resources/uploaders/Constructor.pdf");
    private static ThreadLocal<CustomButton> CUSTOM_BUTTON = new ThreadLocal<>();
    private static ThreadLocal<User> USER = new ThreadLocal<>();
    private static ThreadLocal<OrganizationUser> ORG_USER = new ThreadLocal<>();
    private static ThreadLocal<User> ADMIN_USER = new ThreadLocal<>();
    private static ThreadLocal<Organization> ORGANIZATION = new ThreadLocal<>();
    private static ThreadLocal<AirslateRestClient> REST_CLIENT = new ThreadLocal<>();
    private final SalesforceAirSlateMan salesforceAirSlateMan = new SalesforceAirSlateMan();
    protected AirSlateApiMan airSlateApiMan;
    protected SalesforceAirslateApi salesforceAirslateApi;
    protected User adminUser;
    protected User standardUser;
    protected Organization organization;

    @Override
    protected void initApi() throws IOException, URISyntaxException {
        super.initApi();
        salesforceAirslateApi = new SalesforceAirslateApi(new AccessToken(accessToken, instanceUrl, "Bearer"), salesforceApi.getASAppNamespace());
        salesforceAirslateApi.auth();
        airSlateApiMan = new AirSlateApiMan(airSlateRestClient());
        setAirSlateEnvironmentURL(getEnvironment());
    }

    @Step
    protected void configurateSalesforceOrg() {
        adminUser = createAirSlateAdmin();
        standardUser = createAirSlateUser();

        try {
            organization = createOrganization(adminUser);
            connectUserToOrganization(organization, standardUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
        connectAdmin(adminUser.email);
        setSetupWizardStage(SetupWizardStage.Stage.FINAL);
        connectWorkspace(organization);
    }

    @Step
    public AirslateRestClient airSlateRestClient() {
        if (REST_CLIENT.get() == null) {
            REST_CLIENT.set(AirSlateApiMan.getAirSlateRestClient(getEnvironment()));
        }
        return REST_CLIENT.get();
    }

    @Step("Setting airSlate environment for App")
    private void setAirSlateEnvironmentURL(AirSlateTestData.Environments environment) {
        salesforceMan.setAirSlateEnvironmentURL(salesforceApi, AirSlateApiMan.getAirSlateApiURL(environment));
    }

    @Step("Creating new airSlate flow with file")
    protected Slate createFlowWithDocument(File fileToUpload) throws IOException {
        return airSlateApiMan.createFlowWithDocument(getFlowName(), fileToUpload);
    }

    @Step
    protected Slate publishFlow(Slate flow) throws IOException {
        return airSlateApiMan.publishFlow(flow);
    }

    @Step
    protected Document uploadDocument(BaseFile baseFile) throws IOException {
        return airSlateApiMan.uploadDocument(baseFile);
    }

    @Step
    protected void addDocumentToFlow(Slate flow, Document document) throws IOException {
        airSlateApiMan.addDocumentToFlow(flow, document);
    }

    @Step("Creating new empty flow")
    protected Slate createEmptyFlow() throws IOException {
        return airSlateApiMan.createEmptyFlow(getFlowName());
    }

    @Step("Creating new airSlate organization")
    protected Organization createOrganization(User user) throws IOException {
        if (ORGANIZATION.get() == null) {
            ORGANIZATION.set(airSlateApiMan.createOrganization(user));
        }
        return ORGANIZATION.get();
    }

    @Step("Connecting airSlate user to workspace")
    protected void connectUserToOrganization(Organization organization, User user) throws IOException {
        if ((ORG_USER.get() == null) || (ORGANIZATION.get() != null && !ORG_USER.get().organization.id.equals(ORGANIZATION.get().id))) {
            ORG_USER.set(airSlateApiMan.connectUserToOrganization(organization, user));
        }
    }

    @Step("Open AdminTools page")
    protected ASAppAdminToolsPage getAdminToolsPage(SalesAppBasePage salesAppBasePage) {
        if (!isDXOrg()) {
            return salesAppBasePage.openInstalledPackagesUrl()
                    .openASAppAdminTools();
        } else {
            return salesAppBasePage.openASAppAdminToolPageOnDX();
        }
    }

    private String getFlowName() {
        return "slate" + getRandomString(5);
    }

    @Step("Creating new airSlate QA User")
    protected User createAirSlateUser() {
        if (USER.get() == null) {
            USER.set(airSlateApiMan.createAirSlateUser(defaultAirSlateUserEmail));
        }
        return USER.get();
    }

    @Step("Creating new airSlate QA Admin")
    protected User createAirSlateAdmin() {
        if (ADMIN_USER.get() == null) {
            ADMIN_USER.set(airSlateApiMan.createAirSlateAdmin(defaultAirSlateUserEmail));
        }
        return ADMIN_USER.get();
    }

    @Step("Delete User Config for {0}")
    protected void deleteUserConfigurationByUsername(String username) {
        salesforceAirSlateMan.deleteUserConfigurationByUsername(salesforceApi, username);
    }

    @Step("Delete user config token for {0}")
    protected void eraseUserConfigurationToken(String username) {
        salesforceAirSlateMan.eraseUserConfigurationToken(salesforceApi, username);
    }

    @Step("Set SetupWizard stage: {0}")
    protected void setSetupWizardStage(SetupWizardStage.Stage stage) {
        salesforceAirSlateMan.setSetupWizardStage(salesforceAirslateApi, stage);
    }

    @Step("Connect airSlate app admin with email: {0}")
    protected void connectAdmin(String email) {
        salesforceAirSlateMan.connectAdmin(salesforceAirslateApi, email);
    }

    @Step("Connect airSlate app user with email: {0}")
    protected void connectStandardUser(String email) {
        salesforceAirSlateMan.connectStandardUser(salesforceApi, baseUrl, stUserUsername, stUserPassword, email);
    }

    @Step("Disconnecting connected airSlate app admin account")
    protected void disconnectAdmin() {
        salesforceAirSlateMan.disconnectAdmin(salesforceAirslateApi);
    }

    @Step("Connecting app workspace: {0}")
    protected void connectWorkspace(Organization organization) {
        salesforceAirSlateMan.connectWorkspace(salesforceAirslateApi, organization);
    }

    @Step("Disconnecting connected Workspace")
    protected void disconnectWorkspace() {
        salesforceAirSlateMan.disconnectWorkspace(salesforceAirslateApi);
    }

    @Step("Creating new Custom Button")
    protected void createCustomButton(String flowId,
                                      String flowName,
                                      CustomButton.Mode mode,
                                      String description,
                                      String label,
                                      CustomButton.Action action,
                                      List<SalesforceObject> layouts,
                                      List<SalesforceObject> listViews) {

        CustomButton buttonConfigData = CUSTOM_BUTTON.get() == null ? new CustomButton() : CUSTOM_BUTTON.get();
        CUSTOM_BUTTON.set(salesforceAirSlateMan.createCustomButton(salesforceAirslateApi, buttonConfigData, flowId, flowName, mode, description, label, action, layouts, listViews));
    }

    @Step("Deleting all created Custom Buttons")
    protected void deleteAllCustomButtons() {
        salesforceAirSlateMan.deleteAllCustomButtons(salesforceAirslateApi);
    }

    @Step("Creating new Scheduled FLOW")
    protected void createScheduledFlow(ScheduledFlow.Flow flow,
                                       String name,
                                       ScheduledFlow.Option option,
                                       ScheduledFlow.Params params,
                                       ScheduledFlow.TimeParams timeParams) {

        salesforceAirSlateMan.createScheduledFlow(salesforceAirslateApi, flow, name, option, params, timeParams);
    }

    @Step("Deleting all Scheduled FLOWs")
    protected void deleteAllScheduledFlows() {
        salesforceAirSlateMan.deleteAllScheduledFlows(salesforceAirslateApi);
    }

    @Step("Creating Opportunity record")
    protected String createOpportunityRecord() throws URISyntaxException, IOException {
        return salesforceMan.createOpportunityRecord(salesforceApi);
    }

    @Step("Getting Slates for {0}")
    protected List<Packet> getSlates(String id) throws IOException {
        return airSlateApiMan.getSlates(id);
    }

    @Step("Getting revisions of flowId:{0}/slateId:{1}")
    protected List<PacketRevision> getRevisions(String flowId, String slateId) throws IOException {
        return airSlateApiMan.getRevisions(flowId, slateId);
    }

    @Step("Adding new ListView for {0}")
    protected String addListViewTOObject(SalesforceObject object) throws ConnectionException {
        return salesforceMan.addListViewTOObject(object, metadataApi);
    }

    @Step("Getting UTC Calendar")
    protected Calendar getUTCCalendar() {
        return GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.UK);
    }

    @Step("Waiting for {0} slates from {1}")
    protected boolean waitForSlates(int number, String flowId) {
        return airSlateApiMan.waitForSlates(number, flowId);
    }

    @Step
    protected Packet createBlankSlate(Slate flow) throws IOException {
        return airSlateApiMan.createBlankSlate(flow);
    }

    protected Packet createBlankFlowPacketFromContext(User user, String recordId, SalesforceObject object, String salesforceUsername, String flowId) throws IOException {
        return airSlateApiMan.createBlankFlowPacketFromContext(salesforceApi, user, recordId, object, salesforceUsername, flowId);
    }

    protected void createBlankFlowPacketFromContext(String recordId, SalesforceObject object, String salesforceUsername, String password, Slate flow, String buttonLabel) throws IOException {
        salesforceAirSlateMan.createBlankFlowPacketFromContext(salesforceApi, baseUrl, recordId, object, salesforceUsername, password, flow, buttonLabel);
    }

    @AfterClass
    protected void addProperties() {
        if (ADMIN_USER.get() != null) {
            System.setProperty("airslate.admin", ADMIN_USER.get().email);
        }
        if (USER.get() != null) {
            System.setProperty("airslate.user", USER.get().email);
        }
        if (ORGANIZATION.get() != null) {
            System.setProperty("orgDomain", ORGANIZATION.get().subdomain);
        }
    }
}