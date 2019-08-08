package api.salesforce.entities.airslate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import utils.JsonMan;

@Data
public class ObjectsView {
    private String fullName;
    private String order;
    private String label;
    private String ns;

    @Override
    public String toString() {
        return JsonMan.convertPojoToJsonPrettyString(this);
    }

    @AllArgsConstructor
    public enum View {
        LAYOUTS("layouts"),
        LIST_VIEW("objectsService");

        @Getter
        private String viewName;

        @Override
        public String toString() {
            return viewName;
        }
    }
}
