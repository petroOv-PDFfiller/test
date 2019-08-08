package api.salesforce.entities.airslate.bots;

import api.salesforce.entities.airslate.bots.ui_components.DataDefaultObject;
import api.salesforce.entities.airslate.bots.ui_components.DataMappingObject;
import api.salesforce.entities.airslate.bots.ui_components.DataMatchObject;
import api.salesforce.entities.airslate.bots.ui_components.DataTreeObject;
import com.airslate.api.models.addons.AddonEnum;
import com.airslate.api.models.addons.EventType;
import com.airslate.api.models.addons.integration.AddonIntegration;
import com.airslate.api.models.addons.settings.AddonSettings;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.List;

import static api.salesforce.entities.airslate.bots.SettingType.*;
import static java.util.Arrays.asList;

/**
 * Salesforce bots required settings here:
 *
 * @link https://github.com/airslateinc/salesforce-addons/tree/develop/docker/config
 */
public class AirSlateBotSettings implements AddonSettings {

    public List<Setting> settings;
    private EventType eventType;
    private AddonEnum addonName;
    private String eventSubtype;

    public AirSlateBotSettings() {
    }

    public AirSlateBotSettings(BotBuilder builder) {
        this.settings = builder.settings;
        this.eventType = builder.eventType;
        this.addonName = builder.addonName;
        this.eventSubtype = builder.eventSubtype;
    }

    public String getSettings() {
        try {
            return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .writeValueAsString(settings);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public EventType defaultEventType() {
        return eventType;
    }

    @Override
    public AddonEnum defaultAddonEnum() {
        return addonName;
    }

    @Override
    public String defaultEventSubType() {
        return eventSubtype;
    }

    public static class BotBuilder {

        private List<Setting> settings;
        private EventType eventType;
        private AddonEnum addonName;
        private String eventSubtype;

        public BotBuilder() {
            settings = new ArrayList<>();
        }

        public BotBuilder addSetting(Setting setting) {
            settings.add(setting);
            return this;
        }

        public BotBuilder addSetting(Object data, String name, String type) {
            settings.add(new Setting(data, name, type));
            return this;
        }

        public BotBuilder addIntegrationSetting(String name, AddonIntegration addonIntegration) {
            settings.add(new Setting(ImmutableMap.of("email", addonIntegration.email, "id", addonIntegration.id), name, INTEGRATION.getType()));
            return this;
        }

        public BotBuilder addChoiceSetting(String name, String choiceValue) {
            settings.add(new Setting(new DataDefaultObject(choiceValue, choiceValue), name, CHOICE.getType()));
            return this;
        }

        public BotBuilder addTreeSetting(String name, List<DataTreeObject> tree) {
            settings.add(new Setting(tree, name, TREE.getType()));
            return this;
        }

        public BotBuilder addMultipleChoiceSetting(String name, List<DataDefaultObject> multipleChoice) {
            settings.add(new Setting(multipleChoice, name, MULTIPLE_CHOICE.getType()));
            return this;
        }

        public BotBuilder addMultipleChoiceSetting(String name, String value) {
            settings.add(new Setting(asList(new DataDefaultObject(value, value)), name, MULTIPLE_CHOICE.getType()));
            return this;
        }

        public BotBuilder addMatchSetting(String name, List<DataMatchObject> matches) {
            settings.add(new Setting(ImmutableMap.of("matches", matches), name, MATCH.getType()));
            return this;
        }

        public BotBuilder addTagsSetting(List<String> tags) {
            settings.add(new Setting(tags, SettingName.TAGS.getName(), TAGS.getType()));
            return this;
        }

        public BotBuilder addMappingSetting(String name, List<DataMappingObject> mappingObject) {
            settings.add(new Setting(mappingObject, name, MAPPING.getType()));
            return this;
        }

        public BotBuilder addSetting(Object data, String type) {
            settings.add(new Setting(data, type));
            return this;
        }

        public BotBuilder eventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public BotBuilder botName(AddonEnum addonEnum) {
            this.addonName = addonEnum;
            return this;
        }

        public BotBuilder eventSubtype(String eventSubtype) {
            this.eventSubtype = eventSubtype;
            return this;
        }

        public AirSlateBotSettings build() {
            return new AirSlateBotSettings(this);
        }
    }
}
