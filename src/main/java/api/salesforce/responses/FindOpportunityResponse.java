package api.salesforce.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

public class FindOpportunityResponse extends FindRecordsIDResponse {

    public List<OpportunityRecord> records;

    @Getter
    public static class OpportunityRecord extends Record {
        public @JsonProperty("Name")
        String name;
        public @JsonProperty("StageName")
        String stageName;
        public @JsonProperty("CloseDate")
        String closeDate;

        @JsonProperty("airSlate_Invoke_Process__c")
        public String airSlateInvokeProcess;
    }
}