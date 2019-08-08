package api.salesforce.responses;


import com.fasterxml.jackson.annotation.JsonProperty;

public class FindRecordsIDResponse {

    public int totalSize;
    public boolean done;
    public Record[] records;

    public static class Record {

        public Attributes attributes;
        public String Id;
        @JsonProperty("NamespacePrefix")
        public String namespacePrefix;

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