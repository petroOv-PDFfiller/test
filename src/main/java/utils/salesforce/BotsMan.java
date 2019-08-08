package utils.salesforce;

import api.salesforce.entities.SalesforceObject;
import api.salesforce.entities.airslate.bots.AirSlateBotSettings;
import api.salesforce.entities.airslate.bots.Setting;
import api.salesforce.entities.airslate.bots.SettingType;
import api.salesforce.entities.airslate.bots.salesforce_bots.*;
import api.salesforce.entities.airslate.bots.ui_components.DataDefaultObject;
import api.salesforce.entities.airslate.bots.ui_components.DataFieldMapping;
import api.salesforce.entities.airslate.bots.ui_components.DataMappingObject;
import com.airslate.api.AirslateRestClient;
import com.airslate.api.apiclient.AirslateApi;
import com.airslate.api.apiclient.addonsInstaller.AddonInstaller;
import com.airslate.api.models.addons.AddonEnum;
import com.airslate.api.models.addons.OrganizationAddon;
import com.airslate.api.models.addons.SlateAddon;
import com.airslate.api.models.addons.integration.AddonIntegration;
import com.airslate.api.models.addons.integration.Service;
import com.airslate.api.models.addons.integration.ServiceAccount;
import com.airslate.api.models.addons.integration.ServiceCodeEnum;
import com.airslate.api.models.documents.Dictionary;
import com.airslate.api.models.slates.Slate;
import com.airslate.api.util.RetrofitWait;
import com.google.common.collect.ImmutableMap;
import core.check.Check;
import imap.ImapClient;
import imap.With;
import io.qameta.allure.Step;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;

import javax.mail.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static api.salesforce.entities.airslate.bots.SettingName.NOTIFICATION;
import static api.salesforce.entities.airslate.bots.SettingName.TAGS;
import static api.salesforce.entities.airslate.bots.SettingName.*;
import static api.salesforce.entities.airslate.bots.SettingType.*;

public class BotsMan {

    public static final String sendASlateSalesforceBot = "SendASlateSalesforceBot";
    public static final String openTheSlate = "Open the Slate to check it out!";
    private AirslateRestClient airslateRestClient;

    public BotsMan(AirslateRestClient airslateRestClient) {
        this.airslateRestClient = airslateRestClient;
    }

    public void setAirslateRestClient(AirslateRestClient airslateRestClient) {
        this.airslateRestClient = airslateRestClient;
    }

    @Step
    public void installBotToFlow(Slate flow, AddonEnum addonEnum, AirSlateBotSettings airSlateBotSettings, boolean skipOnFail) throws IOException {
        SlateAddon slateAddon = new SlateAddon.Builder().settingsAsJson(airSlateBotSettings.getSettings(),
                airSlateBotSettings.defaultEventType(), airSlateBotSettings.defaultEventSubType()).build();
        slateAddon.skip_on_fail = skipOnFail;
        new AddonInstaller(new AirslateApi(airslateRestClient), flow.id, addonEnum)
                .installToSlate(slateAddon);
    }

    @Step("Install {addonEnum} bot to flow {flow.id}")
    public void installBotToFlow(Slate flow, AddonEnum addonEnum, AirSlateBotSettings airSlateBotSettings) throws IOException {
        installBotToFlow(flow, addonEnum, airSlateBotSettings, false);
    }

    public SendASlateBotSettings getSendASlateBotSettings(AddonIntegration addonIntegration, SalesforceObject salesforceObject, List<DataDefaultObject> parentObjects, List<DataMappingObject> fieldMappings, DataDefaultObject fieldWithEmail) {
        SendASlateBotSettings sendASlateBotSettings = new SendASlateBotSettings();
        List<Setting> settings = new ArrayList<>();
        setAddonIntegrationSetting(addonIntegration, settings);

        setObjectSetting(salesforceObject, settings);

        setParentObjectSetting(parentObjects, settings);

        settings.add(new Setting(fieldMappings, SELECT_CONDITIONS.getName(), MAPPING.getType()));

        settings.add(new Setting(fieldWithEmail, FIELD_WITH_EMAIL.getName(), CHOICE.getType()));

        settings.add(new Setting(ImmutableMap.of("body", getSendASlateBotBody("{user.name}",
                "{flow.name}"), "subject",
                sendASlateSalesforceBot),
                NOTIFICATION.getName(),
                SettingType.NOTIFICATION.getType()));

        addTags(settings, new ArrayList<>());

        sendASlateBotSettings.settings = settings;
        return sendASlateBotSettings;
    }

    public UploadBotSettings getUploadBotSettings(AddonIntegration addonIntegration, SalesforceObject salesforceObject, List<DataDefaultObject> parentObjects, List<DataMappingObject> conditionMapping) {
        UploadBotSettings uploadBotSettings = new UploadBotSettings();
        List<Setting> settings = new ArrayList<>();
        setAddonIntegrationSetting(addonIntegration, settings);

        setObjectSetting(salesforceObject, settings);

        setParentObjectSetting(parentObjects, settings);

        settings.add(new Setting(conditionMapping, FIELDS_MAP.getName(), MAPPING.getType()));

        settings.add(new Setting(new DataDefaultObject("Attachments", "attachment"), UPLOAD_TYPE.getName(), CHOICE.getType()));

        addTags(settings, new ArrayList<>());

        uploadBotSettings.settings = settings;
        return uploadBotSettings;
    }

