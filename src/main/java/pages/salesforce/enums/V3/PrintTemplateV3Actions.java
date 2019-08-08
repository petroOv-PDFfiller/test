package pages.salesforce.enums.V3;

import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups.LinkToFillPopUp;
import pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups.SendByEmailPopUp;
import pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups.SendToSignPopUp;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.TemplatesTab;
import pages.salesforce.app.DaDaDocs.full_app.pop_ups.TemplateInfoPopUp;
import pages.salesforce.app.DaDaDocs.full_app.templates.CreateTemplateWizardPage;

public enum PrintTemplateV3Actions {

    BACK_TO_TEMPLATES("Back to Templates", TemplatesTab.class),
    GENERATE_DOCUMENT("Generate document", DaDaDocsEditor.class),
    SEND_TO_SIGN("SendToSign", SendToSignPopUp.class),
    LINK_TO_FILL("LinkToFill", LinkToFillPopUp.class),
    SEND_BY_EMAIL("Send by email", SendByEmailPopUp.class),
    EDIT_TEMPLATE("Edit template", CreateTemplateWizardPage.class),
    CREATE_SHORTCUT_BUTTON("Create shortcut button", null),
    TEMPLATE_INFO("Template info", TemplateInfoPopUp.class),
    DELETE("Delete", null);

    private String action;
    private Object page;

    PrintTemplateV3Actions(String action, Object page) {
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
