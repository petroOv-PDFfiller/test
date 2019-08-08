package utils.airslate;

import api.salesforce.SalesforceRestApi;
import api.salesforce.entities.SalesforceObject;
import com.airslate.api.AirslateRestClient;
import com.airslate.api.models.directInvite.OrganizationUser;
import com.airslate.api.models.documents.Dictionary;
import com.airslate.api.models.documents.Document;
import com.airslate.api.models.editor.EditorLinkResp;
import com.airslate.api.models.files.BaseFile;
import com.airslate.api.models.onboarding.Organization;
import com.airslate.api.models.packets.Packet;
import com.airslate.api.models.packets.PacketRevision;
import com.airslate.api.models.slates.Slate;
import com.airslate.api.models.users.User;
import com.airslate.api.util.RetrofitWait;
import com.airslate.util.Generator;
import com.airslate.ws.EditorWsClient;
import com.google.common.collect.ImmutableMap;
import com.itextpdf.text.Rectangle;
import data.TestData;
import data.airslate.AirSlateTestData;
import io.qameta.allure.Step;
import utils.AirSlateWSEditor;
import utils.PDFReadMan;
import utils.StringMan;
import utils.salesforce.SalesforceMan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.airslate.api.models.slates.Slate.SlateStatus.PUBLISHED;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.util.Objects.requireNonNull;
import static org.awaitility.Awaitility.await;

public class AirSlateApiMan {

    private static final String SECURED_CONNECTION_SCHEME = "https://";
    private static final String UNSECURED_CONNECTION_SCHEME = "http://";
    private static final String ADMIN = "Admin";
    private AirslateRestClient airSlateRestClient;

    public AirSlateApiMan(AirslateRestClient airSlateRestClient) {
        this.airSlateRestClient = airSlateRestClient;
    }

    public static AirslateRestClient getAirSlateRestClient(AirSlateTestData.Environments environment) {
        return getAirSlateRestClient(getAirSlateScheme(environment) + environment.getDomain());
    }

    public static AirslateRestClient getAirSlateRestClient(String url) {
        System.setProperty("mvn.airslate.url", url);
        AirslateRestClient airSlateRestClient = new AirslateRestClient();
        airSlateRestClient.setDeserializationFeature(FAIL_ON_UNKNOWN_PROPERTIES, false);
        return airSlateRestClient;
    }

    public static String getAirSlateApiURL(AirSlateTestData.Environments environment) {
        return getAirSlateScheme(environment) + environment.getAPIDomain();
    }

    private static String getAirSlateScheme(AirSlateTestData.Environments environment) {
        switch (environment) {
            case PROD:
            case RC:
            case STAGE:
                return SECURED_CONNECTION_SCHEME;
            default:
                return UNSECURED_CONNECTION_SCHEME;
        }
    }

    public void setAirSlateRestClient(AirslateRestClient airSlateRestClient) {
        this.airSlateRestClient = airSlateRestClient;
    }

    @Step("Creating new airSlate flow with name: {flowname}")
    public Slate createFlowWithDocument(String flowName, File fileToUpload) throws IOException {
        Slate flow = createEmptyFlow(flowName);
        BaseFile baseFile = new BaseFile(fileToUpload, "name.pdf");
        addDocumentToFlow(flow, uploadDocument(baseFile));
        flow = publishFlow(flow);
        createBlankSlate(flow);
        return flow;
    }

    @Step
    public Slate publishFlow(Slate flow) throws IOException {
        flow = Objects.requireNonNull(airSlateRestClient.flows
                .getFlow(flow.id).execute()
                .body())
                .get();
        flow.status = PUBLISHED;
        flow.wizard_status = Slate.WizardStatus.DONE;
        Objects.requireNonNull(airSlateRestClient.flows
                .updateFlow(flow.id, flow)
                .execute()
                .body())
                .get();
        return flow;
    }

    @Step
    public Document uploadDocument(BaseFile baseFile) throws IOException {
        String documentId = Objects.requireNonNull(airSlateRestClient.documents
                .uploadDocument(baseFile)
                .execute()
                .body())
                .get()
                .id;
        return Objects.requireNonNull(airSlateRestClient.documents
                .getDocument(documentId, null)
                .execute()
                .body())
                .get();
    }

