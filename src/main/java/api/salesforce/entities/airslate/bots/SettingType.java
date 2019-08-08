package api.salesforce.entities.airslate.bots;

public enum SettingType {

    INTEGRATION,
    DROPDOWN_AUTOMATIC,
    MULTIPLE_CHOICE,
    FIELDS_MAPPING,
    NOTIFICATION,
    CHOICE,
    TREE,
    MAPPING,
    TAGS,
    INPUT,
    MATCH;

    public String getType() {
        return name().toLowerCase();
    }
}
