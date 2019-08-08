package tests.html_form_builder;

import com.airslate.api.AirslateRestClient;
import com.airslate.api.models.documents.Dictionary;
import com.airslate.api.models.documents.Document;
import com.airslate.api.models.packets.Packet;
import com.airslate.api.models.packets.PacketRevision;
import com.airslate.api.models.slates.Slate;
import com.airslate.api.models.slates.permissions.SlatePermission;
import com.airslate.api.util.RetrofitWait;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.thirdparty.html_form_builder.HTMLFormBuilderClient;
import com.thirdparty.html_form_builder.models.Element;
import com.thirdparty.html_form_builder.models.FormPages;
import com.thirdparty.html_form_builder.models.HTMLForm;
import com.thirdparty.html_form_builder.models.Page;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import listeners.WebTestListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utils.StringMan;

import java.io.IOException;
import java.util.*;

import static data.salesforce.HTMLFormBuilderData.FieldType.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Feature("HTML form builder")
@Listeners({WebTestListener.class})
public class FormBuilderFillTests extends FormBuilderBaseTest {

    private AirslateRestClient client;
    private String flowID;
    private String slateID;
    private HTMLFormBuilderClient formBuilderClient;
    private String textFieldName = "textFiled";
    private String commentFieldName = "commentField";
    private String textFieldValue = "oldTextFieldValue";
    private HTMLForm fillDocument;
    private String access_token;

