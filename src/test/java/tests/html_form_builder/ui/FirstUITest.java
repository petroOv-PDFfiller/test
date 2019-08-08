package tests.html_form_builder.ui;

import base_tests.HTMLFormBuilderBaseTest;
import com.airslate.api.AirslateRestClient;
import com.airslate.api.models.documents.Document;
import com.airslate.api.models.onboarding.Organization;
import com.airslate.api.models.packets.Packet;
import com.airslate.api.models.packets.PacketRevision;
import com.airslate.api.models.slates.Slate;
import com.airslate.api.models.users.User;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.testng.SoftAsserts;
import io.qameta.allure.Step;
import listeners.SelenideScreenShooter;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.airslate.form_builder.components.ToolGroup;
import pages.airslate.form_builder.form_creator.FormCreatorEditor;
import pages.airslate.form_builder.form_creator.FormCreatorPage;
import utils.airslate.AirSlateApiMan;
import utils.airslate.FormBuilderMan;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.open;
import static org.testng.Assert.assertEquals;
import static pages.airslate.form_builder.enums.HtmlElement.UNDO;
import static utils.airslate.AirSlateApiMan.getAirSlateRestClient;

@Listeners({SoftAsserts.class, SelenideScreenShooter.class})
public class FirstUITest extends HTMLFormBuilderBaseTest {

    protected static File fileToUpload = new File("src/test/resources/uploaders/Constructor.pdf");
    private AirslateRestClient airslateRestClient = getAirSlateRestClient(config.url());
    private AirSlateApiMan airSlateApiMan = new AirSlateApiMan(airslateRestClient);
    private FormBuilderMan formBuilderMan = new FormBuilderMan(airslateRestClient);
    private User testUesr;
    private Organization organization;

    @BeforeClass
    public void setUp() throws IOException {
        testUesr = airSlateApiMan.createAirSlateAdmin(config.email());
        organization = airSlateApiMan.createOrganization(testUesr);
    }

    @Test
    public void sometTest() throws IOException {
        Slate flow = airSlateApiMan.createEmptyFlow(RandomStringUtils.randomAlphanumeric(10));
        Document formBuilderDocument = new Document();
        formBuilderDocument.type = "HTML_FORMS";
        formBuilderDocument.name = "documentName";
        formBuilderDocument = Objects.requireNonNull(airslateRestClient.documents
                .createDocument(formBuilderDocument)
                .execute()
                .body())
                .get();
        airSlateApiMan.addDocumentToFlow(flow, formBuilderDocument);
        Packet slate = airSlateApiMan.createBlankSlate(flow);
        PacketRevision packetRevision = airSlateApiMan.createNewRevision(flow, slate);
        formBuilderMan.autoLogin();
        open(formBuilderMan.getFormCreatorLink(flow, formBuilderDocument));
        FormCreatorPage formCreatorPage = new FormCreatorPage();
        FormCreatorEditor editor = formCreatorPage.getHtmlFormEditor();
        List<ToolGroup> toolGroupList = editor.toolsMenus().get(0).toolGroups();
        assertEquals(toolGroupList.size(), 5, "qewqwe");
    }

    @Test
    public void sometTest2() throws IOException {
        Slate flow = airSlateApiMan.createEmptyFlow(RandomStringUtils.randomAlphanumeric(10));
        Document formBuilderDocument = new Document();
        formBuilderDocument.type = "HTML_FORMS";
        formBuilderDocument.name = "documentName";
        formBuilderDocument = Objects.requireNonNull(airslateRestClient.documents
                .createDocument(formBuilderDocument)
                .execute()
                .body())
                .get();
        airSlateApiMan.addDocumentToFlow(flow, formBuilderDocument);
        Packet slate = airSlateApiMan.createBlankSlate(flow);
        PacketRevision packetRevision = airSlateApiMan.createNewRevision(flow, slate);
        formBuilderMan.autoLogin();
        open(formBuilderMan.getFormCreatorLink(flow, formBuilderDocument));
        FormCreatorPage formCreatorPage = new FormCreatorPage();
        FormCreatorEditor editor = formCreatorPage.getHtmlFormEditor();
        some(editor);

    }

    @Step
    public void some(FormCreatorEditor editor) {
        editor.formCreatorHeader.button(UNDO).shouldBe(Condition.visible).click();
    }
}
