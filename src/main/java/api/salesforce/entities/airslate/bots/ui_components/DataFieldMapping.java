package api.salesforce.entities.airslate.bots.ui_components;

public class DataFieldMapping {

    public Field field;
    public Match match;

    public DataFieldMapping() {
    }

    public DataFieldMapping(Field field, Match match) {
        this.field = field;
        this.match = match;
    }

    public class Field {
        public String label;
        public String value;

        public Field() {
        }

        public Field(String label, String value) {
            this.label = label;
            this.value = value;
        }
    }

    public class Match {
        public Data data;
        public String type;

        public Match() {
        }

        public Match(String label, String value, String type) {
            this.data = new Data(label, value);
            this.type = type;
        }

        public class Data {
            public String label;
            public String value;

            public Data() {
            }

            public Data(String label, String value) {
                this.label = label;
                this.value = value;
            }
        }
    }
}
