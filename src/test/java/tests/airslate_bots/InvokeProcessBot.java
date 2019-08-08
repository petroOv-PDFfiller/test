package tests.airslate_bots;

import api.salesforce.entities.airslate.bots.AirSlateBotSettings;
import api.salesforce.entities.airslate.bots.ui_components.DataMappingObject;
import api.salesforce.entities.airslate.bots.ui_components.DataMatchObject;
import com.airslate.api.models.addons.EventType;
import com.airslate.api.models.addons.integration.AddonIntegration;
import com.airslate.api.models.documents.Dictionary;
import com.airslate.api.models.documents.Document;
import com.airslate.api.models.files.BaseFile;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import tests.salesforce.SalesforceAirSaleBotsBaseTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static api.salesforce.entities.SalesforceObject.OPPORTUNITY;
import static api.salesforce.entities.airslate.bots.SettingName.*;
import static com.airslate.api.models.addons.AddonEnum.INVOKE_SALESFORCE_PROCESS_ADD_ON_NAME;
import static data.salesforce.SalesforceTestData.SalesforceOpportunityStages.CLOSED;
import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;

@Feature("Invoke Salesforce process bot")
public class InvokeProcessBot extends SalesforceAirSaleBotsBaseTest {

    private String opportunityField;
    private String globalOpportunityId;

    @BeforeTest
    public void setUp() throws IOException, URISyntaxException {
        configurateSalesforceOrg();
        acceptAggrement(adminUser);
        globalOpportunityId = createOpportunityRecord();
        opportunityField = setUpInvokeProcessBot();
    }

    @Story("Invoke process: context run")
    @Test
    public void invokeBotContext() throws IOException, URISyntaxException {
        String opportunityId = createOpportunityRecord();
        createBlankFlowPacketFromContext(standardUser, opportunityId, OPPORTUNITY, stUserUsername, flow.id);
        Map<String, Object> recordFields = salesforceApi.recordsService().getRecordFields(OPPORTUNITY, opportunityId, asList(opportunityField));
        assertEquals(recordFields.get(opportunityField), CLOSED, "Process is not run, opportunity stage: ");
    }

    @Story("Invoke process: lookup run")
    @Test(priority = 1)
    public void invokeBotLookUp() throws IOException, URISyntaxException {
        createBlankSlate(flow);
        Map<String, Object> recordFields = salesforceApi.recordsService().getRecordFields(OPPORTUNITY, globalOpportunityId, asList(opportunityField));
        assertEquals(recordFields.get(opportunityField), CLOSED, "Process is not run, opportunity stage: ");
    }

    @Step
    private String setUpInvokeProcessBot() throws IOException {
        String salesforceFieldName = "Id";
        String stage = "StageName";

        flow = createEmptyFlow();
        BaseFile baseFile = new BaseFile(fileToUpload, "name.pdf");
        Document document = uploadDocument(baseFile);
        addDocumentToFlow(flow, document);
        addFillableFieldsToDocument(document, fileToUpload, adminUser, airSlateRestClient().interceptors().organizationDomain.getOrganizationDomain());
        flow = publishFlow(flow);
        document = airSlateApiMan.getDocument(document);
        List<Dictionary> fields = airSlateApiMan.getDocumentFields(document);
        Dictionary field = fields.get(0);
        field.value = globalOpportunityId;
        airSlateApiMan.updateDocumentField(document, field, 0);
        AddonIntegration addonIntegration = setUpConnection(INVOKE_SALESFORCE_PROCESS_ADD_ON_NAME);

        AirSlateBotSettings invokeProcessSettings = new AirSlateBotSettings.BotBuilder()
                .addIntegrationSetting(SALESFORCE.getName(), addonIntegration)
                .addChoiceSetting(OBJECT.getName(), OPPORTUNITY.getAPIName())
                .addMultipleChoiceSetting(PARENT_OBJECTS.getName(), "no_parents")
                .addMappingSetting(SELECT_CONDITIONS.getName(),
                        asList(new DataMappingObject.Builder()
                                .leftGroup(OPPORTUNITY)
                                .leftElement(salesforceFieldName)
                                .rightGroup(document)
                                .rightElement(fields.get(0))
                                .build()))
                .addMatchSetting(SET_VALUES.getName(),
                        asList(new DataMatchObject.Builder()
                                .what(stage)
                                .with(CLOSED)
                                .build()))
                .addTagsSetting(asList("Invoke Salesforce Process"))
                .eventType(EventType.POST_FINISH)
                .botName(INVOKE_SALESFORCE_PROCESS_ADD_ON_NAME)
                .build();

        installBotToFlow(flow, INVOKE_SALESFORCE_PROCESS_ADD_ON_NAME, invokeProcessSettings);
        return stage;
    }
}