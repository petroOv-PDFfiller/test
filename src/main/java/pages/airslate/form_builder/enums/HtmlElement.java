package pages.airslate.form_builder.enums;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.replace;


public enum HtmlElement implements ElementName {

    SINGLE_LINE_TEXT, UNDO;

    @Override
    public String text() {
        return replace(capitalize(this.name()), "_", " ");
    }
}
