package api.salesforce.entities.airslate.bots.salesforce_bots;

import api.salesforce.entities.airslate.bots.AirSlateBotSettings;
import com.airslate.api.models.addons.AddonEnum;
import com.airslate.api.models.addons.EventType;

public class InvokeBotSettings extends AirSlateBotSettings {

    public InvokeBotSettings() {
    }

    @Override
    public EventType defaultEventType() {
        return EventType.POST_FINISH;
    }

    @Override
    public AddonEnum defaultAddonEnum() {
        return AddonEnum.INVOKE_SALESFORCE_PROCESS_ADD_ON_NAME;
    }

    @Override
    public String defaultEventSubType() {
        return null;
    }
}
