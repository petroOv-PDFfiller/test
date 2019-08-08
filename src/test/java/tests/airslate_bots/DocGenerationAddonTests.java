package tests.airslate_bots;

import com.airslate.api.AirslateRestClient;
import com.airslate.api.apiclient.AirslateApi;
import com.airslate.api.apiclient.addonsInstaller.AddonInstaller;
import com.airslate.api.models.addons.*;
import com.airslate.api.models.addons.resources.SlateAddonVariant;
import com.airslate.api.models.addons.settings.AddonSettingsPreFillFromCsv;
import com.airslate.api.models.documents.Dictionary;
import com.airslate.api.models.documents.Document;
import com.airslate.api.models.editor.EditorLinkResp;
import com.airslate.api.models.files.Attachment;
import com.airslate.api.models.files.BaseFile;
import com.airslate.api.models.packets.Packet;
import com.airslate.api.models.packets.PacketRevision;
import com.airslate.api.models.slates.Slate;
import com.airslate.api.util.RetrofitWait;
import com.airslate.ws.EditorWsClient;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.common.collect.ImmutableMap;
import core.AssertionException;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import listeners.WebTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import tests.AirSlateBaseTest;
import utils.StringMan;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Feature("Doc generation addon")
@Listeners({WebTestListener.class})
public class DocGenerationAddonTests extends AirSlateBaseTest {

    private AirslateRestClient client;
    private Slate slate;
    private File fileToUpload = new File("src/test/resources/uploaders/docGenerator.docx");

