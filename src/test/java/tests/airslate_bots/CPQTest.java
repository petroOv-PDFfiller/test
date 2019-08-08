package tests.airslate_bots;

import api.salesforce.SalesforceRestApi;
import api.salesforce.entities.SalesforceObject;
import api.salesforce.responses.FindRecordsIDResponse;
import com.airslate.api.AirslateRestClient;
import com.airslate.api.converters.GsonConverterAirslate;
import com.airslate.api.models.auth.LoginResp;
import com.airslate.api.models.documents.Document;
import com.airslate.api.models.editor.EditorLinkResp;
import com.airslate.api.models.packets.Packet;
import com.airslate.api.models.packets.PacketRevision;
import com.airslate.api.models.slates.Slate;
import com.airslate.api.models.users.User;
import com.airslate.util.email.mailer.MailerEmailParser;
import com.airslate.util.email.mailer.models.ParsedMessage;
import com.airslate.util.email.models.AirslateEmail;
import com.airslate.util.lhtml.LHtml;
import com.airslate.ws.EditorWebsocket;
import com.google.common.collect.ImmutableMap;
import com.thirdparty.html_form_builder.HTMLFormBuilderClient;
import com.thirdparty.html_form_builder.models.HTMLForm;
import com.thirdparty.mailer.MailerApi;
import data.TestData;
import data.salesforce.SalesforceOrg;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.aeonbits.owner.ConfigFactory;
import org.testng.annotations.Optional;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import utils.airslate.AirSlateApiMan;
import utils.airslate.FormBuilderMan;
import utils.configs.FormBuilderEnvironmentConfig;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static api.salesforce.entities.SalesforceObject.*;
import static java.util.Objects.requireNonNull;
import static org.awaitility.Awaitility.await;
import static utils.StringMan.getRandomString;
import static utils.StringMan.makeUniqueEmail;
import static utils.airslate.AirSlateApiMan.getAirSlateRestClient;
import static utils.airslate.FormBuilderMan.getFormBuilderClient;

@Feature("Document generation")
public class CPQTest {

    public static final String YOUR_ORDER_IS_READY_TO_SIGN = "Your order is ready to sign!";
    public static final String QUOTE_NEEDS_YOUR_APPROVAL = "Quote needs your approval";
    private final String password = TestData.defaultPassword;
    private final String approverEmail = makeUniqueEmail("pdf_sf_rnt@support.pdffiller.com", String.valueOf(Thread.currentThread().getId()));
    private final String contactEmail = makeUniqueEmail("pdf_sf_res_recip@support.pdffiller.com", String.valueOf(Thread.currentThread().getId()));
    private String orgDomain = "dg-autotest";
    private String email = "pdf_sf_aqa+dg@support.pdffiller.com";
    private SalesforceOrg salesforceOrg;
    private String flowId;
    private String objId;
    private Map<String, SalesforceObject> recordsToDelete = new HashMap<>();
    private AirslateRestClient airslateRestClient;
    private User user;
    private SalesforceRestApi salesforceRestApi;
    private FormBuilderEnvironmentConfig config;

    @Parameters({"environment", "orgDomain", "email"})
    @BeforeClass
    public void setUp(@Optional("STAGE") String environment, @Optional("dg-autotest") String orgDomain, @Optional("pdf_sf_aqa+dg@support.pdffiller.com") String email) throws IOException, URISyntaxException {
        this.orgDomain = orgDomain;
        this.email = email;
        config = ConfigFactory.create(FormBuilderEnvironmentConfig.class,
                ImmutableMap.of("environment", environment));
        flowId = getFlowId(environment);
        salesforceOrg = SalesforceOrg.valueOf(environment);
        airslateRestClient = getAirSlateRestClient(config.url());
        salesforceRestApi = new SalesforceRestApi("https://login.salesforce.com/",
                salesforceOrg.getAdminUsername(), salesforceOrg.getAdminPassword());
        salesforceRestApi.auth();
        createRecords(salesforceRestApi);
        airslateRestClient.auth
                .login(email, password)
                .execute()
                .body();
        user = airslateRestClient.userManagement.getUserInfo(null).execute().body().get();
        airslateRestClient.interceptors().organizationDomain.setOrganizationDomain(orgDomain);
        new AirSlateApiMan(airslateRestClient).acceptAgreement();
    }

