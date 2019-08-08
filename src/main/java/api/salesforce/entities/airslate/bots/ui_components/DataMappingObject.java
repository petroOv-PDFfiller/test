package api.salesforce.entities.airslate.bots.ui_components;

import api.salesforce.entities.SalesforceObject;
import api.salesforce.entities.airslate.bots.Setting;
import com.airslate.api.models.documents.Dictionary;
import com.airslate.api.models.documents.Document;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static api.salesforce.entities.airslate.bots.SettingType.CHOICE;

public class DataMappingObject {

    @JsonProperty("left_group")
    public Setting leftGroup;
    @JsonProperty("mapping")
    public List<Mapping> mapping;
    @JsonProperty("right_group")
    public Setting rightGroup;

    public DataMappingObject() {
    }

    public DataMappingObject(Setting leftGroup, List<Mapping> mapping, Setting rightGroup) {
        this.leftGroup = leftGroup;
        this.mapping = mapping;
        this.rightGroup = rightGroup;
    }

    public DataMappingObject(Builder builder) {
        this.leftGroup = builder.leftGroup;
        this.mapping = builder.mapping;
        this.rightGroup = builder.rightGroup;
    }

    public static class Builder {
        private Setting leftGroup;
        private Setting rightGroup;
        private List<Mapping> mapping;

        public Builder() {
            mapping = new ArrayList<>();
            mapping.add(new Mapping());
        }

        public Builder leftGroup(Setting leftGroup) {
            this.leftGroup = leftGroup;
            return this;
        }

        public Builder leftGroup(Document document) {
            this.leftGroup = new Setting(new DataDefaultObject(document.name, document.id), CHOICE.getType());
            return this;
        }

        public Builder leftGroup(SalesforceObject object) {
            this.leftGroup = new Setting(new DataDefaultObject(object.getAPIName(), object.getAPIName()), CHOICE.getType());
            return this;
        }

        public Builder rightGroup(Setting rightGroup) {
            this.rightGroup = rightGroup;
            return this;
        }

        public Builder rightGroup(Document document) {
            this.rightGroup = new Setting(new DataDefaultObject(document.name, document.id), CHOICE.getType());
            return this;
        }

        public Builder rightGroup(SalesforceObject object) {
            this.rightGroup = new Setting(new DataDefaultObject(object.getAPIName(), object.getAPIName()), CHOICE.getType());
            return this;
        }

        public Builder leftElement(Setting leftElement) {
            mapping.get(0).leftElement = leftElement;
            return this;
        }

        public Builder rightElement(Setting rightElement) {
            mapping.get(0).rightElement = rightElement;
            return this;
        }

        public Builder leftElement(Dictionary leftElement) {
            mapping.get(0).leftElement = new Setting(new DataDefaultObject(leftElement.name, leftElement.name), CHOICE.getType());
            return this;
        }

        public Builder rightElement(Dictionary rightElement) {
            mapping.get(0).rightElement = new Setting(new DataDefaultObject(rightElement.name, rightElement.name), CHOICE.getType());
            return this;
        }

        public Builder leftElement(String fieldName) {
            mapping.get(0).leftElement = new Setting(new DataDefaultObject(fieldName, fieldName), CHOICE.getType());
            return this;
        }

        public Builder rightElement(String fieldName) {
            mapping.get(0).rightElement = new Setting(new DataDefaultObject(fieldName, fieldName), CHOICE.getType());
            return this;
        }

        public Builder mapping(Mapping mapping) {
            this.mapping.set(0, mapping);
            return this;
        }

        public DataMappingObject build() {
            return new DataMappingObject(this);
        }
    }

    public static class Mapping {

        @JsonProperty("left_element")
        public Setting leftElement;
        @JsonProperty("right_element")
        public Setting rightElement;

        public Mapping() {
        }

        public Mapping(Setting leftElement, Setting rightElement) {
            this.leftElement = leftElement;
            this.rightElement = rightElement;
        }
    }
}
