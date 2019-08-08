package pages.airslate.form_builder.form_creator;

import pages.airslate.form_builder.components.ToolsMenu;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static pages.SelenideComponent.initList;

public class FormCreatorEditor {

    public FormCreatorHeader formCreatorHeader = new FormCreatorHeader($("header"));

    public FormCreatorPage getFormCreatorPage() {
        switchTo().defaultContent();
        return page(FormCreatorPage.class);
    }

    public List<ToolsMenu> toolsMenus() {
        return initList($$("aside"), ToolsMenu.class);
    }
}
