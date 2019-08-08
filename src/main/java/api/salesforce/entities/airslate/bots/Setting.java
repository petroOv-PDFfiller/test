package api.salesforce.entities.airslate.bots;

import com.fasterxml.jackson.annotation.JsonInclude;

public class Setting {

    public Object data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String name;
    public String type;

    public Setting() {
    }

    public Setting(Object data, String name, String type) {
        this.data = data;
        this.name = name;
        this.type = type;
    }

    public Setting(Object data, String type) {
        this.data = data;
        this.type = type;
    }
}
