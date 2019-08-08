package pages.salesforce.enums.V3;

import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups.DocumentDeletePopUp;
import pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups.LinkToFillPopUp;
import pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups.SendByEmailPopUp;
import pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups.SendToSignPopUp;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.DocumentsTab;
import pages.salesforce.app.DaDaDocs.full_app.templates.CreateTemplateWizardPage;

public enum PrintDocumentV3Actions {
    BACK_TO_DOCUMENTS("Back to Documents", DocumentsTab.class),
    EDIT_DOCUMENT("Edit document", DaDaDocsEditor.class),
    CREATE_TEMPLATE("Create template", CreateTemplateWizardPage.class),
    SEND_TO_SIGN("SendToSign", SendToSignPopUp.class),
    LINK_TO_FILL("LinkToFill", LinkToFillPopUp.class),
    SEND_BY_EMAIL("Send by email", SendByEmailPopUp.class),
    DELETE("Delete", DocumentDeletePopUp.class);

    private String action;
    private Object page;

    PrintDocumentV3Actions(String action, Object page) {
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