    public CreateRecordBotSettings getCreateRecordBotSettings(AddonIntegration addonIntegration, SalesforceObject salesforceObject, List<DataDefaultObject> parentObjects, List<DataMappingObject> fieldMappings) {
        CreateRecordBotSettings createRecordBotSettings = new CreateRecordBotSettings();
        List<Setting> settings = new ArrayList<>();
        setAddonIntegrationSetting(addonIntegration, settings);

        setObjectSetting(salesforceObject, settings);

        setParentObjectSetting(parentObjects, settings);

        settings.add(new Setting(fieldMappings, FIELDS_MAP.getName(), MAPPING.getType()));

        addTags(settings, new ArrayList<>());

        createRecordBotSettings.settings = settings;
        return createRecordBotSettings;
    }

    public UpdateRecordBotSettings getUpdateRecordBotSettings(AddonIntegration addonIntegration, SalesforceObject salesforceObject, List<DataDefaultObject> parentObjects, List<DataMappingObject> conditionMapping, List<DataMappingObject> fieldMappings) {
        UpdateRecordBotSettings createRecordBotSettings = new UpdateRecordBotSettings();
        List<Setting> settings = new ArrayList<>();
        setAddonIntegrationSetting(addonIntegration, settings);

        setObjectSetting(salesforceObject, settings);

        setParentObjectEmptyLabelSetting(parentObjects, settings);

        settings.add(new Setting(conditionMapping, SELECT_CONDITIONS.getName(), MAPPING.getType()));

        settings.add(new Setting(fieldMappings, FIELDS_MAP.getName(), MAPPING.getType()));

        addTags(settings, new ArrayList<>());

        createRecordBotSettings.settings = settings;
        return createRecordBotSettings;
    }

    public PrefillBotSettings getPrefillBotSettings(AddonIntegration addonIntegration, SalesforceObject salesforceObject, List<DataDefaultObject> childs, List<DataMappingObject> conditionMapping, List<DataMappingObject> fieldMapping) {
        PrefillBotSettings prefillBotSettings = new PrefillBotSettings();
        List<Setting> settings = new ArrayList<>();
        setAddonIntegrationSetting(addonIntegration, settings);

        setObjectSetting(salesforceObject, settings);

        if (childs == null) {
            childs = new ArrayList<>();
        }
        settings.add(new Setting(Collections.singletonList(ImmutableMap.of("children", childs, "label", salesforceObject.getObjectName(), "value", salesforceObject.getAPIName())),
                RELATIONS.getName(), TREE.getType()));

        if (conditionMapping == null) {
            conditionMapping = new ArrayList<>();
        }
        settings.add(new Setting(conditionMapping, SELECT_CONDITIONS.getName(), MAPPING.getType()));
        settings.add(new Setting(new DataDefaultObject("", "single_line"), DATA_TYPE.getName(), CHOICE.getType()));

        settings.add(new Setting(fieldMapping, FIELDS_MAP.getName(), MAPPING.getType()));
        addTags(settings, new ArrayList<>());
        prefillBotSettings.settings = settings;
        return prefillBotSettings;
    }

    public void addTags(List<Setting> settings, List<String> tags) {
        settings.add(new Setting(tags, TAGS.getName(), SettingType.TAGS.getType()));
    }

    public void setFieldMappingSetting(List<DataFieldMapping> fieldMappings, List<Setting> settings, String name) {
        if (fieldMappings == null) {
            fieldMappings = new ArrayList<>();
        }
        settings.add(new Setting(fieldMappings, name, FIELDS_MAPPING.getType()));
    }

    public void setMappingSetting(List<DataMappingObject> mappingObjects, List<Setting> settings, String name) {
        if (mappingObjects == null) {
            mappingObjects = new ArrayList<>();
        }
        settings.add(new Setting(mappingObjects, name, MAPPING.getType()));
    }

    public void setParentObjectSetting(List<DataDefaultObject> parentObjects, List<Setting> settings) {
        if (parentObjects == null) {
            parentObjects = Collections.singletonList(new DataDefaultObject("No Parents", "no_parents"));
        }
        settings.add(new Setting(parentObjects, PARENT_OBJECTS.getName(), MULTIPLE_CHOICE.getType()));
    }

    public void setParentObjectEmptyLabelSetting(List<DataDefaultObject> parentObjects, List<Setting> settings) {
        if (parentObjects == null) {
            parentObjects = Collections.singletonList(new DataDefaultObject("", "no_parents"));
        }
        settings.add(new Setting(parentObjects, PARENT_OBJECTS.getName(), MULTIPLE_CHOICE.getType()));
    }

