package pages.salesforce.enums.V3;

public enum Tags {

    SEND_TO_SIGN("SendToSign"),
    DECLINED_SIGNING("Declined signing"),
    SIGN_PENDING("Sign pending"),
    UPLOADED("Uploaded"),
    FILL_PENDING("Fill pending"),
    LINK_TO_FILL("LinkToFill"),
    FILLED("Filled"),
    SIGNED("Signed");

    private String name;

    Tags(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
