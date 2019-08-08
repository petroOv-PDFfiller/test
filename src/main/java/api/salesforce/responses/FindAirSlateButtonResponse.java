package api.salesforce.responses;

import com.fasterxml.jackson.annotation.JsonAlias;

public class FindAirSlateButtonResponse {

    public int totalSize;
    public boolean done;
    public FindAirSlateButtonResponse.Record[] records;

    public static class Record {
        public FindAirSlateButtonResponse.Attributes attributes;

        public String Id;

        @JsonAlias({"pdffiller_sfree__Action__c", "Action__c"})
        public String action;

        @JsonAlias({"pdffiller_sfree__FlowName__c", "FlowName__c"})
        public String flowName;

        @JsonAlias({"pdffiller_sfree__FlowId__c", "FlowId__c"})
        public String flowId;

        @JsonAlias({"pdffiller_sfree__IsActive__c", "IsActive__c"})
        public boolean isActive;

        @JsonAlias({"pdffiller_sfree__Params__c", "Params__c"})
        public String params;

        @JsonAlias({"pdffiller_sfree__Label__c", "Label__c"})
        public String label;

        public Record() {
        }
    }

    public static class Attributes {

        public String type;
        public String url;

        public Attributes() {
        }
    }
}
