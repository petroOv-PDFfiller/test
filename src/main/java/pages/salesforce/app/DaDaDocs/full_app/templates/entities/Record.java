package pages.salesforce.app.DaDaDocs.full_app.templates.entities;

import java.util.List;
import java.util.Objects;

public class Record {
    private String name;
    private List<MapRow> maps;

    public Record(String name, List<MapRow> maps) {
        this.name = name;
        this.maps = maps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MapRow> getMaps() {
        return maps;
    }

    public void setMaps(List<MapRow> maps) {
        this.maps = maps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Record)) return false;
        Record record = (Record) o;
        return Objects.equals(name, record.name) &&
                Objects.equals(maps, record.maps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, maps);
    }
}
