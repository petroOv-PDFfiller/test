package api.salesforce.entities.airslate.bots.ui_components;

import java.util.ArrayList;
import java.util.List;

public class DataTreeObject {

    public String value;
    public String label;
    public List<DataTreeObject> children;

    public DataTreeObject() {
    }

    public DataTreeObject(String value, String label, List<DataTreeObject> children) {
        this.value = value;
        this.label = label;
        this.children = children == null ? new ArrayList<>() : children;
    }
}
