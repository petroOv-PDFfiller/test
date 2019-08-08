package pages.salesforce.enums;

import pages.salesforce.app.DaDaDocs.full_app.main_tabs.DocumentsPage;
import pages.salesforce.app.DaDaDocs.full_app.main_tabs.DocxTemplatePage;
import pages.salesforce.app.DaDaDocs.full_app.main_tabs.FileStatusPage;
import pages.salesforce.app.DaDaDocs.full_app.main_tabs.TemplatesPage;

public enum SaleforceMyDocsTab {

    DOCUMENTS("documents", DocumentsPage.class),
    TEMPLATES("templates", TemplatesPage.class),
    DOCX_TEMPLATE("docx-template", DocxTemplatePage.class),
    FILE_STATUS("fileStatus", FileStatusPage.class);

    private String name;
    private Object expectedPage;

    SaleforceMyDocsTab(String name, Object expectedPage) {
        this.name = name;
        this.expectedPage = expectedPage;
    }

    public String getName() {
        return name;
    }

    public <T extends Object> T getExpectedPage() {
        return (T) expectedPage;
    }
}
