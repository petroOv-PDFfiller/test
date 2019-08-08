package pages.salesforce.enums.V3;

import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.AuditTrailTab;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.DocumentsTab;
import pages.salesforce.app.DaDaDocs.full_app.V3.tabs.TemplatesTab;

public enum DaDaDocsV3Tabs {

    DOCUMENTS_TAB("Documents", DocumentsTab.class),
    TEMPLATES_TAB("Templates", TemplatesTab.class),
    AUDIT_TRAIL_TAB("Audit Trail", AuditTrailTab.class);

    private String name;
    private Object page;

    DaDaDocsV3Tabs(String name, Object page) {
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
