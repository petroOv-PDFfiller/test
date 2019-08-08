package pages.salesforce.enums.V3;

import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.DaDaDocs.full_app.V3.NewTemplatePage;
import pages.salesforce.app.DaDaDocs.full_app.V3.PrintTemplatePage;
import pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups.*;
import pages.salesforce.app.DaDaDocs.full_app.templates.CreateTemplateWizardPage;

public enum TemplateTabV3Actions {

    NEW_TEMPLATE("New template", NewTemplatePage.class),
    GENERATE_DOCUMENT("Generate document", DaDaDocsEditor.class),
    SEND_TO_SIGN("SendToSign", SendToSignPopUp.class),
    LINK_TO_FILL("LinkToFill", LinkToFillPopUp.class),
    SEND_BY_EMAIL("Send by email", SendByEmailPopUp.class),
    PRINT("Print", PrintTemplatePage.class),
    EDIT_TEMPLATE("Edit template", CreateTemplateWizardPage.class),
    DUPLICATE_TEMPLATE("Duplicate template", DuplicateTemplatePopUp.class),
    TEMPLATE_INFO("Template info", TemplateInfoV3PopUp.class),
    RENAME("Rename", RenameTemplatePopUp.class),
    DELETE("Delete", DeleteTemplatePopUp.class);

    private String action;
    private Object page;

    TemplateTabV3Actions(String action, Object page) {
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
