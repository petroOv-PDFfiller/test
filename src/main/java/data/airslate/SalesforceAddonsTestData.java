package data.airslate;

import lombok.Getter;
import pages.airslate.flow_creator.EventBuilder;

import java.util.Arrays;

import static pages.airslate.addons.AddonEvents.PostFill.SF_CREATE;
import static pages.airslate.addons.AddonEvents.PostFill.SF_UPDATE;
import static pages.airslate.addons.Operators.Text.NOT_EMPTY;

@Getter
public class SalesforceAddonsTestData {

    protected String docName;

    public SalesforceAddonsConfig getCreateConfig(String docName) {
        return new SalesforceAddonsConfig(
                EventBuilder.postFill().addon(SF_CREATE),
                new String[]{"Opportunity"},
                Arrays.asList(new SalesforceAddonsConfig.Condition(new String[]{"Text_2", "Text_3"}, NOT_EMPTY, new String[]{})),
                new SalesforceAddonsConfig.FieldsPair("Name", docName, "Text_2", SalesforceAddonsConfig.PairType.MAPPING),
                new SalesforceAddonsConfig.FieldsPair("Stage", docName, "Dropdown_1", SalesforceAddonsConfig.PairType.MAPPING),
                new SalesforceAddonsConfig.FieldsPair("Close Date", docName, "Text_3", SalesforceAddonsConfig.PairType.MAPPING)
        );
    }

    public SalesforceAddonsConfig getUpdateConfig(boolean isContext, String docName) {
        return new SalesforceAddonsConfig(
                EventBuilder.postFill().addon(SF_UPDATE),
                new String[]{"Opportunity"},
                Arrays.asList(new SalesforceAddonsConfig.Condition(new String[]{"Text_3"}, NOT_EMPTY, new String[]{})),
                new SalesforceAddonsConfig.FieldsPair("Name", docName, "Text_2", isContext ? SalesforceAddonsConfig.PairType.MAPPING : SalesforceAddonsConfig.PairType.MATCHING),
                new SalesforceAddonsConfig.FieldsPair("Stage", docName, "Dropdown_1", SalesforceAddonsConfig.PairType.MAPPING),
                new SalesforceAddonsConfig.FieldsPair("Close Date", docName, "Text_3", SalesforceAddonsConfig.PairType.MAPPING)
        );
    }

    @Getter
    public enum Environments {
        RC("51B66896-0000-0000-0000BA29", "20C960D6-0000-0000-0000BA29"),
        PROD("F00B20A1-1000-0000-0000BA29", "7F9840A1-1000-0000-0000BA29");

        private String createFlowID;
        private String updateFlowID;

        Environments(String createFlowID, String updateFlowID) {
            this.createFlowID = createFlowID;
            this.updateFlowID = updateFlowID;
        }
    }
}