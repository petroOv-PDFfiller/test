package pages.salesforce.app.sf_setup.custom_object.entities;

import org.openqa.selenium.By;

import java.util.Objects;

public class SchemaBuilderEditorElement {

    private String name;
    private By locator;

    public SchemaBuilderEditorElement(String name, By locator) {
        this.name = name;
        this.locator = locator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public By getLocator() {
        return locator;
    }

    public void setLocator(By locator) {
        this.locator = locator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SchemaBuilderEditorElement)) return false;
        SchemaBuilderEditorElement that = (SchemaBuilderEditorElement) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(locator, that.locator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, locator);
    }
}
