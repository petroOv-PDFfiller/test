package pages.salesforce.app.DaDaDocs.full_app.V3.entities;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Template {

    private String name;
    private List<String> tags;
    private Date modifiedDate;
    private String id;

    public Template(String name, List<String> tags, Date modifiedDate, String id) {
        this.name = name;
        this.tags = tags;
        this.modifiedDate = modifiedDate;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
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
        if (!(o instanceof Template)) return false;
        Template template = (Template) o;
        return Objects.equals(name, template.name) &&
                Objects.equals(tags, template.tags) &&
                Objects.equals(modifiedDate, template.modifiedDate) &&
                Objects.equals(id, template.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tags, modifiedDate, id);
    }
}
