package api.salesforce.entities.airslate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import utils.JsonMan;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ResponseBody<T> {
    private ErrorType errorType;
    private String errorMessage;
    private T data;
    private String success;
    @JsonProperty("isLast")
    private boolean isLast;
    private int page;
    private int total;
    private int totalPages;
    private int count;
    private int limitSlates;
    private int allSlates;

    @Override
    public String toString() {
        return JsonMan.convertPojoToJsonPrettyString(this);
    }

    @AllArgsConstructor
    public enum ErrorType {
        BAD_REQUEST("BAD_REQUEST"),
        URL_NOT_FOUND("URL_NOT_FOUND"),
        INVALID_EMAIL("INVALID_EMAIL"),
        DUPLICATE_LOGIN("DUPLICATE_LOGIN"),
        NO_CREDENTIALS("NO_CREDENTIALS"),
        INVALID_CREDENTIALS("INVALID_CREDENTIALS"),
        NOT_ADMIN("NOT_ADMIN"),
        NO_AS_ACCESS_TOKEN("NO_AS_ACCESS_TOKEN");

        @Getter
        private String errorText;

        @Override
        public String toString() {
            return errorText;
        }
    }
}
