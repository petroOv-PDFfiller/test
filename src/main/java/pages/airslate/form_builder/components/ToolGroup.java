package pages.airslate.form_builder.components;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import pages.SelenideComponent;

import static org.openqa.selenium.By.xpath;

public class ToolGroup extends SelenideComponent {

    public SelenideElement groupName = self();
    public ElementsCollection groupTools = self().find(xpath("./following-sibling::*[contains(@class, 'ElementsGroup')]")).findAll(xpath("./*[contains(@class, 'Element__Wrapper')]"));

    public ToolGroup(SelenideElement itself) {
        super(itself);
    }
}