    @Test(testName = "CPQ scenario API test")
    @Story("CPQ scenarion")
    public void cpqApiTest() throws IOException, URISyntaxException, InterruptedException, InstantiationException, IllegalAccessException {
        AirSlateApiMan airSlateApiMan = new AirSlateApiMan(airslateRestClient);
        Slate flow = airslateRestClient.flows.getFlow(flowId).execute().body().get();
        Packet slate = getBlankFlowPacketFromContext(airSlateApiMan);
        fillFormAndCheckGeneratedDocument(airSlateApiMan, flow, slate);

        addSignature(flow, slate, approverEmail, QUOTE_NEEDS_YOUR_APPROVAL, "supplier_signature", "Approver");
        addSignature(flow, slate, contactEmail, YOUR_ORDER_IS_READY_TO_SIGN, "client_signature", "Client");

        Map<String, Object> stage = salesforceRestApi.recordsService().getRecordFields(OPPORTUNITY, objId, Collections.singletonList("StageName"));
        int attachmentsCount = salesforceRestApi.recordsService().executeQuery("Select id from " + ATTACHMENT.getAPIName() + " where ParentId='" + objId + "'", FindRecordsIDResponse.class).records.length;
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(stage.get("StageName"), "Closed Won");
        softAssert.assertEquals(attachmentsCount, 2, "Attachment is not loaded to Salesforce");
        softAssert.assertAll();
    }

    @Step("Fill first revision HTML form and check generated contract document")
    private PacketRevision fillFormAndCheckGeneratedDocument(AirSlateApiMan airSlateApiMan, Slate flow, Packet slate) throws IOException, URISyntaxException, InterruptedException {
        PacketRevision revision = airSlateApiMan.createNewRevision(flow, slate, 300000);

        fillForm(revision.documents.get(0));
        airSlateApiMan.waitPreFillDocument(revision.documents.get(1).id, 300000);
        airSlateApiMan.finishRevision(flow, slate, 300000);
        return revision;
    }

    @Step("Create new Slate for CPQ flow")
    private Packet getBlankFlowPacketFromContext(AirSlateApiMan airSlateApiMan) throws IOException {
        return airSlateApiMan.createBlankFlowPacketFromContext(salesforceRestApi, user, objId, OPPORTUNITY,
                salesforceOrg.getAdminUsername(), flowId, 300000);
    }

    @Step("Fill HTML form by Sales manager")
    private void fillForm(Document document) {
        HTMLFormBuilderClient client = getFormBuilderClient(config.formUrl(),
                airslateRestClient.interceptors().authenticator.getToken(),
                orgDomain);
        await("Wait form prefill").atMost(90, TimeUnit.SECONDS).ignoreExceptions()
                .until(() -> client.htmlFormBuilderService
                        .getForm(document.id, true)
                        .execute().code() == 200);
        HTMLForm form = new HTMLForm();
        form.id = document.id;
        form.html = "";
        form.setJson(String.format("{\"OpportunityID\":\"%s\",\"OpportunityName\":\"OpportunityCPQ\"," +
                "\"OpportunityAmount\":\"400000\",\"DiscountAmount\":0,\"DiscountedPrice\":400000,\"VATAmount\":0," +
                "\"Total\":400000,\"InternalApproverName\":\"lol\",\"InternalApproverTitle\":\"CEO\"," +
                "\"InternalApproverEmail\":\"%s\"}", objId, approverEmail));
        new FormBuilderMan(client).fillForm(form);
        await("Wait for update form field").atMost(30, TimeUnit.SECONDS)
                .pollInterval(3, TimeUnit.SECONDS)
                .ignoreExceptions()
                .until(() -> new AirSlateApiMan(airslateRestClient)
                        .getDocumentFields(document).get(13).value.equals(approverEmail));
    }

