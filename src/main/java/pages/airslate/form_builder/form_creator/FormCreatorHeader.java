package pages.airslate.form_builder.form_creator;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import pages.SelenideComponent;
import pages.airslate.form_builder.enums.ElementName;

public class FormCreatorHeader extends SelenideComponent {

    public SelenideElement undoBtn = self().find("");
    public ElementsCollection buttons = self().findAll("button");

    public FormCreatorHeader(SelenideElement itself) {
        super(itself);
    }

    public SelenideElement button(ElementName elementName) {
        return buttons.findBy(Condition.text(elementName.text()));
    }
}