    public void setObjectSetting(SalesforceObject salesforceObject, List<Setting> settings) {
        settings.add(new Setting(new DataDefaultObject(salesforceObject.getObjectName(), salesforceObject.getAPIName()),
                OBJECT.getName(),
                CHOICE.getType()));
    }

    public void setAddonIntegrationSetting(AddonIntegration addonIntegration, List<Setting> settings) {
        settings.add(new Setting(ImmutableMap.of("email", addonIntegration.email, "id", addonIntegration.id),
                SALESFORCE.getName(),
                INTEGRATION.getType()));
    }

    @Step
    public String getSendASlateBotBody(String username, String flowName) {
        return String.format("Hi there, %s has sent you a Slate from the %s Flow. " + openTheSlate, username, flowName);
    }

    @Step("Get bot integration")
    public AddonIntegration getAddonIntegration(Slate flow, AddonEnum addonName, ServiceAccount serviceAccount) throws IOException {
        AddonIntegration addonIntegration = new AddonIntegration();
        addonIntegration.organizationAddon = new OrganizationAddon(new AddonInstaller(new AirslateApi(airslateRestClient), flow.id, addonName).installToOrganization().id);
        addonIntegration.serviceAccount = serviceAccount;
        addonIntegration.slate = new Slate(flow.id);
        addonIntegration = Objects.requireNonNull(airslateRestClient.integration
                .createAddonIntegration(addonIntegration)
                .execute()
                .body())
                .get();
        return addonIntegration;
    }

    @Step("Authorize connection")
    public void authorizeServiceAccount(ServiceAccount serviceAccount, WebDriver driver, String username, String password, boolean isDxOrg, String dxOwnerEmail) {
        new SalesforceMan().loginToSalesforce(username, password, driver, serviceAccount.authorization_url, isDxOrg, dxOwnerEmail);
    }

    @Step
    public ServiceAccount getSalesforceServiceAccount(ServiceAccount serviceAccount, WebDriver driver, String username, String password, boolean isDxOrg, String dxOwnerEmail) throws IOException {
        if (serviceAccount == null) {
            serviceAccount = new ServiceAccount();
            serviceAccount.service = getSalesforceService();
            String route = isDxOrg ? "sandbox" : "production";
            serviceAccount.options = serviceAccount.new Options(null, route);
            serviceAccount = Objects.requireNonNull(airslateRestClient.serviceAccounts
                    .createServiceAccount(serviceAccount)
                    .execute()
                    .body())
                    .get();
            authorizeServiceAccount(serviceAccount, driver, username, password, isDxOrg, dxOwnerEmail);
            new RetrofitWait<>(airslateRestClient.serviceAccounts
                    .getServiceAccount(serviceAccount.id))
                    .withMessage("Salesforce connection is not activated")
                    .withTimeout(10000)
                    .pollingEvery(1000)
                    .until(r -> Objects.requireNonNull(r.body()).get().account_name != null);
        }
        return Objects.requireNonNull(airslateRestClient.serviceAccounts
                .getServiceAccount(serviceAccount.id)
                .execute()
                .body())
                .get();
    }

    @Step
    public Service getSalesforceService() throws IOException {
        List<Service> services = Objects.requireNonNull(airslateRestClient.serviceAccounts
                .getServices(ServiceCodeEnum.SALESFORCE.name(), "1")
                .execute()
                .body())
                .get();
        return services.get(0);
    }

    @Step
    public Elements getSendASlateMessage(ImapClient imapClient) {
        List<Message> messages = imapClient.findMessages(With.subject(sendASlateSalesforceBot));
        Check.checkEquals(messages.size(), 1, "SendASlate message is not present");
        Document doc = Jsoup.parse(imapClient.getContent(messages.get(0)));
        Elements pinElement = doc.getElementsContainingOwnText(openTheSlate);

        Check.checkEquals(pinElement.size(), 1, "SendASlate message has incorrect text");
        return pinElement;
    }

    @Step
    public String getFieldName(Dictionary field) {
        return field.field_type.substring(0, 1).toUpperCase() + field.field_type.substring(1) + " Field: " + field.name;
    }

    public ServiceAccount getAuthorizedServiceAccount(ServiceAccount serviceAccount, WebDriver driver, String username, String password, boolean isDxOrg, String dxOwnerEmail) throws IOException {
        return getSalesforceServiceAccount(serviceAccount, driver, username, password, isDxOrg, dxOwnerEmail);
    }

    public List<DataFieldMapping> getFieldMappings(com.airslate.api.models.documents.Document document, Dictionary field) {
        List<DataFieldMapping> fieldMappings = new ArrayList<>();
        DataFieldMapping fieldMapping = new DataFieldMapping();
        fieldMapping.field = fieldMapping.new Field(getFieldName(field), document.id + ":" + field.name);
        fieldMapping.match = fieldMapping.new Match("Account ID [id]", "Id", CHOICE.getType());
        fieldMappings.add(fieldMapping);
        return fieldMappings;
    }
}