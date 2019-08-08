package api.salesforce.entities.airslate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import utils.JsonMan;

@Data
public class PackageSettings {
    private String apiUrl;
    @JsonProperty("isSFAdmin")
    private boolean isSFAdmin;
    private String packageVersion;
    private String subdomainUrl;
    private String installationUserEmail;

    @Override
    public String toString() {
        return JsonMan.convertPojoToJsonPrettyString(this);
    }

    @Data
    public class Metadata {
        @JsonProperty("isMetadataReady")
        private String isMetadataReady;
    }
}
