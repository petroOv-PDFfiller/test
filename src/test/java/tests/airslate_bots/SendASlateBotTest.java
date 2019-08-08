package tests.airslate_bots;

import api.salesforce.entities.airslate.bots.salesforce_bots.SendASlateBotSettings;
import api.salesforce.entities.airslate.bots.ui_components.DataDefaultObject;
import api.salesforce.entities.airslate.bots.ui_components.DataMappingObject;
import com.airslate.api.models.addons.integration.AddonIntegration;
import com.airslate.api.models.documents.Dictionary;
import com.airslate.api.models.documents.Document;
import com.airslate.api.models.packets.Packet;
import com.airslate.api.models.packets.PacketRevision;
import com.google.common.collect.ImmutableMap;
import data.TestData;
import imap.ImapClient;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import tests.salesforce.SalesforceAirSaleBotsBaseTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static api.salesforce.entities.SalesforceObject.ACCOUNT;
import static com.airslate.api.models.addons.AddonEnum.SEND_A_SLATE_TO_SALESFORCE_CONTACT;
import static org.testng.Assert.assertEquals;
import static utils.salesforce.BotsMan.sendASlateSalesforceBot;

@Feature("SenASlate bot")
public class SendASlateBotTest extends SalesforceAirSaleBotsBaseTest {

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        configurateSalesforceOrg();
        acceptAggrement(adminUser);

        String fieldWithEmail = setUpSendASlateBot();
        accountId = createAccountRecord(ImmutableMap.of(fieldWithEmail, defaultAirSlateUserEmail));
    }

    @Story("SenASlate bot: context run")
    @Test
    public void sendASlateBotContext() throws IOException {
        ImapClient imapClient = new ImapClient(defaultAirSlateUserEmail, TestData.defaultPassword);
        imapClient.deleteAllMessagesWithSubject(sendASlateSalesforceBot);
        createBlankFlowPacketFromContext(standardUser, accountId, ACCOUNT, stUserUsername, flow.id);
        String sendASlateMessage = getSendASlateMessage(imapClient).get(0).text();
        assertEquals(sendASlateMessage, getSendASlateBotBody(standardUser.first_name + " " + standardUser.last_name, flow.name),
                "Incorrect SendASLate bot email message text");
    }

    @Story("SenASlate bot: lookup run")
    @Test(priority = 1)
    public void sendASlateBotLookUp() throws IOException {
        Packet packet = createBlankSlate(flow);

        ImapClient imapClient = new ImapClient(defaultAirSlateUserEmail, TestData.defaultPassword);
        imapClient.deleteAllMessagesWithSubject(sendASlateSalesforceBot);

        PacketRevision packetRevision = createNewRevision(flow, packet);
        Document document = packetRevision.documents.get(0);
        Dictionary dictionary = airSlateApiMan.getDocumentFields(document)
                .get(0);
        dictionary.value = accountId;
        updateDocumentField(document, dictionary, 0);
        finishRevision(flow, packet);
        createNewRevision(flow, packet);

        String sendASlateMessage = getSendASlateMessage(imapClient).get(0).text();
        assertEquals(sendASlateMessage, getSendASlateBotBody(adminUser.first_name + " " + adminUser.last_name, flow.name),
                "Incorrect SendASLate bot email message text");
    }

    @Step
    private String setUpSendASlateBot() throws IOException {
        String fieldWithEmail = "Website";
        String conditionSFField = "Id";

        Document document = setupFlow(fieldWithEmail);
        AddonIntegration addonIntegration = setUpConnection(SEND_A_SLATE_TO_SALESFORCE_CONTACT);

        List<Dictionary> fields = airSlateApiMan.getDocumentFields(document);
        Dictionary field = fields.get(0);

        List<DataMappingObject> conditionsMapping = setUpMapping(ACCOUNT, conditionSFField, document, field, true);

        SendASlateBotSettings sendASlateBotSettings = getSendASlateBotSettings(addonIntegration, ACCOUNT,
                null, conditionsMapping, new DataDefaultObject(fieldWithEmail + " [url]", fieldWithEmail));
        installBotToFlow(flow, SEND_A_SLATE_TO_SALESFORCE_CONTACT, sendASlateBotSettings);
        airSlateRestClient().packets.flowsCreateBlankPacket(flow.id, new Packet()).execute();
        return fieldWithEmail;
    }
}