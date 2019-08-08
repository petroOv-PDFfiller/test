package api.salesforce.responses;

import lombok.Data;
import utils.JsonMan;

@Data
public class ExecuteApexResponse {
    private int line;
    private int column;
    private boolean compiled;
    private boolean success;
    private String compileProblem;
    private String exceptionStackTrace;
    private String exceptionMessage;

    @Override
    public String toString() {
        return JsonMan.convertPojoToJsonPrettyString(this);
    }
}
