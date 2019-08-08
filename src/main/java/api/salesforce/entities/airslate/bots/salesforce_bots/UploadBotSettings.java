package api.salesforce.entities.airslate.bots.salesforce_bots;

import api.salesforce.entities.airslate.bots.AirSlateBotSettings;
import com.airslate.api.models.addons.AddonEnum;
import com.airslate.api.models.addons.EventType;

public class UploadBotSettings extends AirSlateBotSettings {

    public UploadBotSettings() {
    }

    @Override
    public EventType defaultEventType() {
        return EventType.POST_FINISH;
    }

    @Override
    public AddonEnum defaultAddonEnum() {
        return AddonEnum.EXPORT_TO_SALESFORCE;
    }

    @Override
    public String defaultEventSubType() {
        return null;
    }
}
