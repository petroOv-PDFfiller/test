package tests.salesforce;

import api.salesforce.entities.SalesforceObject;
import api.salesforce.entities.airslate.bots.AirSlateBotSettings;
import api.salesforce.entities.airslate.bots.Setting;
import api.salesforce.entities.airslate.bots.salesforce_bots.*;
import api.salesforce.entities.airslate.bots.ui_components.DataDefaultObject;
import api.salesforce.entities.airslate.bots.ui_components.DataMappingObject;
import api.salesforce.responses.FindRecordsIDResponse;
import com.airslate.api.models.addons.AddonEnum;
import com.airslate.api.models.addons.integration.AddonIntegration;
import com.airslate.api.models.addons.integration.ServiceAccount;
import com.airslate.api.models.documents.Dictionary;
import com.airslate.api.models.documents.Document;
import com.airslate.api.models.files.BaseFile;
import com.airslate.api.models.packets.Packet;
import com.airslate.api.models.packets.PacketRevision;
import com.airslate.api.models.slates.Slate;
import com.airslate.api.models.users.User;
import com.google.common.collect.ImmutableMap;
import data.TestData;
import imap.ImapClient;
import io.qameta.allure.Step;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import utils.salesforce.BotsMan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static api.salesforce.entities.SalesforceObject.ACCOUNT;
import static api.salesforce.entities.airslate.bots.SettingType.CHOICE;
import static org.awaitility.Awaitility.await;

public class SalesforceAirSaleBotsBaseTest extends SalesforceAirSlateBaseTest {

    private static ThreadLocal<ServiceAccount> SERVICE_ACCOUNT = new ThreadLocal<>();
    protected BotsMan botsMan;
    protected Slate flow;
    protected String accountId;

    @Override
    protected void initApi() throws IOException, URISyntaxException {
        super.initApi();
        botsMan = new BotsMan(airSlateRestClient());
    }

    @Step
    protected void installBotToFlow(Slate flow, AddonEnum addonEnum, AirSlateBotSettings airSlateBotSettings, boolean skipOnFail) throws IOException {
        botsMan.installBotToFlow(flow, addonEnum, airSlateBotSettings, skipOnFail);
    }

    @Step
    protected void installBotToFlow(Slate flow, AddonEnum addonEnum, AirSlateBotSettings airSlateBotSettings) throws IOException {
        botsMan.installBotToFlow(flow, addonEnum, airSlateBotSettings);
    }

    protected SendASlateBotSettings getSendASlateBotSettings(AddonIntegration addonIntegration, SalesforceObject salesforceObject, List<DataDefaultObject> parentObjects, List<DataMappingObject> fieldMappings, DataDefaultObject fieldWithEmail) {
        return botsMan.getSendASlateBotSettings(addonIntegration, salesforceObject, parentObjects, fieldMappings, fieldWithEmail);
    }

    protected UploadBotSettings getUploadBotSettings(AddonIntegration addonIntegration, SalesforceObject salesforceObject, List<DataDefaultObject> parentObjects, List<DataMappingObject> fieldMappings) {
        return botsMan.getUploadBotSettings(addonIntegration, salesforceObject, parentObjects, fieldMappings);
    }

    protected CreateRecordBotSettings getCreateRecordBotSettings(AddonIntegration addonIntegration, SalesforceObject salesforceObject, List<DataDefaultObject> parentObjects, List<DataMappingObject> fieldMappings) {
        return botsMan.getCreateRecordBotSettings(addonIntegration, salesforceObject, parentObjects, fieldMappings);
    }

    protected UpdateRecordBotSettings getUpdateRecordBotSettings(AddonIntegration addonIntegration, SalesforceObject salesforceObject, List<DataDefaultObject> parentObjects, List<DataMappingObject> conditionMappings, List<DataMappingObject> fieldMappings) {
        return botsMan.getUpdateRecordBotSettings(addonIntegration, salesforceObject, parentObjects, conditionMappings, fieldMappings);
    }

    protected PrefillBotSettings getPrefillBotSettings(AddonIntegration addonIntegration, SalesforceObject salesforceObject, List<DataDefaultObject> childs, List<DataMappingObject> conditionMappings, List<DataMappingObject> fieldMappings) {
        return botsMan.getPrefillBotSettings(addonIntegration, salesforceObject, childs, conditionMappings, fieldMappings);
    }

    @Step
    protected String getSendASlateBotBody(String username, String flowName) {
        return botsMan.getSendASlateBotBody(username, flowName);
    }

    @Step("Get bot integration")
    protected AddonIntegration getAddonIntegration(Slate flow, AddonEnum addonName, ServiceAccount serviceAccount) throws IOException {
        return botsMan.getAddonIntegration(flow, addonName, serviceAccount);
    }

    @Step
    protected ServiceAccount getSalesforceServiceAccount(WebDriver driver) throws IOException {
        SERVICE_ACCOUNT.set(botsMan.getSalesforceServiceAccount(SERVICE_ACCOUNT.get(), driver, salesforceDefaultLogin, salesforceDefaultPassword, isDXOrg(), dxOwnerEmail));
        return SERVICE_ACCOUNT.get();
    }

