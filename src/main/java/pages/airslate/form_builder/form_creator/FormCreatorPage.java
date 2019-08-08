package pages.airslate.form_builder.form_creator;

import pages.airslate.form_builder.FormBuilderBasePage;

import static com.codeborne.selenide.Selenide.page;

public class FormCreatorPage extends FormBuilderBasePage {

    public FormCreatorEditor getHtmlFormEditor() {
        switchToFrame();
        return page(FormCreatorEditor.class);
    }
}
