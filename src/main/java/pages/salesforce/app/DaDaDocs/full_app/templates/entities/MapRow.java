package pages.salesforce.app.DaDaDocs.full_app.templates.entities;

import java.util.Objects;

public class MapRow {

    private String sfField;
    private String dField;

    public MapRow(String sfField, String dField) {
        this.sfField = sfField;
        this.dField = dField;
    }

    public String getSfField() {
        return sfField;
    }

    public void setSfField(String sfField) {
        this.sfField = sfField;
    }

    public String getdField() {
        return dField;
    }

    public void setdField(String dField) {
        this.dField = dField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MapRow)) return false;
        MapRow mapRow = (MapRow) o;
        return Objects.equals(sfField, mapRow.sfField) &&
                Objects.equals(dField, mapRow.dField);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sfField, dField);
    }
}
