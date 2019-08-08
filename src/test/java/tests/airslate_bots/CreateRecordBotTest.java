package tests.airslate_bots;

import api.salesforce.entities.airslate.bots.salesforce_bots.CreateRecordBotSettings;
import api.salesforce.entities.airslate.bots.ui_components.DataMappingObject;
import com.airslate.api.models.addons.integration.AddonIntegration;
import com.airslate.api.models.documents.Dictionary;
import com.airslate.api.models.documents.Document;
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
import static com.airslate.api.models.addons.AddonEnum.CREATE_SALESFORCE_RECORD;
import static org.testng.Assert.assertEquals;

@Feature("Create record bot")
public class CreateRecordBotTest extends SalesforceAirSaleBotsBaseTest {

    private String accountName = getUniqueRecordName(ACCOUNT);

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        configurateSalesforceOrg();
        acceptAggrement(adminUser);

        setUpCreateRecordBot();
    }

    @Story("Create record bot: context run")
    @Test
    public void createBotContext() throws IOException {
        int accountsBefore = getNumberOfAccounts(accountName);
        createBlankFlowPacketFromContext(standardUser, accountId, ACCOUNT, stUserUsername, flow.id);
        waitForAccount(accountName, accountsBefore + 1);
        assertEquals(getNumberOfAccounts(accountName), accountsBefore + 1, "New Account is not created: ");
    }

    @Story("Create record bot: lookup run")
    @Test(priority = 1)
    public void createBotLookUp() throws IOException {
        int accountsBefore = getNumberOfAccounts(accountName);
        createBlankSlate(flow);
        waitForAccount(accountName, accountsBefore + 1);
        assertEquals(getNumberOfAccounts(accountName), accountsBefore + 1, "New Account is not uploaded");
    }

    @NotNull
    private String setUpCreateRecordBot() throws IOException {
        String mappingSFField = "Name";
        Document document = setupFlow(mappingSFField);
        AddonIntegration addonIntegration = setUpConnection(CREATE_SALESFORCE_RECORD);

        List<Dictionary> fields = airSlateApiMan.getDocumentFields(document);
        Dictionary documentField = fields.get(0);
        documentField.value = accountName;
        airSlateApiMan.updateDocumentField(document, documentField, 0);

        List<DataMappingObject> mappingObjects = setUpMapping(ACCOUNT, mappingSFField, document, documentField, false);

        CreateRecordBotSettings createBotSettings = getCreateRecordBotSettings(addonIntegration, ACCOUNT, null, mappingObjects);
        installBotToFlow(flow, CREATE_SALESFORCE_RECORD, createBotSettings);

        return mappingSFField;
    }
}