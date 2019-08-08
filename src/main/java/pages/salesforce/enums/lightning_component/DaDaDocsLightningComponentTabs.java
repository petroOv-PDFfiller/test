package pages.salesforce.enums.lightning_component;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
public enum DaDaDocsLightningComponentTabs {

    DOCUMENTS("Documents"),
    TEMPLATES("Templates");

    @Getter
    private String name;
}