    @Step("Sign generated document Signature field - {signatureFiled} by {signatureText} (email: {emailAddress})")
    private void addSignature(Slate flow, Packet slate, String emailAddress, String emailSubject, String signatureFiled, String signatureText) throws IOException, IllegalAccessException, InstantiationException {
        ParsedMessage parsedMessage;
        String openRevisionLink;
        Pattern pattern;
        Matcher matcher;
        String hash;
        AirslateRestClient unauthorized;
        Response response;
        PacketRevision packet;
        EditorLinkResp editorLinkResp;
        AtomicReference<EditorWebsocket> wsClient;
        MailerEmailParser mailer = new MailerEmailParser(emailAddress);
        parsedMessage = mailer.getEmail(mail -> mail.subject.equals(emailSubject + "\r"), 10);
        openRevisionLink = new MailerApi().decryptLinkO(new LHtml().fromHtml(parsedMessage.htmlBody, AirslateEmail.class).openRevisionLink);

        pattern = Pattern.compile("hash=([^&]+)");
        matcher = pattern.matcher(openRevisionLink);
        matcher.find();
        hash = matcher.group(1);

        unauthorized = new AirslateRestClient();
        response = unauthorized.getRequest(unauthorized.getRetrofit().baseUrl() + "autologin/email?hash=" + hash);
        if (response.isSuccessful()) {
            ResponseBody body = response.peekBody(Long.MAX_VALUE);
            String token = GsonConverterAirslate.getGsonConverter().fromJson(body.string(), LoginResp.class).access_token;
            unauthorized.interceptors().authenticator.setToken(token);
        }
        unauthorized.interceptors().organizationDomain.setOrganizationDomain(unauthorized.auth.usersMe().execute().body().get().organizations.stream().findFirst().get().subdomain);

        packet = new AirSlateApiMan(unauthorized).createNewRevision(flow, slate, 300000);
        new AirSlateApiMan(unauthorized).acceptAgreement();
        editorLinkResp = unauthorized.editor.getEditorLink(packet.documents.get(0).id).execute().body();
        wsClient = new AtomicReference<>();
        waitForFillableFields(unauthorized, editorLinkResp, wsClient);
        wsClient.get().fillableFields.setSignatureTextInField(signatureFiled, signatureText);
        wsClient.get().close();
        new AirSlateApiMan(unauthorized).finishRevision(flow, slate, 300000);
    }

    @Step("Wait for document prefill")
    private void waitForFillableFields(AirslateRestClient unauthorized, EditorLinkResp editorLinkResp, AtomicReference<EditorWebsocket> wsClient) {
        await().ignoreExceptions().atMost(180, TimeUnit.SECONDS).until(() -> {
            wsClient.set(new EditorWebsocket(unauthorized.interceptors().authenticator.getToken(),
                    requireNonNull(editorLinkResp).projectId,
                    editorLinkResp.viewerId,
                    unauthorized.interceptors().organizationDomain.getOrganizationDomain()));
            wsClient.get().getClient().waitFillableFieldsLoaded();
            return wsClient.get().getClient().waitResponse(s -> s.contains("\"onStart\":[{\"action\""), 30000, "Fillable Fields NOT LOADED");
        });
    }

