package tests.html_form_builder;

import api.airslate.AirSlateRequests;
import com.airslate.api.AirslateRestClient;
import com.airslate.api.apiclient.AirslateApi;
import com.airslate.api.apiclient.PacketsApi;
import com.airslate.api.apiclient.addonsInstaller.AddonInstaller;
import com.airslate.api.models.addons.AddonEnum;
import com.airslate.api.models.addons.SlateAddon;
import com.airslate.api.models.addons.settings.AddonSettingsPreFillLockFields;
import com.airslate.api.models.documents.Dictionary;
import com.airslate.api.models.documents.Document;
import com.airslate.api.models.packets.Packet;
import com.airslate.api.models.packets.PacketRevision;
import com.airslate.api.models.slates.Slate;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.thirdparty.html_form_builder.HTMLFormBuilderClient;
import com.thirdparty.html_form_builder.models.Element;
import com.thirdparty.html_form_builder.models.FormPages;
import com.thirdparty.html_form_builder.models.HTMLForm;
import com.thirdparty.html_form_builder.models.Page;
import data.TestData;
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

import static com.airslate.api.models.slates.Slate.SlateStatus.PUBLISHED;
import static data.salesforce.HTMLFormBuilderData.FieldType.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

@Feature("HTML form builder: addons")
@Listeners({WebTestListener.class})
public class FormBuilderAddonTests extends FormBuilderBaseTest {

    private AirslateRestClient client;
    private String flowID;
    private HTMLFormBuilderClient formBuilderClient;
    private String adminUserID;
    private String access_token;

    @Step
    @BeforeClass
    public void setUp() throws IOException {
        AirSlateRequests api = loginApi(testData.getEmail());
        adminUserID = api.users().userInfo().data.id;

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
        Slate slate = new Slate(flowName, flowName, true);
        flowID = Objects.requireNonNull(client.flows
                .createFlow(slate)
                .execute()
                .body())
                .get()
                .id;
    }

    @Story("Lock filed addon test")
    @Test(enabled = false)
    public void checkLockFieldAddonTest() throws IOException {
        Document formBuilderDocument = initFormBuilderDocumentData("HFBdoc" + StringMan.getRandomString(5));
        formBuilderDocument = Objects.requireNonNull(client.documents
                .createDocument(formBuilderDocument)
                .execute()
                .body())
                .get();

        List<Element> pageElements = new ArrayList<>();
        String descriptionFieldName = "usersTestDescqweqweqweqweqweqweq";
        pageElements.add(new Element(descriptionFieldName, HTML, false));
        String textFieldName = "usersTestText";
        pageElements.add(new Element(textFieldName, TEXT, true));
        String commentFieldName = "userTestCommentHfhewlkhfqewlkhwqlkrhwqlkrhqwlkhrlkwqhrlkqwhlrqwhwrlkh";
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

        Map<String, List<String>> allowed_users = new HashMap<>();
        String documentId = formBuilderDocument.id;
        List<Dictionary> dictionaries = Objects.requireNonNull(client.documents.getDocumentFields(documentId)
                .execute()
                .body())
                .get();
        allowed_users.put(documentId + ":" + dictionaries.get(2).name, Collections.singletonList(testData.getEmail()));

        AddonInstaller addonInstaller = new AddonInstaller(new AirslateApi(client), flowID, AddonEnum.INVOKE_SALESFORCE_PROCESS_ADD_ON_NAME);
        addonInstaller.installToSlate(new SlateAddon.Builder().settings(new AddonSettingsPreFillLockFields(allowed_users)).build());

        Slate slate = Objects.requireNonNull(client.flows
                .getFlow(flowID)
                .execute()
                .body())
                .get();
        slate.status = PUBLISHED;
        Objects.requireNonNull(client.flows
                .updateFlow(flowID, slate)
                .execute()
                .body())
                .get();

        access_token = Objects.requireNonNull(client.auth
                .login(testData.emails.get(1), TestData.defaultPassword)
                .execute()
                .body())
                .access_token;
        formBuilderClient.interceptors()
                .authenticator
                .setToken(access_token);
        slate = Objects.requireNonNull(client.flows
                .getFlow(flowID)
                .execute()
                .body())
                .get();
        Packet userPacket = Objects.requireNonNull(client.packets
                .createPacket(slate.id, new Packet())
                .execute()
                .body())
                .get();
        List<PacketRevision> packetRevisions = Objects.requireNonNull(client.packets
                .getRevisionsList(slate.id, userPacket.id)
                .execute()
                .body())
                .get();
        PacketRevision userPacketRevision = packetRevisions.get(0);
        String userDocumentID = userPacketRevision.documents.get(0).id;
        HTMLForm userForm = formBuilderClient.htmlFormBuilderService
                .getForm(userDocumentID, false)
                .execute()
                .body();
        FormPages formPages = Objects.requireNonNull(userForm).getJson();
        assertEquals(formPages.pages.get(0).elements.size(), 3, "Incorrect number of elements in form");
        Element readOnlyDocument = formPages
                .pages
                .get(0)
                .elements
                .get(2);
        Element anotherElement = formPages
                .pages
                .get(0)
                .elements
                .get(1);

        assertNull(anotherElement.allowedUsers, "Allowed users list size on unlocked field");
        assertEquals(readOnlyDocument.allowedUsers.size(), 1, "Incorrect allowed users array is set");
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(readOnlyDocument.readOnly, "Form comment field hasn't readOnly status");
        softAssert.assertFalse(anotherElement.readOnly, "Text field is read only");
        softAssert.assertEquals(readOnlyDocument.allowedUsers.get(0), adminUserID, "Incorrect user id in 'allowed users'");
        softAssert.assertAll();

        HTMLForm fillDocument = new HTMLForm();
        fillDocument.id = userDocumentID;
        Map<String, String> filedValues = new HashMap<>();
        String textFieldValue = "12";
        filedValues.put(textFieldName, textFieldValue);
        String commentFieldValue = "12qwe";
        filedValues.put(commentFieldName, commentFieldValue);
        fillDocument.setJson(getFillJson(filedValues));
        Objects.requireNonNull(formBuilderClient.htmlFormBuilderService
                .fillDocument(fillDocument)
                .execute()
                .body())
                .get();

        new PacketsApi(client).finishPacket(flowID, userPacket.id);
        List<Dictionary> formBuilderFields = Objects.requireNonNull(client.documents
                .getDocumentFields(fillDocument.id)
                .execute()
                .body())
                .get();
        assertNull(formBuilderFields.get(1).value, "User can set value to readOnly field");
    }
}
