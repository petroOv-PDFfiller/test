package api.salesforce.entities.airslate.bots;

public enum SettingName {

    SALESFORCE,
    OBJECT,
    PARENT_OBJECTS,
    SELECT_CONDITIONS,
    FIELD_WITH_EMAIL,
    NOTIFICATION,
    RELATIONS,
    DATA_TYPE,
    FIELDS_MAP,
    TAGS,
    UPLOAD_TYPE,
    SET_VALUES,
    CREATE_MAPPING;

    public String getName() {
        return name().toLowerCase();
    }
}