    @Step
    @BeforeClass
    public void setUp() throws IOException {
        client = new AirslateRestClient(testData.apiUrl);
        client.interceptors()
                .organizationDomain
                .setOrganizationDomain(orgDomain);
        access_token = Objects.requireNonNull(client.auth
                .login(testData.getEmail(), testData.password)
                .execute()
                .body())
                .access_token;

        client.setDeserializationFeature(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        formBuilderClient = new HTMLFormBuilderClient(getApiUrl());
        formBuilderClient.interceptors()
                .organizationDomain
                .setOrganizationDomain(orgDomain);
        formBuilderClient.interceptors()
                .authenticator
                .setToken(access_token);
        formBuilderClient.setDeserializationFeature(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String flowName = getFlowName();
        Slate slate = new Slate(flowName, flowName, false);
        flowID = Objects.requireNonNull(client.flows
                .createFlow(slate)
                .execute()
                .body())
                .get()
                .id;
    }

    @Story("Create html form flow")
    @Test
    public void createFormBuilderDocument() throws IOException {
        Document formBuilderDocument = initFormBuilderDocumentData("HFBdoc" + StringMan.getRandomString(5));
        formBuilderDocument = Objects.requireNonNull(client.documents
                .createDocument(formBuilderDocument)
                .execute()
                .body())
                .get();

        List<Element> pageElements = new ArrayList<>();
        String descriptionFieldName = "html";
        pageElements.add(new Element(descriptionFieldName, HTML, false));
        pageElements.add(new Element(textFieldName, TEXT, true));
        pageElements.add(new Element(commentFieldName, COMMENT, true));
        List<Page> pages = new ArrayList<>();
        pages.add(new Page("Page1", pageElements));

        String newFormName = "HtmlForm";
        HTMLForm form = initFormData(newFormName, formBuilderDocument.id, flowID, new FormPages(pages, "off"));
        formBuilderDocument = Objects.requireNonNull(formBuilderClient.htmlFormBuilderService
                .changeDocument(form)
                .execute()
                .body())
                .get();
        formBuilderDocument = Objects.requireNonNull(client.documents
                .getDocument(formBuilderDocument.id, "")
                .execute()
                .body())
                .get();
        assertTrue(formBuilderDocument.meta.containsKey("fillable_fields_count"), "Fillable fields meta is not added");

        List<Dictionary> formBuilderFields = Objects.requireNonNull(client.documents
                .getDocumentFields(formBuilderDocument.id)
                .execute()
                .body())
                .get();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(formBuilderDocument.name, newFormName, "Form name is not updated");
        softAssert.assertNotNull(formBuilderDocument.fields_file, "Fields file is not init");
        softAssert.assertEquals((int) formBuilderDocument.meta.get("fillable_fields_count"), 3, "Incorrect fillable fields count");

        assertEquals(formBuilderFields.size(), 3, "Incorrect field list size");
        softAssert.assertEquals(formBuilderFields.get(0).name, descriptionFieldName, "Incorrect description field name");
        softAssert.assertNull(formBuilderFields.get(0).value, "incorrect default description value");
        softAssert.assertEquals(formBuilderFields.get(0).field_type, TEXT, "Incorrect description field type");

        softAssert.assertEquals(formBuilderFields.get(1).name, textFieldName, "Incorrect text field name");
        softAssert.assertNull(formBuilderFields.get(1).value, "Incorrect text field default value");
        softAssert.assertEquals(formBuilderFields.get(1).field_type, TEXT, "Incorrect text field type");

        softAssert.assertEquals(formBuilderFields.get(2).name, commentFieldName, "Incorrect comment field name");
        softAssert.assertNull(formBuilderFields.get(2).value, "Incorrect comment field default value");
        softAssert.assertEquals(formBuilderFields.get(2).field_type, TEXT, "Incorrect comment field type");
        softAssert.assertAll();
    }

    @Story("Fill Slate by second user")
    @Test(priority = 2)
    public void secondUserFillSlateWithoutRequiredFields() throws IOException {

        SlatePermission body = new SlatePermission(SlatePermission.Type.LINK, SlatePermission.Access.CAN_FILL);
        List<SlatePermission> bodyL = Arrays.asList(body);
        client.slatesPermissions.updateFlowPermission(flowID, bodyL).execute().body();

        String secondUserToken = Objects.requireNonNull(client.auth
                .login(testData.emails.get(1), testData.password)
                .execute()
                .body())
                .access_token;

        Packet slate = Objects.requireNonNull(client.packets
                .flowsCreateBlankPacket(flowID, new Packet())
                .execute()
                .body())
                .get();

        slateID = slate.id;

        List<PacketRevision> packetRevisions = Objects.requireNonNull(client.packets
                .getRevisionsList(flowID, slateID)
                .execute()
                .body())
                .get();

        String userId = client.userManagement.getUserInfo(null).execute().body().get().id;
        client.userManagement.acceptTos(userId).execute();

        PacketRevision packetRevision = packetRevisions.stream().findFirst().get();

        fillDocument = new HTMLForm();
        fillDocument.id = packetRevision.documents.get(0).id;

        formBuilderClient.interceptors()
                .authenticator
                .setToken(secondUserToken);
        formBuilderClient.interceptors().asserter.disableOnce();

        int respCode = formBuilderClient.htmlFormBuilderService
                .fillDocument(fillDocument)
                .execute().code();

        String latestRevisionId = client.packets.flowsGetPacket(flowID, slateID).execute().body().get().latest_revisions.id;
        PacketRevision.Status revisionStatus = client.packets.getRevision(flowID, slateID, latestRevisionId).execute().body().get().status;

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(respCode, 500, "User who does't flow admin have finished document without filling required fields");
        softAssert.assertEquals(revisionStatus, PacketRevision.Status.FILLED, "Revision have active status");
        softAssert.assertAll();
    }

    @Story("Create slate with html form")
    @Test(priority = 1)
    public void createSlateWithForm() throws IOException {
        Objects.requireNonNull(client.packets
                .flowsCreateBlankPacket(flowID, new Packet())
                .execute()
                .body())
                .get();
        Packet packet = Objects.requireNonNull(client.packets
                .flowCreatePacket(flowID, new Packet())
                .execute()
                .body())
                .get();
        slateID = packet.id;
        List<PacketRevision> packetRevisions = Objects.requireNonNull(client.packets
                .getRevisionsList(flowID, slateID)
                .execute()
                .body())
                .get();

        String userId = client.userManagement.getUserInfo(null).execute().body().get().id;
        client.userManagement.acceptTos(userId).execute();

        assertTrue(packetRevisions.stream().findFirst().isPresent(), "FirstRevision is not created");

        PacketRevision packetRevision = packetRevisions.stream().findFirst().get();

        fillDocument = new HTMLForm();
        fillDocument.id = packetRevision.documents.get(0).id;
        Map<String, String> filedValues = new HashMap<>();
        filedValues.put(textFieldName, textFieldValue);
        String commentFieldValue = "12qwe";
        filedValues.put(commentFieldName, commentFieldValue);
        fillDocument.setJson(getFillJson(filedValues));
        Objects.requireNonNull(formBuilderClient.htmlFormBuilderService
                .fillDocument(fillDocument)
                .execute()
                .body())
                .get();
        Objects.requireNonNull(client.packets
                .flowsGetPacket(flowID, slateID)
                .execute()
                .body())
                .get();

        packet = client.packets.flowsFinishPacket(flowID, slateID).execute().body().get();
        new RetrofitWait<>(client.packets.flowsGetPacket(flowID, slateID))
                .withMessage("REVISION STATUS IS NOT FINISHED")
                .withTimeout(30000)
                .until(r -> {
                    if (r.body() != null) {
                        Packet executePacket = r.body().get();

                        return executePacket.finished_revisions_count.equals(executePacket.revisions_total);
                    } else {
                        return false;
                    }
                });
        packet = client.packets.flowsGetPacket(flowID, slateID).execute().body().get();
        List<Dictionary> formBuilderFields = Objects.requireNonNull(client.documents
                .getDocumentFields(fillDocument.id)
                .execute()
                .body())
                .get();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals((int) packet.finished_revisions_count, 1, "Revision is not finished");
        softAssert.assertEquals(formBuilderFields.size(), 3, "Incorrect fields size in filled document");
        softAssert.assertEquals(formBuilderFields.get(1).value, textFieldValue, "Incorrect value text field");
        softAssert.assertEquals(formBuilderFields.get(2).value, commentFieldValue, "Incorrect value comment field");
        softAssert.assertAll();
    }

    @Story("Change finished form")
    @Test(dependsOnMethods = "createSlateWithForm")
    public void tryToChangeFinishedRevision() throws IOException {
        formBuilderClient.interceptors()
                .authenticator
                .setToken(access_token);

        formBuilderClient.interceptors().asserter.disableOnce();
        Objects.requireNonNull(client.packets
                .flowsCreateRevision(flowID, slateID, new PacketRevision())
                .execute()
                .body());

        formBuilderClient.htmlFormBuilderService
                .getForm(fillDocument.id, true)
                .execute();

        Map<String, String> filedValues = new HashMap<>();
        String newTextFieldValue = "newTextFieldValue";
        filedValues.put(textFieldName, newTextFieldValue);
        fillDocument.setJson(getFillJson(filedValues));
        formBuilderClient.interceptors().asserter.disableOnce();
        int responseCode = formBuilderClient.htmlFormBuilderService
                .fillDocument(fillDocument)
                .execute()
                .code();

        assertEquals(responseCode, 403, "Response code isn't 403");
    }
}
