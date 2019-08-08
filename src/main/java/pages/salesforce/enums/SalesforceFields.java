package pages.salesforce.enums;

public enum SalesforceFields {
    LOOKUP_RELATIONSHIP("Lookup Relationship"),
    TEXT("Text"),
    TEXT_AREA("Text Area"),
    TEXT_AREA_LONG("Text Area (Long)"),
    TEXT_AREA_RICH("Text Area (Rich)"),
    NUMBER("Number"),
    CURRENCY("Currency"),
    FORMULA("Formula");

    private String description;

    SalesforceFields(String description) {
        this.description = description;
    }

    public String getName() {
        return description;
    }
}
