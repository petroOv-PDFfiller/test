package pages.salesforce.app.sf_setup.custom_object.entities;

import org.openqa.selenium.By;

import java.util.Objects;

public class SchemaBuilderCanvasObject {

    private String name;
    private By locator;
    private String id;

    public SchemaBuilderCanvasObject(String name, By locator, String id) {
        this.name = name;
        this.locator = locator;
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SchemaBuilderCanvasObject)) return false;
        SchemaBuilderCanvasObject that = (SchemaBuilderCanvasObject) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(locator, that.locator) &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, locator, id);
    }
}
