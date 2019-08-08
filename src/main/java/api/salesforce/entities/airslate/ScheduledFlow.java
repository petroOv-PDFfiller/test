package api.salesforce.entities.airslate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import utils.JsonMan;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduledFlow {
    private String id;
    private String jobId;
    private String scheduleId;
    private String name;
    private Flow flow;
    private TimeParams timeParams;
    private String orderBy;
    private String order;
    private Params params;
    private Option option;
    private String lastModifiedDate;
    private String lastModifiedByEmail;
    private String createdDate;
    private String createdByEmail;
    @JsonProperty("isActive")
    private boolean isActive;

    @Override
    public String toString() {
        return JsonMan.convertPojoToJsonPrettyString(this);
    }

    public enum Option {
        SOQL_QUERY("query"),
        SF_REPORT("report");

        @Getter
        private String optionName;

        Option(String optionName) {
            this.optionName = optionName;
        }

        @Override
        public String toString() {
            return optionName;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class Flow {
        private @Getter
        String id, name;

        public ScheduledFlow.Flow setId(String flowId) {
            this.id = flowId;
            return this;
        }

        public ScheduledFlow.Flow setName(String flowName) {
            this.name = flowName;
            return this;
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Params {
        private String query;
        private Report report;

        public ScheduledFlow.Params setQuery(String query) {
            this.query = query;
            return this;
        }

        public ScheduledFlow.Params setReport(ScheduledFlow.Report report) {
            this.report = report;
            return this;
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Report {
        @JsonProperty("id")
        private String reportId;
        @JsonProperty("name")
        private String reportName;


        public ScheduledFlow.Report setReportId(String reportId) {
            this.reportId = reportId;
            return this;
        }

        public ScheduledFlow.Report setReportName(String reportName) {
            this.reportName = reportName;
            return this;
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TimeParams {
        private String type, hh, mm, tt;
        private String[] ddd;
    }
}
