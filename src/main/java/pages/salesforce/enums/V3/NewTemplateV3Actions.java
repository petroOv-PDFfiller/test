package pages.salesforce.enums.V3;

import pages.salesforce.app.DaDaDocs.full_app.V3.NewTemplatePage;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.TemplatesTab;
import pages.salesforce.app.DaDaDocs.full_app.templates.CreateTemplateWizardPage;

public enum NewTemplateV3Actions {
    BACK_TO_TEMPLATES_LIST("Back to templates list", TemplatesTab.class),
    UPLOAD_DOCUMENT("Upload Document", NewTemplatePage.class),
    NEW_DOCX_TEMPLATE("New DocX template", CreateTemplateWizardPage.class),
    NEW_PPTX_TEMPLATE("New PptX template", CreateTemplateWizardPage.class),
    CREATE_TEMPLATE("Create template", CreateTemplateWizardPage.class);

    private String action;
    private Object page;

    NewTemplateV3Actions(String action, Object page) {
        this.action = action;
        this.page = page;
    }

    public String getAction() {
        return action;
    }

    public <T extends Object> T getExpectedPage() {
        return (T) page;
    }
}
