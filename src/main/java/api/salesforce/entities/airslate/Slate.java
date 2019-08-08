package api.salesforce.entities.airslate;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import utils.JsonMan;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Slate {
    private String flowId;
    private String flowName;
    private List<String> recordIds;
    private String objectName;
    private String recordName;
    private String customButtonName;
    private CustomButton.Action actionName;
    private String recordId;
    private String slateId;
    private String activityDate;
    private String activityDateTimestamp;

    @Override
    public String toString() {
        return JsonMan.convertPojoToJsonPrettyString(this);
    }
}
