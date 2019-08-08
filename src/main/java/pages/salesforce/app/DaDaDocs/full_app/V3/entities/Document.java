package pages.salesforce.app.DaDaDocs.full_app.V3.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pages.salesforce.enums.FileTypes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode
@ToString
public class Document {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm");
    private String name;
    private List<String> tags;
    private Date modifiedDate;
    @Getter
    @Setter
    private FileTypes type;


    public Document(String name, List<String> tags, Date modifiedDate) {
        this.name = name;
        this.tags = tags;
        this.modifiedDate = modifiedDate;
        type = FileTypes.PDF;
    }

    public Document(String name, List<String> tags, Date modifiedDate, FileTypes type) {
        this.name = name;
        this.tags = tags;
        this.modifiedDate = modifiedDate;
        this.type = type;
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
}
