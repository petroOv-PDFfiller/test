package api.salesforce.entities.airslate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import utils.JsonMan;

@Data
@AllArgsConstructor
public class WorkspaceConfig {
    private String workspaceId;
    @JsonProperty("workspaceSubdomain")
    private String workspaceSubdomain;

    @Override
    public String toString() {
        return JsonMan.convertPojoToJsonPrettyString(this);
    }
}
