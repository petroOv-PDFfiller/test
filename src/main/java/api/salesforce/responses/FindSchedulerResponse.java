package api.salesforce.responses;

import com.fasterxml.jackson.annotation.JsonAlias;

public class FindSchedulerResponse {

    public int totalSize;
    public boolean done;
    public FindSchedulerResponse.Record[] records;

    public static class Record {
        public FindSchedulerResponse.Attributes attributes;

        public String Id;

        @JsonAlias({"pdffiller_sfree__Name__c", "Name__c"})
        public String schedulerName;

        @JsonAlias({"pdffiller_sfree__Date__c", "Date__c"})
        public String date;

        @JsonAlias({"pdffiller_sfree__Params__c", "Params__c"})
        public String params;

        @JsonAlias({"pdffiller_sfree__Option__c", "Option__c"})
        public String option;

        @JsonAlias({"pdffiller_sfree__Query__c", "Query__c"})
        public String query;

        @JsonAlias({"pdffiller_sfree__FlowName__c", "FlowName__c"})
        public String flowName;

        @JsonAlias({"pdffiller_sfree__FlowId__c", "FlowId__c"})
        public String flowId;

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
