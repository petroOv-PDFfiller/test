package pages.airslate.form_builder;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;

public class FormBuilderBasePage {

    public void switchToFrame() {
        switchTo().frame($(".iframe-wrap iframe"));
    }
}
