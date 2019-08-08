package api.salesforce.entities.airslate.bots.ui_components;

import api.salesforce.entities.airslate.bots.Setting;

import static api.salesforce.entities.airslate.bots.SettingType.CHOICE;
import static api.salesforce.entities.airslate.bots.SettingType.INPUT;

public class DataMatchObject {

    public Setting what;
    public Setting with;

    public DataMatchObject() {
    }

    public DataMatchObject(Builder builder) {
        this.what = builder.what;
        this.with = builder.with;
    }

    public static class Builder {

        private Setting what;
        private Setting with;

        public Builder() {

        }

        public Builder what(Setting setting) {
            what = setting;
            return this;
        }

        public Builder what(String fieldName) {
            what = new Setting(new DataDefaultObject(fieldName, fieldName), CHOICE.getType());
            return this;
        }

        public Builder with(String text) {
            with = new Setting(text, INPUT.getType());
            return this;
        }

        public DataMatchObject build() {
            return new DataMatchObject(this);
        }
    }
}
