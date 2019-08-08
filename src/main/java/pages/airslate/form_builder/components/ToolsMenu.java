package pages.airslate.form_builder.components;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import pages.SelenideComponent;

import java.util.List;

public class ToolsMenu extends SelenideComponent {

    public ToolsMenu(SelenideElement itself) {
        super(itself);
    }

    public List<ToolGroup> toolGroups() {
        return initList(self().findAll(By.cssSelector("div[class|='Subtitle']")), ToolGroup.class);
    }
}
