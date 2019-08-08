package pages.salesforce.enums.admin_tools;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ASAppCustomButtonField {

    LABEL("Button label"),
    DESCRIPTION("Description"),
    FLOW("Run"),
    DATE("Created date");

    @Getter
    private String name;
}
