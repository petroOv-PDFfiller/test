package pages.salesforce.enums.V3;

import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;
import pages.salesforce.app.DaDaDocs.full_app.V3.PrintDocumentPage;
import pages.salesforce.app.DaDaDocs.full_app.V3.pop_ups.*;
import pages.salesforce.app.DaDaDocs.full_app.templates.CreateTemplateWizardPage;

public enum DocumentTabV3Actions {

    RENAME("Rename", DocumentRenamePopUp.class),
    DELETE("Delete", DocumentDeletePopUp.class),
    CREATE_TEMPLATE("Create template", CreateTemplateWizardPage.class),
    EDIT_DOCUMENT("Edit document", DaDaDocsEditor.class),
    LINK_TO_FILL("LinkToFill", LinkToFillPopUp.class),
    SHARE_FOR_EDITING("Share for editing", ShareForEditingPopUp.class),
    PRINT("Print", PrintDocumentPage.class),
    SEND_BY_EMAIL("Send by email", SendByEmailPopUp.class),
    MERGE_DOCUMENTS("Merge documents", MergePopUp.class),
    SEND_TO_SIGN("SendToSign", SendToSignPopUp.class);

    private String action;
    private Object page;

    DocumentTabV3Actions(String action, Object page) {
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