    @Step
    public void addDocumentToFlow(Slate flow, Document document) throws IOException {
        airSlateRestClient.flows
                .addDocumentsToFlow(flow.id, flow.template.id, Collections.singletonList(document))
                .execute();
    }

    @Step("Creating new empty flow")
    public Slate createEmptyFlow(String flowName) throws IOException {
        return Objects.requireNonNull(airSlateRestClient.flows
                .createFlow(new Slate(flowName, "SF-AS tests app", true))
                .execute()
                .body())
                .get();
    }

    @Step("Creating new airSlate organization")
    public Organization createOrganization(User user) {
        Organization organization = getNewOrganization(getUniqueAirSlateOrgDomain(), user);
        airSlateRestClient.interceptors().organizationDomain.setOrganizationDomain(organization.subdomain);
        return organization;
    }

    private Organization getNewOrganization(String orgDomain, User user) {
        try {
            airSlateRestClient.auth.login(user.email, TestData.defaultPassword).execute();
            return Objects.requireNonNull(airSlateRestClient.organizationManagement
                    .createOrganization(new Organization(orgDomain, orgDomain))
                    .execute()
                    .body())
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
            throw new AssertionError("Cannot create org");
        }
    }

    @Step("Connecting airSlate user to workspace")
    public OrganizationUser connectUserToOrganization(Organization organization, User user) throws IOException {
        airSlateRestClient.interceptors().organizationDomain.setOrganizationDomain(organization.subdomain);
        String token = airSlateRestClient.interceptors().authenticator.getToken();
        airSlateRestClient.auth.login(user.email, TestData.defaultPassword).execute();
        OrganizationUser organizationUser = Objects.requireNonNull(airSlateRestClient.domainJoin
                .addOrganizationUser(new OrganizationUser(user.id, organization.id))
                .execute()
                .body())
                .get();
        airSlateRestClient.interceptors().authenticator.setToken(token);
        return organizationUser;
    }

    @Step("Updating airSlate password for user: {user.email}")
    public User updateUserPassword(User user) throws IOException {
        String token = airSlateRestClient.interceptors().authenticator.getToken();
        airSlateRestClient.auth.login(user.email, TestData.defaultPassword).execute();
        user.password = TestData.defaultPassword + "new";
        user = Objects.requireNonNull(airSlateRestClient.userManagement
                .updateUser(user.id, user)
                .execute()
                .body())
                .get();
        airSlateRestClient.interceptors().authenticator.setToken(token);
        return user;
    }

    @Step("Creating new airSlate QA User")
    public User createAirSlateUser(String baseEmail) {
        return getUser(baseEmail, "user");
    }

    @Step("Creating new airSlate QA Admin")
    public User createAirSlateAdmin(String baseEmail) {
        return getUser(baseEmail, ADMIN);
    }

