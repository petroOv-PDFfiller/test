package api.salesforce.entities.airslate;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import utils.JsonMan;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SlateActivityResponse {
    private int limitSlates;
    private int allSlates;
    private List<Slate> data = new ArrayList<>();

    @Override
    public String toString() {
        return JsonMan.convertPojoToJsonPrettyString(this);
    }
}
