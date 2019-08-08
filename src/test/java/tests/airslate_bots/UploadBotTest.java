package tests.airslate_bots;

import api.salesforce.entities.airslate.bots.salesforce_bots.UploadBotSettings;
import api.salesforce.entities.airslate.bots.ui_components.DataMappingObject;
import api.salesforce.responses.FindRecordsIDResponse;
import com.airslate.api.models.addons.integration.AddonIntegration;
import com.airslate.api.models.documents.Dictionary;
import com.airslate.api.models.documents.Document;
import com.airslate.api.models.packets.Packet;
import com.airslate.api.models.packets.PacketRevision;
import com.google.common.collect.ImmutableMap;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import tests.salesforce.SalesforceAirSaleBotsBaseTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static api.salesforce.entities.SalesforceObject.ACCOUNT;
import static api.salesforce.entities.SalesforceObject.ATTACHMENT;
import static com.airslate.api.models.addons.AddonEnum.EXPORT_TO_SALESFORCE;
import static org.awaitility.Awaitility.await;
import static org.testng.Assert.assertEquals;

@Feature("Upload bot")
public class UploadBotTest extends SalesforceAirSaleBotsBaseTest {

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        configurateSalesforceOrg();
        acceptAggrement(adminUser);

        String fieldWithEmail = setUpUploadBot();
        accountId = createAccountRecord(ImmutableMap.of(fieldWithEmail, defaultAirSlateUserEmail));
    }

    @Story("SenASlate bot: context run")
    @Test
    public void uploadBotContext() throws IOException {
        int attachmentsBeforeTest = getNumberOfAttachments(accountId);
        createBlankFlowPacketFromContext(standardUser, accountId, ACCOUNT, stUserUsername, flow.id);
        waitForAttachment(accountId, attachmentsBeforeTest + 1);
        assertEquals(getNumberOfAttachments(accountId), attachmentsBeforeTest + 1, "New Attachments is not uploaded");
    }

    @Story("Upload bot: lookup run")
    @Test(priority = 1)
    public void uploadBotLookUp() throws IOException {
        Packet packet = createBlankSlate(flow);

        PacketRevision packetRevision = createNewRevision(flow, packet);

        Document document = packetRevision.documents.get(0);
        Dictionary dictionary = airSlateApiMan.getDocumentFields(document)
                .get(0);
        dictionary.value = accountId;

        int attachmentsBeforeTest = getNumberOfAttachments(accountId);
        updateDocumentField(document, dictionary, 0);
        finishRevision(flow, packet);
        createNewRevision(flow, packet);
        waitForAttachment(accountId, attachmentsBeforeTest + 1);
        assertEquals(getNumberOfAttachments(accountId), attachmentsBeforeTest + 1, "New Attachments is not uploaded");
    }

    @NotNull
    private String setUpUploadBot() throws IOException {
        String fieldWithEmail = "Website";
        String conditionSFField = "Id";

        Document document = setupFlow(fieldWithEmail);
        AddonIntegration addonIntegration = setUpConnection(EXPORT_TO_SALESFORCE);

        List<Dictionary> fields = airSlateApiMan.getDocumentFields(document);
        Dictionary documentField = fields.get(0);

        List<DataMappingObject> conditionsMapping = setUpMapping(ACCOUNT, conditionSFField, document, documentField, true);
        UploadBotSettings uploadBotSettings = getUploadBotSettings(addonIntegration, ACCOUNT,
                null, conditionsMapping);

        installBotToFlow(flow, EXPORT_TO_SALESFORCE, uploadBotSettings);
        airSlateRestClient().packets.flowsCreateBlankPacket(flow.id, new Packet()).execute();
        return fieldWithEmail;
    }

    @Step
    private int getNumberOfAttachments(String recordId) {
        String query = "SELECT id From " + ATTACHMENT.getAPIName() + " Where parentId = '" + recordId + "'";
        FindRecordsIDResponse findRecordsIDResponse;
        try {
            findRecordsIDResponse = salesforceApi.recordsService().
                    executeQuery(query, FindRecordsIDResponse.class);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return 0;
        }
        return findRecordsIDResponse.totalSize;
    }

    @Step
    private void waitForAttachment(String recordId, int count) {
        try {
            await().atMost(2, TimeUnit.MINUTES).pollInterval(10, TimeUnit.SECONDS).until(() -> getNumberOfAttachments(recordId) == count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}