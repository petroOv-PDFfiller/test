package api.salesforce.entities.airslate;

import lombok.Data;
import utils.JsonMan;

@Data
public class FeedbackSurvey {
    private String subject, message;

    @Override
    public String toString() {
        return JsonMan.convertPojoToJsonPrettyString(this);
    }
}
