package pages.salesforce.enums.lightning_component;

import pages.salesforce.app.DaDaDocs.DaDaDocsLightningComponent;
import pages.salesforce.app.DaDaDocs.editor.DaDaDocsEditor;

public enum DaDaDocsLightningComponentActions {

    EDIT_DOCUMENT("Edit Document", DaDaDocsEditor.class),
    SEND_TO_SIGN("SendToSign", DaDaDocsLightningComponent.class),
    LINK_TO_FILL("LinkToFill", DaDaDocsLightningComponent.class),
    SHARE("Share", DaDaDocsLightningComponent.class),
    SEND_BY_EMAIL("Send by E-mail", DaDaDocsLightningComponent.class);

    private String actionName;
    private Object page;

    DaDaDocsLightningComponentActions(String actionName, Object page) {
        this.actionName = actionName;
        this.page = page;
    }

    public String getActionName() {
        return actionName;
    }

    public <T extends Object> T getPage() {
        return (T) page;
    }
}
