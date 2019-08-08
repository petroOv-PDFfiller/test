package pages.salesforce.enums.admin_tools;

public enum AdminToolFeatureSettings {

    USE_CUSTOM_TOKEN_MANAGEMENT("Use custom token management"),
    USE_CHATTER_FILES_PRIOR_TO_ATTACHMENTS("Use Chatter Files Prior To Attachments"),
    STORE_FILES_TO_EXTERNAL_STORAGE("Store files to external storage"),
    GET_YOUR_DOCUMENTS_SIGNED_WITH_SIGN_NOW("Get your documents signed with SignNow");

    private String settingName;

    AdminToolFeatureSettings(String settingName) {
        this.settingName = settingName;
    }

    public String getName() {
        return settingName;
    }
}