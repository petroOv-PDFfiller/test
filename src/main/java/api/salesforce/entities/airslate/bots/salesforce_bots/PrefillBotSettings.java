package api.salesforce.entities.airslate.bots.salesforce_bots;

import api.salesforce.entities.airslate.bots.AirSlateBotSettings;
import com.airslate.api.models.addons.AddonEnum;
import com.airslate.api.models.addons.EventType;

public class PrefillBotSettings extends AirSlateBotSettings {

    public PrefillBotSettings() {
    }

    @Override
    public EventType defaultEventType() {
        return EventType.PRE_FILL;
    }

    @Override
    public AddonEnum defaultAddonEnum() {
        return AddonEnum.PRE_FILL_FROM_SALESFORCE_RECORD;
    }

    @Override
    public String defaultEventSubType() {
        return null;
    }
}