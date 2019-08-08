package tests.airslate_bots;

import api.salesforce.entities.airslate.SetupWizardStage;
import api.salesforce.entities.airslate.bots.salesforce_bots.PrefillBotSettings;
import api.salesforce.entities.airslate.bots.salesforce_bots.UpdateRecordBotSettings;
import api.salesforce.entities.airslate.bots.ui_components.DataMappingObject;
import com.airslate.api.models.addons.integration.AddonIntegration;
import com.airslate.api.models.documents.Dictionary;
import com.airslate.api.models.documents.Document;
import com.airslate.api.models.onboarding.Organization;
import com.airslate.api.models.packets.Packet;
import com.airslate.api.models.packets.PacketRevision;
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
import static com.airslate.api.models.addons.AddonEnum.PRE_FILL_FROM_SALESFORCE_RECORD;
import static com.airslate.api.models.addons.AddonEnum.UPDATE_SALESFORCE_RECORD;
import static org.testng.Assert.assertEquals;

@Feature("Update bot")
public class UpdateRecordBotTest extends SalesforceAirSaleBotsBaseTest {

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        adminUser = createAirSlateAdmin();
        acceptAggrement(adminUser);
        standardUser = createAirSlateUser();

        Organization organization = createOrganization(adminUser);
        connectUserToOrganization(organization, standardUser);

        connectAdmin(adminUser.email);
        setSetupWizardStage(SetupWizardStage.Stage.FINAL);
        connectWorkspace(organization);

        Document document = setupFlow(null);
        AddonIntegration addonIntegration = getAddonIntegration(flow, UPDATE_SALESFORCE_RECORD, getAuthorizedServiceAccount());
        List<Dictionary> fields = airSlateApiMan.getDocumentFields(document);

        List<DataMappingObject> fieldCondition = setUpMapping(ACCOUNT, "Id", document, fields.get(0), true);
        List<DataMappingObject> fieldMappings = setUpMapping(ACCOUNT, "Name", document, fields.get(0), false);
        UpdateRecordBotSettings updateRecordBotSettings = getUpdateRecordBotSettings(addonIntegration, ACCOUNT, null, fieldCondition, fieldMappings);

        installBotToFlow(flow, UPDATE_SALESFORCE_RECORD, updateRecordBotSettings);
        prefillFromSalesforce(document, fields);
        airSlateRestClient().packets.flowsCreateBlankPacket(flow.id, new Packet()).execute();
        accountId = createAccountRecord(null);
    }

    @Story("Update bot: context run")
    @Test
    public void updateBotContext() throws IOException {
        int accountsBefore = getNumberOfAccounts(accountId);
        createBlankFlowPacketFromContext(standardUser, accountId, ACCOUNT, stUserUsername, flow.id);
        waitForAccount(accountId, accountsBefore + 1);
        assertEquals(getNumberOfAccounts(accountId), accountsBefore + 1, "New Account is not uploaded");
    }

    @Story("Update bot: lookup run")
    @Test(priority = 1)
    public void updateBotLookUp() throws IOException {
        accountId = createAccountRecord(null);
        Packet packet = createBlankSlate(flow);

        PacketRevision packetRevision = createNewRevision(flow, packet);

        Document document = packetRevision.documents.get(0);
        Dictionary dictionary = airSlateApiMan.getDocumentFields(document)
                .get(0);
        dictionary.value = accountId;
        int accountsBefore = getNumberOfAccounts(accountId);
        updateDocumentField(document, dictionary, 0);
        finishRevision(flow, packet);
        createNewRevision(flow, packet);
        assertEquals(getNumberOfAccounts(accountId), accountsBefore + 1, "New Account is not uploaded");
    }

    @Step
    private void prefillFromSalesforce(Document document, List<Dictionary> fields) throws IOException {
        AddonIntegration addonIntegration = setUpConnection(PRE_FILL_FROM_SALESFORCE_RECORD);
        Dictionary field = fields.get(0);

        List<DataMappingObject> conditionsMapping = setUpMapping(ACCOUNT, "Id", document, field, true);
        List<DataMappingObject> fieldsMapping = setUpMapping(ACCOUNT, "Id", document, field, true);

        PrefillBotSettings prefillBotSettings = getPrefillBotSettings(addonIntegration, ACCOUNT, null, conditionsMapping, fieldsMapping);
        installBotToFlow(flow, PRE_FILL_FROM_SALESFORCE_RECORD, prefillBotSettings);
    }

}