    @Step
    private void createRecords(SalesforceRestApi salesforceRestApi) throws IOException, URISyntaxException {
        String accountName = getRandomString(6) + "CPQ Account test";
        Map<String, String> accountProperties = new HashMap<String, String>() {
            {
                put("Name", accountName);
                put("BillingStreet", "BillingStreetCPQ");
                put("BillingCity", "BillingCityCPQ");
                put("BillingState", "BillingStateCPQ");
                put("BillingPostalCode", "BillingPostalCodeCPQ");
                put("BillingCountry", "BillingCountryCPQ");
            }
        };
        String accountId = salesforceRestApi.recordsService().createRecord(SalesforceObject.ACCOUNT,
                accountProperties).id;
        recordsToDelete.put(accountId, SalesforceObject.ACCOUNT);
        Map<String, String> contactProperties = new HashMap<String, String>() {
            {
                put("LastName", getRandomString(6) + "CPQ");
                put("FirstName", "TEST");
                put("Title", "CEOCPQ");
                put("AccountId", accountId);
                put("Email", contactEmail);
            }
        };
        String contactId = salesforceRestApi.recordsService().createRecord(SalesforceObject.CONTACT,
                contactProperties).id;
        recordsToDelete.put(contactId, SalesforceObject.CONTACT);
        Map<String, String> product = new HashMap<String, String>() {
            {
                put("Name", "ProductCPQ");
                put("IsActive", "true");
                put("ProductCode", "cpqtest");
            }
        };
        String productId = salesforceRestApi.recordsService().createRecord(SalesforceObject.PRODUCT_2,
                product).id;
        recordsToDelete.put(productId, SalesforceObject.PRODUCT_2);
        Calendar cl = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.UK);
        cl.add(Calendar.HOUR, 24);
        Map<String, String> opportunity = new HashMap<String, String>() {
            {
                put("Name", "OpportunityCPQ");
                put("StageName", "Prospecting");
                put("AccountId", accountId);
                put("CloseDate", String.valueOf(cl.getTime().getTime()));
            }
        };
        objId = salesforceRestApi.recordsService().createRecord(OPPORTUNITY,
                opportunity).id;
        recordsToDelete.put(objId, OPPORTUNITY);
        String priceBookId = salesforceRestApi.recordsService()
                .executeQuery("select id from Pricebook2 where IsStandard = true limit 1", FindRecordsIDResponse.class)
                .records[0].Id;
        Map<String, String> priceBookEntry = new HashMap<String, String>() {
            {
                put("Pricebook2Id", priceBookId);
                put("Product2Id", productId);
                put("UnitPrice", "100000");
                put("isActive", "true");
            }
        };
        String priceBookEntryId = salesforceRestApi.recordsService().createRecord(SalesforceObject.PRICEBOOK_ENTRY,
                priceBookEntry).id;
        recordsToDelete.put(priceBookEntryId, PRICEBOOK_ENTRY);
        Map<String, String> opportunityLineItem = new HashMap<String, String>() {
            {
                put("OpportunityId", objId);
                put("Product2Id", productId);
                put("PricebookEntryId", priceBookEntryId);
                put("Quantity", "4");
                put("UnitPrice", "100000");
            }
        };
        String opportunityLineItemId = salesforceRestApi.recordsService().createRecord(SalesforceObject.OPPORTUNITY_LINE_ITEM,
                opportunityLineItem).id;
        recordsToDelete.put(opportunityLineItemId, OPPORTUNITY_LINE_ITEM);
        Map<String, String> opportunityContactRole = new HashMap<String, String>() {
            {
                put("ContactId", contactId);
                put("IsPrimary", "true");
                put("OpportunityId", objId);
            }
        };
        String opportunityContactRoleId = salesforceRestApi.recordsService().createRecord(SalesforceObject.OPPORTUNITY_CONTACT_ROLE,
                opportunityContactRole).id;
        recordsToDelete.put(opportunityContactRoleId, OPPORTUNITY_CONTACT_ROLE);
    }

    private String getFlowId(String environment) {
        switch (environment.toLowerCase()) {
            case ("prod"):
                return "38D6A72C-5000-0000-0000BA29";
            case ("rc"):
                return "4E2AA8F9-2000-0000-0000BA29";
            case ("stage"):
                return "CFBFE73E-0000-0000-0000BA29";
            default:
                throw new NullPointerException("Doesnt cover " + environment + " environment");
        }
    }

    @AfterMethod(alwaysRun = true)
    public void deleteTestRecords() {
        if (salesforceRestApi != null) {
            salesforceRestApi.auth();
            for (Map.Entry<String, SalesforceObject> entry : recordsToDelete.entrySet()) {
                try {
                    salesforceRestApi.recordsService().deleteRecord(entry.getValue(), entry.getKey());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
