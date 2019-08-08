package api.salesforce.entities.airslate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import utils.JsonMan;

@Data
public class Credentials {
    private String accessToken;
    private String workspaceSubdomain;
    private String workspaceId;
    private String apiUrl;
    private String login;
    @JsonProperty("isInstallationUser")
    private boolean isInstallationUser;

    @Override
    public String toString() {
        return JsonMan.convertPojoToJsonPrettyString(this);
    }
}
