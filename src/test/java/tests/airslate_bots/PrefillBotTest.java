package tests.airslate_bots;

import api.salesforce.entities.airslate.bots.salesforce_bots.PrefillBotSettings;
import api.salesforce.entities.airslate.bots.ui_components.DataMappingObject;
import com.airslate.api.models.addons.integration.AddonIntegration;
import com.airslate.api.models.documents.Dictionary;
import com.airslate.api.models.documents.Document;
import com.airslate.api.models.packets.Packet;
import com.airslate.api.models.packets.PacketRevision;
import com.google.common.collect.ImmutableMap;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import tests.salesforce.SalesforceAirSaleBotsBaseTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static api.salesforce.entities.SalesforceObject.ACCOUNT;
import static com.airslate.api.models.addons.AddonEnum.PRE_FILL_FROM_SALESFORCE_RECORD;
import static org.testng.Assert.assertEquals;

@Feature("Prefill bot")
public class PrefillBotTest extends SalesforceAirSaleBotsBaseTest {

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        configurateSalesforceOrg();
        acceptAggrement(adminUser);
        String fieldWithEmail = setUpPrefillBot();
        accountId = createAccountRecord(ImmutableMap.of(fieldWithEmail, defaultAirSlateUserEmail));
    }

    @Story("Prefill bot: context run")
    @Test
    public void prefillBotContext() throws IOException {
        Packet slate = createBlankFlowPacketFromContext(standardUser, accountId, ACCOUNT, stUserUsername, flow.id);
        PacketRevision last = airSlateApiMan.getLatestRevisions(flow, slate);
        last = airSlateApiMan.getRevision(flow, slate, last);
        Dictionary dictionary = airSlateApiMan.getDocumentFields(last.documents.get(0))
                .get(1);
        assertEquals(dictionary.value, defaultAirSlateUserEmail, "Incorrect email prefilled");
    }

    @Story("Prefill bot: lookup run")
    @Test(priority = 1)
    public void prefillBotLookUp() throws IOException {
        Packet packet = createBlankSlate(flow);
        PacketRevision packetRevision = createNewRevision(flow, packet);

        Document document = packetRevision.documents.get(0);
        Dictionary dictionary = airSlateApiMan.getDocumentFields(document)
                .get(0);
        dictionary.value = accountId;
        updateDocumentField(document, dictionary, 0);
        finishRevision(flow, packet);
        packetRevision = createNewRevision(flow, packet);
        dictionary = airSlateApiMan.getDocumentFields(packetRevision.documents.get(0))
                .get(1);

        assertEquals(dictionary.value, defaultAirSlateUserEmail, "Incorrect email prefilled");
    }

    @NotNull
    private String setUpPrefillBot() throws IOException {
        String conditionSFField = "Id";
        String mappingSFField = "Website";

        Document document = setupFlow(mappingSFField);
        AddonIntegration addonIntegration = setUpConnection(PRE_FILL_FROM_SALESFORCE_RECORD);

        List<Dictionary> documentFields = airSlateApiMan.getDocumentFields(document);
        Dictionary documentField = documentFields.get(0);

        List<DataMappingObject> conditionsMapping = setUpMapping(ACCOUNT, conditionSFField, document, documentField, true);

        documentField = documentFields.get(1);
        List<DataMappingObject> fieldsMapping = setUpMapping(ACCOUNT, mappingSFField, document, documentField, true);

        PrefillBotSettings prefillBotSettings = getPrefillBotSettings(addonIntegration, ACCOUNT, null, conditionsMapping, fieldsMapping);
        installBotToFlow(flow, PRE_FILL_FROM_SALESFORCE_RECORD, prefillBotSettings);
        airSlateRestClient().packets.flowsCreateBlankPacket(flow.id, new Packet()).execute();
        return mappingSFField;
    }
}