    public User getUser(String baseEmail, String role) {
        User newUser = new User(StringMan.makeUniqueEmail(baseEmail, String.valueOf(Thread.currentThread().getId())),
                TestData.defaultPassword,
                "Salesforce",
                "Autotest_" + role,
                Generator.generateUserName());
        try {
            newUser = airSlateRestClient.qaUsers.createUser(newUser).execute().body();
            return newUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUniqueAirSlateOrgDomain() {
        return "sf-autotest" + StringMan.getRandomStringFrom("abcdefghijklmnopqrstuvwxyz".toCharArray(), 5);
    }

    public List<Packet> getSlates(String id) throws IOException {
        return Objects.requireNonNull(airSlateRestClient.packets
                .getPackets(id, null, null, null, null)
                .execute()
                .body())
                .get();
    }

    @Step("Getting revisions of flowId:{flowId}/slateId:{slateId}")
    public List<PacketRevision> getRevisions(String flowId, String slateId) throws IOException {
        return Objects.requireNonNull(airSlateRestClient.packets.getRevisionsList(flowId, slateId).execute().body()).get();
    }

    @Step("Waiting for {number} slates from {flowId}")
    public boolean waitForSlates(int number, String flowId) {
        try {
            new RetrofitWait<>(airSlateRestClient.packets.getPackets(flowId, null, null, null, null))
                    .withTimeout(120000)
                    .pollingEvery(10000)
                    .withMessage("Slates size != " + number)
                    .until(r -> Objects.requireNonNull(r.body()).get().size() == number);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Create new slate in flow {flow.name}")
    public Packet createBlankSlate(Slate flow) throws IOException {
        Packet slate = Objects.requireNonNull(airSlateRestClient.packets
                .flowsCreateBlankPacket(flow.id, new Packet())
                .execute()
                .body())
                .get();
        new RetrofitWait<>(airSlateRestClient.packets.flowsGetPacket(flow.id, slate.id))
                .withMessage("BLANK REVISION IS NOT FINISHED")
                .withTimeout(30000)
                .pollingEvery(3000)
                .until(r -> Objects.requireNonNull(r.body()).get()
                        .finished_revisions_count == 1);
        return slate;
    }

    @Step
    public Packet createBlankFlowPacketFromContext(SalesforceRestApi salesforceRestApi, User user, String recordId, SalesforceObject object, String salesforceUsername, String flowId) throws IOException {
        return createBlankFlowPacketFromContext(salesforceRestApi, user, recordId, object, salesforceUsername, flowId, 30000);
    }

    @Step
    public Packet createBlankFlowPacketFromContext(SalesforceRestApi salesforceRestApi, User user, String recordId, SalesforceObject object, String salesforceUsername, String flowId, int timeout) throws IOException {
        Packet blankPacket = new Packet();
        String token = airSlateRestClient.interceptors().authenticator.getToken();
        try {
            airSlateRestClient.auth.login(user.email, TestData.defaultPassword).execute();
            blankPacket.additional_data = ImmutableMap.of("sf_data",
                    ImmutableMap.of("run_flow",
                            ImmutableMap.of("record_id", recordId, "user_id",
                                    new SalesforceMan().getUserId(salesforceRestApi, salesforceUsername),
                                    "object_api_name", object.getAPIName())));
            Slate flow = Objects.requireNonNull(airSlateRestClient.flows
                    .getFlow(flowId)
                    .execute()
                    .body())
                    .get();
            Packet slate = Objects.requireNonNull(airSlateRestClient.packets.flowsCreateBlankPacket(flow.id, blankPacket).execute().body()).get();
            new RetrofitWait<>(airSlateRestClient.packets.getPacket(flow.id, slate.id))
                    .withMessage("BLANK REVISION IS NOT FINISHED")
                    .withTimeout(timeout)
                    .pollingEvery(3000)
                    .until(r -> Objects.requireNonNull(r.body()).get()
                            .finished_revisions_count == 1);
            return slate;
        } finally {
            airSlateRestClient.interceptors().authenticator.setToken(token);
        }
    }

    @Step
    public void addFillableFieldsToDocument(Document document, File fileToUpload, User adminUser, String orgDomain) {
        Rectangle pdf = PDFReadMan.getPdfSize(fileToUpload.getAbsolutePath());
        double width = pdf.getWidth();
        double height = pdf.getHeight();

        AirSlateWSEditor ws = null;
        try {
            ws = new AirSlateWSEditor(airSlateRestClient.interceptors().authenticator.getToken(), document, adminUser, orgDomain);
            ws.addText(width / 4, height / 20, "first", true);
            ws.addText(width / 4, height / 5, "second", false);
            ws.addNumber(width / 4, height / 2, "third", false);
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (ws != null) {
                ws.destroy();
            }
        }
    }

    @Step("Get document fields: {document.id}")
    public List<Dictionary> getDocumentFields(Document document) throws IOException {
        return requireNonNull(airSlateRestClient.documents
                .getDocumentFields(document.id)
                .execute()
                .body())
                .get();
    }

    @Step("Get latest revision from slate: {slate.id}")
    public PacketRevision getLatestRevisions(Slate flow, Packet slate) throws IOException {
        return requireNonNull(airSlateRestClient.packets
                .getPacket(flow.id, slate.id)
                .execute()
                .body())
                .get()
                .latest_revisions;
    }

    @Step("Get revision by id: {last.id} in slate: {slate.id}")
    public PacketRevision getRevision(Slate flow, Packet slate, PacketRevision last) throws IOException {
        return requireNonNull(airSlateRestClient.packets
                .getRevision(flow.id, slate.id, last.id)
                .execute()
                .body())
                .get();
    }

    @Step
    public com.airslate.api.models.documents.Document getDocument(Document document) throws IOException {
        return requireNonNull(airSlateRestClient.documents
                .getDocument(document.id, null)
                .execute()
                .body())
                .get();
    }

    @Step
    public void finishRevision(Slate flow, Packet packet) throws IOException {
        finishRevision(flow, packet, 30000);
    }

    @Step
    public void finishRevision(Slate flow, Packet packet, int timeout) throws IOException {
        airSlateRestClient.packets.flowsFinishPacket(flow.id, packet.id).execute().body().get();
        new RetrofitWait<>(airSlateRestClient.packets.flowsGetPacket(flow.id, packet.id))
                .withMessage("REVISION STATUS IS NOT FINISHED")
                .withTimeout(timeout)
                .until(r -> {
                    if (r.body() != null) {
                        Packet executePacket = r.body().get();
                        return executePacket.finished_revisions_count.equals(executePacket.revisions_total);
                    } else {
                        return false;
                    }
                });
    }

    @Step
    public void updateDocumentField(Document document, Dictionary dictionary, int fieldNumber) throws IOException {
        airSlateRestClient.documents
                .updateDocumentFields(document.id, Collections.singletonList(dictionary))
                .execute();
        new RetrofitWait<>(airSlateRestClient.documents.getDocumentFields(document.id))
                .withTimeout(20000)
                .ignoring(NullPointerException.class)
                .until(r -> Objects.requireNonNull(r.body()).get().get(fieldNumber).value.equals(dictionary.value));
    }

    @Step
    public void updateDocumentFields(Document document, List<Dictionary> dictionary) throws IOException {
        airSlateRestClient.documents
                .updateDocumentFields(document.id, dictionary)
                .execute();
    }

    @Step
    public void waitPreFillDocument(String documentId) throws IOException {
        waitPreFillDocument(documentId, 60000);
    }

    @Step
    public void waitPreFillDocument(String documentId, int timeout) throws IOException {
        EditorLinkResp editorLinkResp = airSlateRestClient.editor.getEditorLink(documentId).execute().body();
        AtomicReference<EditorWsClient> wsClient = new AtomicReference<>();
        await("Cannot connect to websocket").ignoreExceptions().atMost(180, TimeUnit.SECONDS).until(() -> {
            wsClient.set(new EditorWsClient(airSlateRestClient.interceptors().authenticator.getToken(),
                    requireNonNull(editorLinkResp).projectId,
                    editorLinkResp.viewerId,
                    airSlateRestClient.interceptors().organizationDomain.getOrganizationDomain()));
            return wsClient.get().waitResponse(s -> s.contains("\"onStart\":[{\"action\""), timeout, "Fillable Fields NOT LOADED");
        });
        if (wsClient.get() != null && !wsClient.get().isClosed()) {
            wsClient.get().close();
        }
    }

    @Step
    public void acceptAgreement(User user) {
        String token = airSlateRestClient.interceptors().authenticator.getToken();
        try {
            airSlateRestClient.auth.login(user.email, TestData.defaultPassword).execute();
            airSlateRestClient.userManagement
                    .acceptTos(user.id)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            airSlateRestClient.interceptors().authenticator.setToken(token);
        }
    }

    @Step
    public void acceptAgreement() {
        try {
            User me = airSlateRestClient.userManagement.getUserInfo(null).execute().body().get();
            airSlateRestClient.userManagement
                    .acceptTos(me.id)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Step
    public PacketRevision createNewRevision(Slate flow, Packet packet) throws IOException {
        return createNewRevision(flow, packet, 40000);
    }

    @Step
    public PacketRevision createNewRevision(Slate flow, Packet packet, int timeout) throws IOException {
        PacketRevision packetRevision = Objects.requireNonNull(airSlateRestClient.packets
                .flowsCreateRevision(flow.id, packet.id, new PacketRevision())
                .execute()
                .body())
                .get();
        new RetrofitWait<>(airSlateRestClient.packets.getRevision(flow.id, packet.id, packetRevision.id))
                .withTimeout(timeout)
                .withMessage("packet.status != DRAFT")
                .until(r -> Objects.requireNonNull(r.body()).get().status == PacketRevision.Status.DRAFT);
        return Objects.requireNonNull(airSlateRestClient.packets
                .getRevision(flow.id, packet.id, packetRevision.id)
                .execute()
                .body())
                .get();
    }
}
