package api.salesforce.entities.airslate.bots.salesforce_bots;

import api.salesforce.entities.airslate.bots.AirSlateBotSettings;
import com.airslate.api.models.addons.AddonEnum;
import com.airslate.api.models.addons.EventType;

public class SendASlateBotSettings extends AirSlateBotSettings {

    public SendASlateBotSettings() {
    }

    @Override
    public EventType defaultEventType() {
        return EventType.POST_FINISH;
    }

    @Override
    public AddonEnum defaultAddonEnum() {
        return AddonEnum.SEND_A_SLATE_TO_SALESFORCE_CONTACT;
    }

    @Override
    public String defaultEventSubType() {
        return null;
    }
}
