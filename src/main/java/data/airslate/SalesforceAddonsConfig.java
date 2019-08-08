package data.airslate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pages.airslate.addons.Operators;
import pages.airslate.flow_creator.EventBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class SalesforceAddonsConfig {

    private EventBuilder addon;
    private String[] selectValues;
    private List<Condition> conditions = new ArrayList<>();
    private List<FieldsPair> fieldPairs = new ArrayList<>();

    public SalesforceAddonsConfig(EventBuilder addon, String[] selectValues) {
        this.addon = addon;
        this.selectValues = selectValues;
    }

    public SalesforceAddonsConfig(EventBuilder addon, String[] selectValues, FieldsPair... fieldsPairs) {
        this(addon, selectValues);
        this.fieldPairs = Arrays.asList(fieldsPairs);
    }

    public SalesforceAddonsConfig(EventBuilder addon, String[] selectValues, List<Condition> conditions, FieldsPair... fieldsPairs) {
        this(addon, selectValues, fieldsPairs);
        this.conditions = conditions;
    }

    public void setPair(FieldsPair... fieldsPairs) {
        this.fieldPairs = Arrays.asList(fieldsPairs);
    }

    @AllArgsConstructor
    public enum PairType {
        MATCHING("matching"),
        MAPPING("mapping"),
        TRANSFER("transfer");

        private final String type;
    }

    @Getter
    @AllArgsConstructor
    public static class FieldsPair {
        private String objFieldName;
        private String documentName;
        private String documentFieldName;
        private PairType pairType;
    }

    @Getter
    @AllArgsConstructor
    public static class Condition {
        private String[] fields;
        private Operators operator;
        private String[] values;
    }
}