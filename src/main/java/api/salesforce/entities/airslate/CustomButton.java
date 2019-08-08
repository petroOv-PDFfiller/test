package api.salesforce.entities.airslate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import utils.JsonMan;

import java.util.List;

@Data
public class CustomButton {
    private String id;
    private String buttonId;
    private String flowId;
    private String flowName;
    private String orderBy;
    private String order;
    private List<Object> objects;
    private List<Object> layouts;
    private String label;
    private String description;
    private CBParameters params;
    private Action action;
    private List<String> layoutsNew;
    private List<String> objectsNew;
    private String createdDateToSort;
    private String createdDate;
    private String lastModifiedDate;
    private String lastModifiedByEmail;
    private String createdByEmail;
    @JsonProperty("isActive")
    private boolean isActive;
    private String ownerEmail;

    @Override
    public String toString() {
        return JsonMan.convertPojoToJsonPrettyString(this);
    }

    public String buttonToCreateString() {
        return "{" +
                "label='" + label + '\'' +
                ", description='" + description + '\'' +
                ", params=" + params +
                ", action=" + action +
                ", layoutsNew=" + layoutsNew +
                ", objectsNew=" + objectsNew +
                '}';
    }

    @AllArgsConstructor
    public enum Action {
        RUN_FLOW("run_flow"),
        INVOKE_PROCESS("invoke_process");

        @Setter
        @Getter
        private String actionName;

        @Override
        public String toString() {
            return actionName;
        }
    }

    @AllArgsConstructor
    public enum Mode {
        DEFAULT("default"),
        SEND_SLATE("send_slate"),
        OPEN_SLATE("open_slate");

        @Getter
        private String modeName;

        @Override
        public String toString() {
            return modeName;
        }
    }

    @Data
    public class CBParameters {
        private String flowId;
        private String flowName;
        private Mode mode;

        @Override
        public String toString() {
            return "{" +
                    "\"flowId\":\"" + flowId + "\"" +
                    ", \"flowName\":\"" + flowName + "\"" +
                    ", \"mode\":\"" + mode + "\"" +
                    "}";
        }
    }
}
