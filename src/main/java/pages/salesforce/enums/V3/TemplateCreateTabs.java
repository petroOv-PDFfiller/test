package pages.salesforce.enums.V3;

import pages.salesforce.app.DaDaDocs.full_app.templates.tabs.*;
import pages.salesforce.app.DaDaDocs.full_app.templates.tabs.x_templates.*;

public enum TemplateCreateTabs {
    TEMPLATE_X_INFO("Template info", XTemplateInfoPage.class),
    RELATED_PARENT("Related parent", RelatedParentPage.class),
    RELATED_CHILD("Related child", RelatedChildPage.class),
    COPY_TAGS("Copy tags", CopyTagsPage.class),
    TEMPLATE_UPLOAD("Template upload", TemplateUploadPage.class),
    TEMPLATE_INFO("Template info", TemplateInfoPage.class),
    ADD_FILLABLE_FIELDS("Add fillable fields", TemplateAddFieldsPage.class),
    MAP_FIELDS_TO_PREFILL("Map fields to prefill", TemplateMapToPrefillPage.class),
    MAP_FIELDS_TO_UPDATE("Map fields to update", TemplateMapToUpdatePage.class),
    MAP_FIELDS_TO_CREATE("Map fields to create", TemplateMapToCreatePage.class),
    TEMPLATE_X_ACCESS_SETTINGS("Access settings", XTemplateAccessSettingsPage.class),
    ACCESS_SETTINGS("Access settings", TemplateAccessSettingsPage.class);

    private String name;
    private Object page;

    TemplateCreateTabs(String name, Object page) {
        this.name = name;
        this.page = page;
    }

    public String getName() {
        return name;
    }

    public <T extends Object> T getExpectedPage() {
        return (T) page;
    }
}