    @BeforeMethod
    public void setUp() throws IOException {
        client = new AirslateRestClient(testData.apiUrl);
        client.interceptors()
                .organizationDomain
                .setOrganizationDomain(orgDomain);
        client.auth
                .login(testData.getEmail(), testData.password)
                .execute();
        client.setDeserializationFeature(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    @Story("Doc generation + csv prefill")
    public void docGenerationBasicTest() throws IOException, URISyntaxException, InterruptedException {
        String flowName = getFlowName();
        slate = new Slate(flowName, flowName, false);
        slate = requireNonNull(client.flows
                .createFlow(slate)
                .execute()
                .body())
                .get();
        BaseFile baseFile = new BaseFile(fileToUpload, "doc.docx", "DOC_GENERATION");
        String documentId = requireNonNull(client.documents
                .uploadDocument(baseFile)
                .execute()
                .body())
                .get()
                .id;
        Document document = requireNonNull(client.documents
                .getDocument(documentId, null)
                .execute()
                .body())
                .get();
        client.flows
                .addDocumentsToFlow(slate.id, slate.template.id, Collections.singletonList(document))
                .execute();

        addCsvPreFillAddon(documentId, document);
        addDocumentGeneratorAddon(documentId);

        Packet packet = requireNonNull(client.packets
                .flowsCreateBlankPacket(slate.id, new Packet())
                .execute()
                .body())
                .get();
        new RetrofitWait<>(client.packets.getRevision(slate.id, packet.id, packet.latest_revisions.id))
                .withTimeout(30000)
                .withMessage("packet.status != FINISHED")
                .until(r -> requireNonNull(r.body()).get().status == PacketRevision.Status.FINISHED);

        PacketRevision packetRevision = requireNonNull(client.packets.
                createRevision(slate.id, packet.id, new PacketRevision())
                .execute()
                .body())
                .get();
        new RetrofitWait<>(client.packets.getRevision(slate.id, packet.id, packetRevision.id))
                .withTimeout(40000)
                .withMessage("packet.status != DRAFT")
                .until(r -> requireNonNull(r.body()).get().status == PacketRevision.Status.DRAFT);

        Dictionary dictionary = requireNonNull(client.documents
                .getDocumentFields(packetRevision.documents.get(0).id)
                .execute()
                .body())
                .get()
                .get(0);
        dictionary.value = "Jon";
        client.documents
                .updateDocumentFields(packetRevision.documents.get(0).id, Collections.singletonList(dictionary))
                .execute();
        new RetrofitWait<>(client.documents.getDocumentFields(packetRevision.documents.get(0).id))
                .withTimeout(20000)
                .ignoring(NullPointerException.class)
                .until(r -> requireNonNull(r.body()).get().get(0).value.equals("Jon"));
        new AirslateApi(client)
                .events
                .executePostFinish(slate.id, packet.id);
        new RetrofitWait<>(client.packets.getPacket(slate.id, packet.id))
                .withMessage("REVISION STATUS NOT FINISHED")
                .until(r -> requireNonNull(r.body()).get()
                        .revision_status == Packet.Status.FINISHED);

        packetRevision = requireNonNull(client.packets
                .createRevision(slate.id, packet.id, new PacketRevision())
                .execute()
                .body())
                .get();
        new RetrofitWait<>(client.packets.getRevision(slate.id, packet.id, packetRevision.id))
                .withTimeout(10000)
                .withMessage("packet.status != DRAFT")
                .until(r -> requireNonNull(r.body()).get().status == PacketRevision.Status.INIT);
        waitPreFillDocument(packetRevision.documents.get(0).id);
        Dictionary resultField = requireNonNull(client.documents
                .getDocumentFields(packetRevision.documents.get(0).id)
                .execute()
                .body())
                .get()
                .get(1);
        Assert.assertEquals(resultField.value, "jon.snow@pdffiller.com", "FIELD NOT PRE FILLED");
    }

    @AfterMethod
    public void deleteFlow() {
        super.tearDown();
        try {
            client.interceptors().asserter.disableOnce();
            client.flows.deleteFlow(slate.id).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Step
    private void addCsvPreFillAddon(String documentId, Document document) throws IOException {
        AddonInstaller addonInstaller = new AddonInstaller(new AirslateApi(client), slate.id, AddonEnum.PRE_FILL_FROM_CSV);
        addonInstaller.uploadAddonFile(new File("src/test/resources/uploaders/CsvFile.csv"), "CsvFile.csv");

        List<SlateAddonVariant> record_mapping_variants = addonInstaller.getResources()
                .getResourceBySettingNameWithAttachment("record_mapping");
        List<SlateAddonVariant> fields_mapping_variants = addonInstaller.getResources()
                .getResourceBySettingNameWithAttachment("fields_mapping");

        List<Dictionary> docFields = requireNonNull(client.documents
                .getDocumentFields(documentId)
                .execute()
                .body())
                .get();
        Map<String, String> record_mapping = ImmutableMap.of(document.id + ":" + docFields.get(0).name,
                record_mapping_variants.get(0).name);
        Map<String, String> fields_mapping = ImmutableMap.of(document.id + ":" + docFields.get(1).name,
                fields_mapping_variants.get(2).name);
        addonInstaller.installToSlate(
                new SlateAddon.Builder().settings(new AddonSettingsPreFillFromCsv.Builder()
                        .attachment(new Attachment(addonInstaller.getAddonFile()))
                        .recordMapping(record_mapping)
                        .fieldsMapping(fields_mapping)
                        .build()
                ).build()
        );
    }

    @Step
    private void addDocumentGeneratorAddon(String documentId) throws IOException {
        List<OrganizationAddon> addons = requireNonNull(client.addons
                .getOrganizationAddons()
                .execute()
                .body())
                .get()
                .stream()
                .filter(addon -> (addon.addon.name.equals("DOCUMENT_GENERATION_ADD_ON_NAME")))
                .collect(Collectors.toList());
        OrganizationAddon docGenAddon;
        if (addons.size() > 0) {
            docGenAddon = addons.get(0);
        } else {
            List<Addon> allAddons = requireNonNull(client.addons
                    .getAddonsAll()
                    .execute()
                    .body())
                    .get();
            String addonId = allAddons.stream()
                    .filter(addon -> (addon.name.equals("DOCUMENT_GENERATION_ADD_ON_NAME")))
                    .findFirst()
                    .orElseThrow(() -> new AssertionException("Cannot find doc gen addon"))
                    .id;
            docGenAddon = requireNonNull(client.addons
                    .createOrganizationAddon(new OrganizationAddon(new Addon(addonId)))
                    .execute()
                    .body())
                    .get();
        }
        Map<String, String> settings = ImmutableMap.of("documents", documentId);
        SlateAddon slateAddon = new SlateAddon.Builder()
                .name("DOCUMENT_GENERATION_ADD_ON_DESCRIPTION")
                .settingsAsMap(settings, EventType.PRE_FILL, "")
                .build();
        slateAddon.organizationAddon = new OrganizationAddon(docGenAddon.id);
        slateAddon.slate = slate;
        requireNonNull(client.addons
                .createSlateAddon(slateAddon)
                .execute()
                .body())
                .get();
    }

    private String getFlowName() {
        return "DocGen" + StringMan.getRandomString(5);
    }

    @Step
    private void waitPreFillDocument(String documentId) throws IOException, URISyntaxException, InterruptedException {
        EditorLinkResp editorLinkResp = this.client.editor.getEditorLink(documentId).execute().body();
        EditorWsClient wsClient = new EditorWsClient(new URI(wsUrl));
        wsClient.authorize(this.client.interceptors().authenticator.getToken(),
                requireNonNull(editorLinkResp).projectId,
                editorLinkResp.viewerId,
                this.client.interceptors().organizationDomain.getOrganizationDomain());
        wsClient.waitResponse(s -> s.contains("\"onStart\":[{\"action\""), 60000, "Fillable Fields NOT LOADED");
        wsClient.close();
    }
}
