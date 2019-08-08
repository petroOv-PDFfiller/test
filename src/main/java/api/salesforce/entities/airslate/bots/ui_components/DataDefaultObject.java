package api.salesforce.entities.airslate.bots.ui_components;

public class DataDefaultObject {

    public String label;
    public String value;

    public DataDefaultObject() {
    }

    public DataDefaultObject(String label, String value) {
        this.value = value;
        this.label = label;
    }
}
