package api.salesforce.entities.airslate.bots.salesforce_bots;

import api.salesforce.entities.airslate.bots.AirSlateBotSettings;
import com.airslate.api.models.addons.AddonEnum;
import com.airslate.api.models.addons.EventType;

public class UpdateRecordBotSettings extends AirSlateBotSettings {

    public UpdateRecordBotSettings() {
    }

    @Override
    public EventType defaultEventType() {
        return EventType.POST_FINISH;
    }

    @Override
    public AddonEnum defaultAddonEnum() {
        return AddonEnum.UPDATE_SALESFORCE_RECORD;
    }

    @Override
    public String defaultEventSubType() {
        return null;
    }
}
