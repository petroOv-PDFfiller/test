package pages.salesforce.app.sf_setup.custom_object.entities;

import java.util.Objects;

public class SchemaBuilderEditorObject {

    private String name;
    private boolean active;

    public SchemaBuilderEditorObject(String name, boolean active) {
        this.name = name;
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SchemaBuilderEditorObject)) return false;
        SchemaBuilderEditorObject that = (SchemaBuilderEditorObject) o;
        return active == that.active &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, active);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
