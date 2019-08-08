package api.salesforce.entities.airslate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import utils.JsonMan;

@Data
public class SetupWizardStage {
    private Stage stage;

    @Override
    public String toString() {
        return JsonMan.convertPojoToJsonPrettyString(this);
    }

    @AllArgsConstructor
    public enum Stage {
        AUTHORIZATION("Authorization"),
        WORKSPACE("Workspace"),
        TEAMMATES("Teammates"),
        FINAL("Final");

        @Getter
        private String stageName;

        @Override
        public String toString() {
            return stageName;
        }
    }
}