    @Step
    protected Elements getSendASlateMessage(ImapClient imapClient) {
        return botsMan.getSendASlateMessage(imapClient);
    }

    @Step
    public String getFieldName(Dictionary field) {
        return botsMan.getFieldName(field);
    }

    @Step
    protected void finishRevision(Slate flow, Packet packet) throws IOException {
        airSlateApiMan.finishRevision(flow, packet);
    }

    @Step
    protected void updateDocumentField(Document document, Dictionary dictionary, int fieldNumber) throws IOException {
        airSlateApiMan.updateDocumentField(document, dictionary, fieldNumber);
    }

    @Step
    protected void acceptAggrement(User user) {
        airSlateApiMan.acceptAgreement(user);
    }

    @Step
    protected PacketRevision createNewRevision(Slate flow, Packet packet) throws IOException {
        return airSlateApiMan.createNewRevision(flow, packet);
    }

    @Step
    protected Packet createBlankSlate(Slate flow) throws IOException {
        return airSlateApiMan.createBlankSlate(flow);
    }

    @Step
    protected ServiceAccount getAuthorizedServiceAccount() throws IOException {
        SERVICE_ACCOUNT.set(botsMan.getAuthorizedServiceAccount(SERVICE_ACCOUNT.get(), getDriver(), salesforceDefaultLogin, salesforceDefaultPassword, isDXOrg(), dxOwnerEmail));
        return SERVICE_ACCOUNT.get();
    }

    @Step
    protected void addFillableFieldsToDocument(com.airslate.api.models.documents.Document document, File fileToUpload, User adminUser, String orgDomain) throws IOException {
        airSlateApiMan.addFillableFieldsToDocument(document, fileToUpload, adminUser, orgDomain);
    }

    @AfterSuite
    protected void cleanEmail() {
        ImapClient imapClient = new ImapClient(defaultAirSlateUserEmail, TestData.defaultPassword);
        imapClient.deleteAllMessages();
    }

    @Step
    protected Document setupFlow(String fieldWithEmail) throws IOException {
        return setupFlow(fieldWithEmail, fileToUpload, "name.pdf");
    }

    @Step
    protected Document setupFlow(String newAccountRecordName, File fileToUpload, String fileName) throws IOException {
        if (newAccountRecordName != null) {
            accountId = createAccountRecord(ImmutableMap.of(newAccountRecordName, defaultAirSlateUserEmail));
        }

        flow = createEmptyFlow();
        BaseFile baseFile = new BaseFile(fileToUpload, fileName);
        Document document = uploadDocument(baseFile);
        addDocumentToFlow(flow, document);
        addFillableFieldsToDocument(document, fileToUpload, adminUser, airSlateRestClient().interceptors().organizationDomain.getOrganizationDomain());
        flow = publishFlow(flow);
        document = airSlateApiMan.getDocument(document);

        return document;
    }

    @Step
    protected AddonIntegration setUpConnection(AddonEnum addonEnum) throws IOException {
        ServiceAccount serviceAccount = getAuthorizedServiceAccount();
        getDriver().quit();
        setActiveDriver(null);

        AddonIntegration addonIntegration = getAddonIntegration(flow, addonEnum, serviceAccount);
        return addonIntegration;
    }

    @Step
    protected void waitForAccount(String accountName, int count) {
        try {
            await().atMost(30, TimeUnit.SECONDS).pollInterval(5, TimeUnit.SECONDS).until(() -> getNumberOfAccounts(accountName) == count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Step
    protected int getNumberOfAccounts(String accountName) {
        String query = "SELECT id From " + ACCOUNT.getAPIName() + " Where Name = '" + accountName + "'";
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
    protected List<DataMappingObject> setUpMapping(SalesforceObject salesforceObject, String objectField, Document document, Dictionary documentField, boolean isObjectFirst) {
        Setting objectGroup = new Setting(new DataDefaultObject(salesforceObject.getObjectName(), salesforceObject.getAPIName()), CHOICE.getType());
        Setting documentGroup = new Setting(new DataDefaultObject(document.name, document.id), CHOICE.getType());

        List<DataMappingObject.Mapping> conditionMapping = new ArrayList<>();
        DataDefaultObject objElement = new DataDefaultObject(objectField, objectField);
        DataDefaultObject docElement = new DataDefaultObject(getFieldName(documentField), documentField.name);

        DataMappingObject.Mapping pair = new DataMappingObject.Mapping(
                new Setting(isObjectFirst ? objElement : docElement, CHOICE.getType()),
                new Setting(isObjectFirst ? docElement : objElement, CHOICE.getType()));
        conditionMapping.add(pair);

        DataMappingObject defaultCondition = new DataMappingObject();
        defaultCondition.leftGroup = isObjectFirst ? objectGroup : documentGroup;
        defaultCondition.rightGroup = isObjectFirst ? documentGroup : objectGroup;
        defaultCondition.mapping = conditionMapping;

        return new ArrayList<>(Collections.singletonList(defaultCondition));
    }
}
