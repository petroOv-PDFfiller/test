package pages.salesforce.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ButtonNames {

    BACK_TO_LIST_VIEW("Back to List View"),
    SHOW_ALL_REVISIONS("Show all revisions");

    @Getter
    private String name;
}
